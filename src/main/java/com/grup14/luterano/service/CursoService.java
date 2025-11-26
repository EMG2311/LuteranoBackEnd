
package com.grup14.luterano.service;

import com.grup14.luterano.request.curso.CursoRequest;
import com.grup14.luterano.request.curso.CursoUpdateRequest;
import com.grup14.luterano.request.curso.IntercambiarAulasRequest;
import com.grup14.luterano.response.curso.CursoResponse;
import com.grup14.luterano.response.curso.CursoResponseList;

public interface CursoService {

    CursoResponse crearCurso(CursoRequest cursoRequest);

    CursoResponse updateCurso(CursoUpdateRequest cursoUpdateRequest);

    CursoResponse deleteCurso(Long id);

    CursoResponse getCursoById(Long id);

    CursoResponseList listCursos();

    CursoResponseList listarCursosPorPreceptor(Long preceptorId);

    CursoResponseList listarCursosPorDocente(Long docenteId);
    CursoResponse intercambiarAulas(IntercambiarAulasRequest req);


}
