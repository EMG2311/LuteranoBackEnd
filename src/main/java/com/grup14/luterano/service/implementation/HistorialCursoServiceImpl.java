package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.HistorialCursoDto;
import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.CicloLectivo;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.HistorialCurso;
import com.grup14.luterano.exeptions.HistorialCursoException;
import com.grup14.luterano.mappers.HistorialCursoMapper;
import com.grup14.luterano.repository.AlumnoRepository;
import com.grup14.luterano.repository.CicloLectivoRepository;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.repository.HistorialCursoRepository;
import com.grup14.luterano.request.historialCursoRequest.HistorialCursoRequest;
import com.grup14.luterano.response.historialCurso.HistorialCursoResponse;
import com.grup14.luterano.response.historialCurso.HistorialCursoResponseList;
import com.grup14.luterano.service.HistorialCursoService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor@Service
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
}
