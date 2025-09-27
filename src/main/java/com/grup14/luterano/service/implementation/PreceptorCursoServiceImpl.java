package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.Preceptor;
import com.grup14.luterano.exeptions.PreceptorCursoException;
import com.grup14.luterano.mappers.CursoMapper;
import com.grup14.luterano.mappers.PreceptorMapper;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.repository.PreceptorRepository;
import com.grup14.luterano.response.Preceptor.PreceptorResponseList;
import com.grup14.luterano.response.preceptorCurso.PreceptorCursoResponse;
import com.grup14.luterano.service.PreceptorCursoService;
import org.springframework.stereotype.Service;

@Service
public class PreceptorCursoServiceImpl implements PreceptorCursoService {

    private final CursoRepository cursoRepository;
    private final PreceptorRepository preceptorRepository;

    public PreceptorCursoServiceImpl(CursoRepository cursoRepository,
                                     PreceptorRepository preceptorRepository) {
        this.cursoRepository = cursoRepository;
        this.preceptorRepository = preceptorRepository;
    }

    @Override
    public PreceptorCursoResponse asignarPreceptorACruso(Long idPreceptor, Long idCurso) {
        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new PreceptorCursoException("No existe el curso con id " + idCurso));

        Preceptor preceptor = preceptorRepository.findById(idPreceptor)
                .orElseThrow(() -> new PreceptorCursoException("No existe el preceptor con id " + idPreceptor));

        if (curso.getPreceptor() != null) {
            if (curso.getPreceptor().getId().equals(idPreceptor)) {
                throw new PreceptorCursoException("El curso ya tiene asignado este preceptor");
            } else {
                throw new PreceptorCursoException("El curso ya tiene asignado otro preceptor");
            }
        }

        curso.setPreceptor(preceptor);
        cursoRepository.save(curso);

        return PreceptorCursoResponse.builder()
                .preceptor(PreceptorMapper.toDto(preceptor))
                .curso(CursoMapper.toDto(curso))
                .mensaje("Preceptor asignado correctamente al curso")
                .build();
    }

    @Override
    public PreceptorCursoResponse desasignarPreceptorACurso(Long idPreceptor, Long idCurso) {
        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new PreceptorCursoException("No existe el curso con id " + idCurso));

        Preceptor preceptor = preceptorRepository.findById(idPreceptor)
                .orElseThrow(() -> new PreceptorCursoException("No existe el preceptor con id " + idPreceptor));

        if (curso.getPreceptor() == null) {
            throw new PreceptorCursoException("El curso no tiene ningún preceptor asignado");
        }

        if (!curso.getPreceptor().getId().equals(idPreceptor)) {
            throw new PreceptorCursoException("El curso no tiene asignado a este preceptor, sino a otro");
        }

        curso.setPreceptor(null);
        cursoRepository.save(curso);

        return PreceptorCursoResponse.builder()
                .preceptor(PreceptorMapper.toDto(preceptor))
                .curso(CursoMapper.toDto(curso))
                .mensaje("Preceptor desasignado correctamente del curso")
                .build();
    }
}
