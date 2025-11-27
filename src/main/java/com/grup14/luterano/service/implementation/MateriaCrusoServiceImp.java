package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.materiaCurso.MateriaCursoDto;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.Materia;
import com.grup14.luterano.entities.MateriaCurso;
import com.grup14.luterano.exeptions.MateriaCursoException;
import com.grup14.luterano.mappers.MateriaCursoMapper;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.response.MateriaCurso.MateriaCursoListResponse;
import com.grup14.luterano.response.MateriaCurso.MateriaCursoResponse;
import com.grup14.luterano.service.MateriaCursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MateriaCrusoServiceImp implements MateriaCursoService {
        private final MateriaRepository materiaRepository;
        private final CursoRepository cursoRepository;
        private final MateriaCursoRepository materiaCursoRepository;
        private final DocenteRepository docenteRepository;
        private final com.grup14.luterano.repository.HorarioClaseModuloRepository horarioClaseModuloRepository;
        private final HistorialMateriaRepository historialMateriaRepository;
    @Override
    public MateriaCursoResponse asignarMateriasACurso(List<Long> materiaIds, Long cursoId) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new MateriaCursoException("Curso no encontrado"));

        List<Long> yaAsignadas = new ArrayList<>();

        for (Long materiaId : materiaIds) {
            Materia materia = materiaRepository.findById(materiaId)
                    .orElseThrow(() -> new MateriaCursoException("Materia con id " + materiaId + " no encontrada"));

            boolean existe = curso.getDictados().stream()
                    .anyMatch(mc -> mc.getMateria().getId().equals(materiaId));

            if (existe) {
                yaAsignadas.add(materiaId);
                continue;
            }

            MateriaCurso mc = MateriaCurso.builder()
                    .curso(curso)
                    .materia(materia)
                    .build();
            materiaCursoRepository.save(mc);
            curso.getDictados().add(mc);
        }

        cursoRepository.save(curso);

        String mensaje = yaAsignadas.isEmpty()
                ? "Materias asignadas correctamente"
                : "Algunas materias ya estaban asignadas: " + yaAsignadas;

        return MateriaCursoResponse.builder()
                .code(0)
                .mensaje(mensaje)
                .build();
    }


    @Override
    public MateriaCursoListResponse quitarMateriasDeCurso(List<Long> materiaIds, Long cursoId) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new MateriaCursoException("El curso con id " + cursoId + " no existe"));

        List<Long> noAsignadas = new ArrayList<>();
        List<MateriaCursoDto> eliminadas = new ArrayList<>();

        for (Long materiaId : materiaIds) {
            MateriaCurso materiaCurso = materiaCursoRepository.findByMateriaIdAndCursoId(materiaId, curso.getId())
                    .orElse(null);

            if (materiaCurso == null) {
                noAsignadas.add(materiaId);
                continue;
            }
            if (historialMateriaRepository.existsByMateriaCurso_Id(materiaCurso.getId())) {
                throw new MateriaCursoException(
                        "No se puede eliminar la asignación porque existen historiales de materia asociados. " +
                                "Solo se puede desasignar el docente o inactivar la materia."
                );
            }

            materiaCursoRepository.delete(materiaCurso);
            eliminadas.add(MateriaCursoMapper.toDto(materiaCurso));
        }

        String mensaje;
        if (noAsignadas.isEmpty()) {
            mensaje = "Materias eliminadas correctamente del curso";
        } else {
            mensaje = "Algunas materias no estaban asignadas al curso: " + noAsignadas;
        }

        return MateriaCursoListResponse.builder()
                .materiaCursoDtoLis(eliminadas)
                .code(0)
                .mensaje(mensaje)
                .build();
    }


    @Override
    public MateriaCursoListResponse listarMateriasDeCurso(Long cursoId) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new MateriaCursoException("El curso con id " + cursoId + " no existe"));

        List<MateriaCursoDto> materias = materiaCursoRepository.findByCursoIdAndMateria_ActivaTrue(curso.getId())
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
        if (materiaCurso.getDocente() != null) {
            throw new MateriaCursoException("La materia ya tiene un docente asignado: "
                    + materiaCurso.getDocente().getNombre());
        }
        materiaCurso.setDocente(docente);
        materiaCursoRepository.save(materiaCurso);

        return MateriaCursoResponse.builder()
                .materiaCursoDto(MateriaCursoMapper.toDto(materiaCurso))
                .code(0)
                .mensaje("Docente asignado correctamente")
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

        // Desasignar docente en todos los horarios de ese MateriaCurso
        var horarios = horarioClaseModuloRepository.findAllByMateriaCurso_Id(materiaCurso.getId());
        for (var h : horarios) {
            horarioClaseModuloRepository.delete(h);
        }

        return MateriaCursoResponse.builder()
                .materiaCursoDto(MateriaCursoMapper.toDto(materiaCurso))
                .code(0)
                .mensaje("Docente y horarios desasignados correctamente")
                .build();
    }
}
