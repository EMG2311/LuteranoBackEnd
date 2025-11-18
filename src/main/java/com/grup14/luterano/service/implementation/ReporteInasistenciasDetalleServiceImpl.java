package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.asistencia.AsistenciaDetalleDto;
import com.grup14.luterano.dto.asistencia.InasistenciasAlumnoDetalleDto;
import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.AsistenciaAlumno;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import com.grup14.luterano.entities.enums.EstadoAsistencia;
import com.grup14.luterano.exeptions.AsistenciaException;
import com.grup14.luterano.repository.AlumnoRepository;
import com.grup14.luterano.repository.AsistenciaAlumnoRepository;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.response.asistenciaAlumno.ReporteInasistenciasDetalleResponse;
import com.grup14.luterano.service.ReporteInasistenciasDetalleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteInasistenciasDetalleServiceImpl implements ReporteInasistenciasDetalleService {

    private final CursoRepository cursoRepository;
    private final AlumnoRepository alumnoRepository;
    private final AsistenciaAlumnoRepository asistenciaAlumnoRepository;

    @Override
    @Transactional(readOnly = true)
    public ReporteInasistenciasDetalleResponse inasistenciasDetalle(Integer anio, Long cursoId, Long alumnoId) {
        if (anio == null) {
            throw new AsistenciaException("anio es requerido");
        }
        if (cursoId == null && alumnoId == null) {
            throw new AsistenciaException("Debe indicar cursoId o alumnoId");
        }

        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);

        List<InasistenciasAlumnoDetalleDto> filas = new ArrayList<>();

        if (alumnoId != null) {
            // reporte puntual por alumno
            Alumno alumno = alumnoRepository.findById(alumnoId)
                    .orElseThrow(() -> new AsistenciaException("Alumno no encontrado"));

            Curso curso = alumno.getCursoActual();

            List<AsistenciaAlumno> registros =
                    asistenciaAlumnoRepository.findDetallePorAlumnoEntreFechas(alumnoId, desde, hasta);

            filas.add(mapAlumnoConDetalle(alumno, curso, registros));

        } else {
            // reporte por curso (aula)
            Curso curso = cursoRepository.findById(cursoId)
                    .orElseThrow(() -> new AsistenciaException("Curso no encontrado"));

            // alumnos activos del curso
            List<Alumno> alumnos = alumnoRepository.findByCursoActual_IdAndEstadoNotIn(
                    curso.getId(),
                    List.of(EstadoAlumno.BORRADO, EstadoAlumno.EGRESADO, EstadoAlumno.EXCLUIDO_POR_REPETICION)
            );

            // registros de asistencia del curso en el rango
            List<AsistenciaAlumno> registros =
                    asistenciaAlumnoRepository.findDetallePorCursoEntreFechas(curso.getId(), desde, hasta);

            Map<Long, List<AsistenciaAlumno>> registrosPorAlumno = registros.stream()
                    .collect(Collectors.groupingBy(aa -> aa.getAlumno().getId()));

            // ordenar alumnos por curso/anio/division/apellido/nombre
            Collator coll = Collator.getInstance(new Locale("es", "AR"));
            coll.setStrength(Collator.PRIMARY);

            alumnos.sort(Comparator
                    .comparing(Alumno::getApellido, coll)
                    .thenComparing(Alumno::getNombre, coll));

            for (Alumno a : alumnos) {
                List<AsistenciaAlumno> regs = registrosPorAlumno.getOrDefault(a.getId(), List.of());
                filas.add(mapAlumnoConDetalle(a, curso, regs));
            }
        }

        return ReporteInasistenciasDetalleResponse.builder()
                .anio(anio)
                .cursoId(cursoId)
                .alumnoId(alumnoId)
                .filas(filas)
                .code(0)
                .mensaje("OK")
                .build();
    }

    private InasistenciasAlumnoDetalleDto mapAlumnoConDetalle(Alumno a, Curso curso, List<AsistenciaAlumno> registros) {
        if (curso == null && a.getCursoActual() != null) {
            curso = a.getCursoActual();
        }

        double total = 0.0;
        double just = 0.0;
        double noJust = 0.0;

        List<AsistenciaDetalleDto> detalles = new ArrayList<>();

        for (AsistenciaAlumno aa : registros) {
            EstadoAsistencia estado = aa.getEstado();
            double peso = calcularPeso(estado);
            boolean esJustificada = (estado == EstadoAsistencia.JUSTIFICADO
                    || estado == EstadoAsistencia.CON_LICENCIA);

            total += peso;
            if (esJustificada) {
                just += peso;
            } else {
                noJust += peso;
            }

            detalles.add(AsistenciaDetalleDto.builder()
                    .fecha(aa.getFecha())
                    .estado(estado)
                    .observacion(aa.getObservacion())
                    .build());
        }

        return InasistenciasAlumnoDetalleDto.builder()
                .alumnoId(a.getId())
                .dni(a.getDni())
                .apellido(a.getApellido())
                .nombre(a.getNombre())
                .cursoId(curso != null ? curso.getId() : null)
                .cursoEtiqueta(curso != null ? etiquetaCurso(curso) : null)
                .totalInasistencias(redondear1(total))
                .totalJustificadas(redondear1(just))
                .totalNoJustificadas(redondear1(noJust))
                .detalles(detalles)
                .build();
    }

    /**
     * Mismo criterio de ponderación que usás para LIBRE:
     * - AUSENTE / JUSTIFICADO / CON_LICENCIA / RETIRO → 1.0
     * - TARDE → 0.5
     * - resto → 0
     */
    private double calcularPeso(EstadoAsistencia estado) {
        if (estado == null) return 0.0;
        return switch (estado) {
            case AUSENTE, JUSTIFICADO, CON_LICENCIA, RETIRO -> 1.0;
            case TARDE -> 0.5;
            default -> 0.0;
        };
    }

    private static double redondear1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private static String etiquetaCurso(Curso c) {
        if (c == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append(c.getAnio()).append("°");
        if (c.getDivision() != null) {
            sb.append(" ").append(c.getDivision().name());
        }
        if (c.getNivel() != null) {
            sb.append(" - ").append(c.getNivel().name());
        }
        return sb.toString();
    }
}
