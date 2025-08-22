package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.MateriaCursoDto;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.Materia;
import com.grup14.luterano.entities.MateriaCurso;
import com.grup14.luterano.exeptions.MateriaCursoException;
import com.grup14.luterano.mappers.MateriaCursoMapper;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.repository.DocenteRepository;
import com.grup14.luterano.repository.MateriaCursoRepository;
import com.grup14.luterano.repository.MateriaRepository;
import com.grup14.luterano.response.MateriaCurso.MateriaCursoListResponse;
import com.grup14.luterano.response.MateriaCurso.MateriaCursoResponse;
import com.grup14.luterano.service.MateriaCursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MateriaCrusoServiceImp implements MateriaCursoService {
    private final MateriaRepository materiaRepository;
    private final CursoRepository cursoRepository;
    private final MateriaCursoRepository materiaCursoRepository;
    private final DocenteRepository docenteRepository;

    @Override
    public MateriaCursoResponse asignarMateriaACurso(Long materiaId, Long cursoId) {
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new MateriaCursoException("La materia con id " + materiaId + " no existe"));

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new MateriaCursoException("El curso con id " + cursoId + " no existe"));

        if (materiaCursoRepository.existsByMateriaIdAndCursoId(materia.getId(), curso.getId())) {
            throw new MateriaCursoException("La materia ya está asignada al curso");
        }

        MateriaCurso materiaCurso = MateriaCurso.builder()
                .materia(materia)
                .curso(curso)
                .build();

        materiaCursoRepository.save(materiaCurso);

        return MateriaCursoResponse.builder()
                .materiaCursoDto(MateriaCursoMapper.toDto(materiaCurso))
                .code(0)
                .manesaje("Materia asignada correctamente al curso")
                .build();
    }

    @Override
    public MateriaCursoResponse quitarMateriaDeCurso(Long materiaId, Long cursoId) {
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new MateriaCursoException("La materia con id " + materiaId + " no existe"));

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new MateriaCursoException("El curso con id " + cursoId + " no existe"));

        MateriaCurso materiaCurso = materiaCursoRepository.findByMateriaIdAndCursoId(materia.getId(), curso.getId())
                .orElseThrow(() -> new MateriaCursoException("La materia no está asignada al curso"));

        materiaCursoRepository.delete(materiaCurso);

        return MateriaCursoResponse.builder()
                .materiaCursoDto(MateriaCursoMapper.toDto(materiaCurso))
                .code(0)
                .manesaje("Materia eliminada correctamente del curso")
                .build();
    }

    @Override
    public MateriaCursoListResponse listarMateriasDeCurso(Long cursoId) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new MateriaCursoException("El curso con id " + cursoId + " no existe"));

        List<MateriaCursoDto> materias = materiaCursoRepository.findByCursoId(curso.getId())
                .stream()
                .map(MateriaCursoMapper::toDto)
                .collect(Collectors.toList());

        return MateriaCursoListResponse.builder()
                .materiaCursoDtoLis(materias)
                .code(0)
                .mensaje("Materias listadas correctamente para el curso")
                .build();
    }

    @Override
    public MateriaCursoListResponse listarCursosDeMateria(Long materiaId) {
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new MateriaCursoException("La materia con id " + materiaId + " no existe"));

        List<MateriaCursoDto> cursos = materiaCursoRepository.findByMateriaId(materia.getId())
                .stream()
                .map(MateriaCursoMapper::toDto)
                .collect(Collectors.toList());

        return MateriaCursoListResponse.builder()
                .materiaCursoDtoLis(cursos)
                .code(0)
                .mensaje("Cursos listados correctamente para la materia")
                .build();
    }


    @Override
    public MateriaCursoResponse asignarDocente(Long materiaId, Long cursoId, Long docenteId) {
        materiaRepository.findById(materiaId)
            .orElseThrow(() -> new MateriaCursoException("La materia con id " + materiaId + " no existe"));

        cursoRepository.findById(cursoId)
                .orElseThrow(() -> new MateriaCursoException("El curso con id " + cursoId + " no existe"));

        MateriaCurso materiaCurso = materiaCursoRepository
                .findByMateriaIdAndCursoId(materiaId, cursoId)
                .orElseThrow(() -> new MateriaCursoException("La materia no está asignada a ese curso"));

        Docente docente = docenteRepository.findById(docenteId)
                .orElseThrow(() -> new MateriaCursoException("Docente no encontrado"));

        materiaCurso.setDocente(docente);
        materiaCursoRepository.save(materiaCurso);

        return MateriaCursoResponse.builder()
                .materiaCursoDto(MateriaCursoMapper.toDto(materiaCurso))
                .code(0)
                .manesaje("Docente asignado correctamente")
                .build();
    }

    @Override
    public MateriaCursoResponse desasignarDocente(Long materiaId, Long cursoId) {
        materiaRepository.findById(materiaId)
                .orElseThrow(() -> new MateriaCursoException("La materia con id " + materiaId + " no existe"));

        cursoRepository.findById(cursoId)
                .orElseThrow(() -> new MateriaCursoException("El curso con id " + cursoId + " no existe"));

        MateriaCurso materiaCurso = materiaCursoRepository
                .findByMateriaIdAndCursoId(materiaId, cursoId)
                .orElseThrow(() -> new MateriaCursoException("La materia no está asignada a ese curso"));

        materiaCurso.setDocente(null);
        materiaCursoRepository.save(materiaCurso);

        return MateriaCursoResponse.builder()
                .materiaCursoDto(MateriaCursoMapper.toDto(materiaCurso))
                .code(0)
                .manesaje("Docente desasignado correctamente")
                .build();
    }
}
