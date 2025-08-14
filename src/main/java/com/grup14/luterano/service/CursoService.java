package com.grup14.luterano.service;

import com.grup14.luterano.request.curso.CursoRequest;
import com.grup14.luterano.request.curso.CursoUpdateRequest;
import com.grup14.luterano.response.curso.CursoResponse;

public interface CursoService {

    CursoResponse crearCurso(CursoRequest cursoRequest);
    CursoResponse updateCurso(CursoUpdateRequest cursoUpdateRequest);
    CursoResponse deleteCurso(Long id);

}
