package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.Preceptor;
import com.grup14.luterano.exeptions.PreceptorCursoException;
import com.grup14.luterano.mappers.CursoMapper;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.repository.PreceptorRepository;
import com.grup14.luterano.response.curso.CursoResponse;
import com.grup14.luterano.service.PreceptorCursoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreceptorCursoServiceImpl implements PreceptorCursoService {

    private final CursoRepository cursoRepository;
    private final PreceptorRepository preceptorRepository;

    @Override
    @Transactional
    public CursoResponse asignarPreceptorCurso(Long preceptorId, Long cursoId) {
        if (cursoId == null || preceptorId == null) {
            throw new PreceptorCursoException("Debe indicar cursoId y preceptorId.");
        }

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new PreceptorCursoException("Curso no encontrado (id=" + cursoId + ")."));

        Preceptor preceptor = preceptorRepository.findById(preceptorId)
                .orElseThrow(() -> new PreceptorCursoException("Preceptor no encontrado (id=" + preceptorId + ")."));

        if (curso.getPreceptor() != null) {
            throw new PreceptorCursoException("El curso ya tiene un preceptor asignado. Use reasignar si quiere cambiarlo.");
        }
        preceptor.addCurso(curso);
        curso.setPreceptor(preceptor);
        cursoRepository.save(curso);
        preceptorRepository.save(preceptor);
        return CursoResponse.builder()
                .curso(CursoMapper.toDto(curso))
                .code(0)
                .mensaje("Se asigno correctamente el preceptor")
                .build();
    }

    @Override
    @Transactional
    public CursoResponse desasignarPreceptorCurso(Long cursoId) {
        if (cursoId == null) {
            throw new PreceptorCursoException("Debe indicar el cursoId.");
        }

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new PreceptorCursoException("Curso no encontrado (id=" + cursoId + ")."));

        if (curso.getPreceptor() == null) {
            throw new PreceptorCursoException("El curso no tiene preceptor asignado.");
        }
        Preceptor preceptor = curso.getPreceptor();
        preceptor.removeCurso(curso);
        curso.setPreceptor(null);
        cursoRepository.save(curso);
        preceptorRepository.save(preceptor);
        System.out.println("paso");

        return CursoResponse.builder()
                .code(0)
                .mensaje("Preceptor desasignado correctamente.")
                .curso(CursoMapper.toDto(curso))
                .build();
    }
}
