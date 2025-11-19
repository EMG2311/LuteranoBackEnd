package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.notaFinal.NotaFinalDetalleDto;
import com.grup14.luterano.dto.reporteNotas.CalificacionesMateriaResumenDto;
import com.grup14.luterano.entities.MesaExamenAlumno;
import com.grup14.luterano.entities.MateriaCurso;
import com.grup14.luterano.repository.MateriaCursoRepository;
import com.grup14.luterano.repository.MesaExamenAlumnoRepository;
import com.grup14.luterano.response.reporteNotas.CalificacionesAlumnoAnioResponse;
import com.grup14.luterano.service.NotaFinalService;
import com.grup14.luterano.service.ReporteNotasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotaFinalServiceImpl implements NotaFinalService {

    private final MesaExamenAlumnoRepository mesaExamenAlumnoRepo;
    private final ReporteNotasService reporteNotasService;
    private final MateriaCursoRepository materiaCursoRepository;

    @Override
    @Transactional(readOnly = true)
    public Integer calcularNotaFinal(Long alumnoId, Long materiaId, int anio) {
        NotaFinalDetalleDto detalle = obtenerNotaFinalDetallada(alumnoId, materiaId, anio);
        return detalle != null ? detalle.getNotaFinal() : null;
    }

    @Override
    @Transactional(readOnly = true)
    public NotaFinalDetalleDto obtenerNotaFinalDetallada(Long alumnoId, Long materiaId, int anio) {
        // 1. Buscar mesa de examen más reciente en el año
        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);

        List<MesaExamenAlumno> mesas = mesaExamenAlumnoRepo
                .findByAlumno_IdAndMesaExamen_FechaBetween(alumnoId, desde, hasta);

        MesaExamenAlumno mesaMasReciente = mesas.stream()
                .filter(mea -> mea.getMesaExamen().getMateriaCurso().getMateria().getId().equals(materiaId))
                .filter(mea -> mea.getNotaFinal() != null)
                .max((a, b) -> {
                    LocalDate fechaA = a.getMesaExamen().getFecha();
                    LocalDate fechaB = b.getMesaExamen().getFecha();
                    if (fechaA == null && fechaB == null) return 0;
                    if (fechaA == null) return -1;
                    if (fechaB == null) return 1;
                    return fechaA.compareTo(fechaB);
                })
                .orElse(null);

        // 2. Si tiene mesa de examen, usar esa nota (manda sobre todo)
        if (mesaMasReciente != null) {
            return NotaFinalDetalleDto.desdeMesa(
                    mesaMasReciente.getNotaFinal(),
                    mesaMasReciente.getMesaExamen().getId()
            );
        }

        // 3. Si no tiene mesa, calcular respetando la regla de etapas
        CalificacionesAlumnoAnioResponse reporteNotas = reporteNotasService.listarResumenPorAnio(alumnoId, anio);

        if (reporteNotas == null || reporteNotas.getCalificacionesAlumnoResumenDto() == null) {
            return null;
        }

        CalificacionesMateriaResumenDto materia = reporteNotas.getCalificacionesAlumnoResumenDto()
                .getMaterias().stream()
                .filter(m -> m.getMateriaId().equals(materiaId))
                .findFirst()
                .orElse(null);

        if (materia == null || materia.getPg() == null) {
            return null;
        }

        // 👉 NUEVO: usar las etapas para decidir APROBADA/DESAPROBADA
        Double e1 = materia.getE1(); // promedio etapa 1
        Double e2 = materia.getE2(); // promedio etapa 2

        // Si alguna etapa está desaprobada (< 6) → no hay nota final aprobada
        if ((e1 != null && e1 < 6.0) || (e2 != null && e2 < 6.0)) {
            // La materia queda DESAPROBADA si no fue a mesa
            // Devuelvo null para que promoción masiva la trate como desaprobada
            return NotaFinalDetalleDto.desdePromedio(null, materia.getPg());
        }

        // Si ambas etapas están aprobadas (>=6), usar PG redondeado
        Integer notaFinalRedondeada = (int) Math.round(materia.getPg());
        return NotaFinalDetalleDto.desdePromedio(notaFinalRedondeada, materia.getPg());
    }

    @Override
    @Transactional(readOnly = true)
    public int contarMateriasDesaprobadasPorAlumno(Long alumnoId, Long cursoId, int anio) {
        // Obtener todas las materias del curso en una sola consulta
        List<MateriaCurso> materiasCurso = materiaCursoRepository.findByCursoId(cursoId);
        
        if (materiasCurso.isEmpty()) {
            return 0;
        }

        // Obtener el reporte completo del alumno para el año (una sola consulta)
        CalificacionesAlumnoAnioResponse reporteNotas = reporteNotasService.listarResumenPorAnio(alumnoId, anio);
        
        if (reporteNotas == null || reporteNotas.getCalificacionesAlumnoResumenDto() == null) {
            // Si no hay reporte, todas las materias están desaprobadas
            return materiasCurso.size();
        }

        // Obtener todas las mesas de examen del alumno en el año (una sola consulta)
        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);
        List<MesaExamenAlumno> mesas = mesaExamenAlumnoRepo
                .findByAlumno_IdAndMesaExamen_FechaBetween(alumnoId, desde, hasta);

        // Contar materias desaprobadas
        int desaprobadas = 0;
        for (MateriaCurso mc : materiasCurso) {
            Long materiaId = mc.getMateria().getId();
            
            // 1. Buscar si tiene mesa de examen para esta materia
            MesaExamenAlumno mesaMasReciente = mesas.stream()
                    .filter(mea -> mea.getMesaExamen().getMateriaCurso().getMateria().getId().equals(materiaId))
                    .filter(mea -> mea.getNotaFinal() != null)
                    .max((a, b) -> {
                        LocalDate fechaA = a.getMesaExamen().getFecha();
                        LocalDate fechaB = b.getMesaExamen().getFecha();
                        if (fechaA == null && fechaB == null) return 0;
                        if (fechaA == null) return -1;
                        if (fechaB == null) return 1;
                        return fechaA.compareTo(fechaB);
                    })
                    .orElse(null);

            Integer notaFinal;
            if (mesaMasReciente != null) {
                // Usar nota de mesa
                notaFinal = mesaMasReciente.getNotaFinal();
            } else {
                // Buscar en el reporte de notas (PG redondeado)
                CalificacionesMateriaResumenDto materia = reporteNotas.getCalificacionesAlumnoResumenDto()
                        .getMaterias().stream()
                        .filter(m -> m.getMateriaId().equals(materiaId))
                        .findFirst()
                        .orElse(null);

                if (materia == null || materia.getPg() == null) {
                    notaFinal = null;
                } else {
                    notaFinal = (int) Math.round(materia.getPg());
                }
            }

            // Si la nota es null o menor a 6, está desaprobada
            if (notaFinal == null || notaFinal < 6) {
                desaprobadas++;
            }
        }

        return desaprobadas;
    }
}