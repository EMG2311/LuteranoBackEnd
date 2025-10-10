package com.grup14.luterano.service;

import com.grup14.luterano.response.curso.CursoResponse;

public interface PreceptorCursoService {
    CursoResponse asignarPreceptorCurso(Long preceptorId, Long cursoId);
    CursoResponse desasignarPreceptorCurso(Long cursoId);
}
