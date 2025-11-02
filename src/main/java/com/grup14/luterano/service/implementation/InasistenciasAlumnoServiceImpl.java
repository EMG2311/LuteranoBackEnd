package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.inasistenciasAlumno.InasistenciaAlumnoDto;
import com.grup14.luterano.entities.AsistenciaAlumno;
import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.enums.EstadoAsistencia;
import com.grup14.luterano.exeptions.AsistenciaException;
import com.grup14.luterano.mappers.CursoMapper;
import com.grup14.luterano.repository.AlumnoRepository;
import com.grup14.luterano.repository.AsistenciaAlumnoRepository;
import com.grup14.luterano.response.inasistenciasAlumno.InasistenciasAlumnoResponse;
import com.grup14.luterano.service.InasistenciasAlumnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InasistenciasAlumnoServiceImpl implements InasistenciasAlumnoService {

    private final AsistenciaAlumnoRepository asistenciaRepo;
    private final AlumnoRepository alumnoRepo;

    @Override
    @Transactional(readOnly = true)
    public InasistenciasAlumnoResponse listarInasistenciasPorAlumno(Long alumnoId) {
        Alumno alumno = alumnoRepo.findById(alumnoId)
                .orElseThrow(() -> new AsistenciaException("Alumno no encontrado con ID: " + alumnoId));

        return generarReporteInasistencias(alumno);
    }

    @Override
    @Transactional(readOnly = true)
    public InasistenciasAlumnoResponse listarInasistenciasPorDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("DNI es requerido");
        }

        Alumno alumno = alumnoRepo.findByDni(dni)
                .orElseThrow(() -> new AsistenciaException("Alumno no encontrado con DNI: " + dni));

        return generarReporteInasistencias(alumno);
    }

    private InasistenciasAlumnoResponse generarReporteInasistencias(Alumno alumno) {
        List<AsistenciaAlumno> asistencias = asistenciaRepo.findByAlumnoIdWithCurso(alumno.getId());

        List<InasistenciaAlumnoDto> inasistenciasDto = asistencias.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return InasistenciasAlumnoResponse.builder()
                .inasistencias(inasistenciasDto)
                .alumnoId(alumno.getId())
                .nombreCompleto(alumno.getApellido() + ", " + alumno.getNombre())
                .totalInasistencias(inasistenciasDto.size())
                .code(0)
                .mensaje("OK")
                .build();
    }

    private InasistenciaAlumnoDto mapToDto(AsistenciaAlumno asistencia) {
        // Obtener el día de la semana en español
        String diaSemana = asistencia.getFecha()
                .getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-AR"));

        // Determinar si está justificada basándose en el estado
        Boolean justificada = determinarJustificada(asistencia.getEstado());

        return InasistenciaAlumnoDto.builder()
                .asistenciaId(asistencia.getId())
                .fecha(asistencia.getFecha())
                .estado(asistencia.getEstado())
                .justificada(justificada)
                .observacion(asistencia.getObservacion())
                .curso(asistencia.getAlumno().getCursoActual() != null ? 
                       CursoMapper.toDto(asistencia.getAlumno().getCursoActual()) : null)
                .diaSemana(diaSemana)
                .build();
    }

    private Boolean determinarJustificada(EstadoAsistencia estado) {
        return switch (estado) {
            case JUSTIFICADO, CON_LICENCIA -> true;
            case AUSENTE, TARDE, RETIRO -> false;
            default -> null; // Para PRESENTE u otros estados
        };
    }
}