package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.AlumnoDto;
import com.grup14.luterano.dto.HistorialCursoDto;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.HistorialCurso;
import com.grup14.luterano.exeptions.HistorialCursoException;
import com.grup14.luterano.mappers.AlumnoMapper;
import com.grup14.luterano.mappers.CursoMapper;
import com.grup14.luterano.mappers.HistorialCursoMapper;
import com.grup14.luterano.repository.AlumnoRepository;
import com.grup14.luterano.repository.CicloLectivoRepository;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.repository.HistorialCursoRepository;
import com.grup14.luterano.response.CursoAlumno.CursoAlumnosResponse;
import com.grup14.luterano.response.historialCurso.HistorialCursoResponse;
import com.grup14.luterano.response.historialCurso.HistorialCursoResponseList;
import com.grup14.luterano.service.HistorialCursoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
public class HistorialCursoServiceImpl implements HistorialCursoService {

    private final HistorialCursoRepository historialCursoRepository;
    private final AlumnoRepository alumnoRepository;
    private final CursoRepository cursoRepository;
    private final CicloLectivoRepository cicloLectivoRepository;


    public HistorialCursoResponseList listarHistorialAlumnoFiltrado(Long alumnoId, Long cicloLectivoId, Long cursoId) {
        List<HistorialCurso> historiales;
        if (cicloLectivoId != null && cursoId != null) {
            historiales = historialCursoRepository.findByAlumno_IdAndCicloLectivo_IdAndCurso_Id(alumnoId, cicloLectivoId, cursoId);
        } else if (cicloLectivoId != null) {
            historiales = historialCursoRepository.findByAlumno_IdAndCicloLectivo_Id(alumnoId, cicloLectivoId);
        } else if (cursoId != null) {
            historiales = historialCursoRepository.findByAlumno_IdAndCurso_Id(alumnoId, cursoId);
        } else {
            historiales = historialCursoRepository.findByAlumno_Id(alumnoId);
        }
        List<HistorialCursoDto> dtoList = historiales.stream()
                .map(HistorialCursoMapper::toDto)
                .toList();

        return HistorialCursoResponseList.builder()
                .historialCursos(dtoList)
                .code(0)
                .mensaje("Historial filtrado correctamente")
                .build();
    }

    @Override
    public HistorialCursoResponse getHistorialCursoActual(Long alumnoId) {
        HistorialCurso historial = historialCursoRepository
                .findByAlumno_IdAndFechaHastaIsNull(alumnoId)
                .orElseThrow(() -> new HistorialCursoException("No se encontró historial actual del alumno"));

        return HistorialCursoResponse.builder()
                .code(0)
                .mensaje("Historial actual del alumno encontrado correctamente")
                .historialCurso(HistorialCursoMapper.toDto(historial))
                .build();
    }


    @Transactional(readOnly = true)
    public CursoAlumnosResponse listarAlumnosPorCurso(Long cursoId, Long cicloLectivoIdOpt) {

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new HistorialCursoException("Curso no encontrado (id=" + cursoId + ")"));

        Long cicloId;
        if (cicloLectivoIdOpt != null) {
            cicloId = cicloLectivoIdOpt;
            cicloLectivoRepository.findById(cicloId)
                    .orElseThrow(() -> new HistorialCursoException("Ciclo lectivo no encontrado (id=" + cicloId + ")"));
        } else {
            LocalDate hoy = java.time.LocalDate.now();
            cicloId = cicloLectivoRepository
                    .findByFechaDesdeBeforeAndFechaHastaAfter(hoy, hoy)
                    .orElseThrow(() -> new HistorialCursoException("No hay ciclo lectivo activo"))
                    .getId();
        }

        List<HistorialCurso> historiales = historialCursoRepository.findAbiertosByCursoAndCiclo(cursoId, cicloId);

        List<AlumnoDto> alumnos = historiales.stream()
                .map(HistorialCurso::getAlumno)
                .map(AlumnoMapper::toDto)
                .collect(java.util.stream.Collectors.toList());

        return CursoAlumnosResponse.builder()
                .curso(CursoMapper.toDto(curso))
                .alumnos(alumnos)
                .code(0)
                .mensaje("OK")
                .build();
    }
}
