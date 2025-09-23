package com.grup14.luterano.service;

import com.grup14.luterano.response.Preceptor.PreceptorResponseList;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.response.curso.CursoResponseList;
import com.grup14.luterano.response.preceptorCurso.PreceptorCursoResponse;

public interface PreceptorCursoService {
    PreceptorCursoResponse asignarPreceptorACruso(Long idPreceptor, Long idCurso);
    PreceptorCursoResponse desasignarPreceptorACurso(Long idPreceptor,Long idCurso);
}
