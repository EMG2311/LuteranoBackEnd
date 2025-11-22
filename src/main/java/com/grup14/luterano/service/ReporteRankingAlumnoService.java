package com.grup14.luterano.service;

import com.grup14.luterano.response.reporteRankingAlumno.RankingAlumnosColegioResponse;
import com.grup14.luterano.response.reporteRankingAlumno.RankingAlumnosCursoResponse;
import com.grup14.luterano.response.reporteRankingAlumno.RankingTodosCursosResponse;

public interface ReporteRankingAlumnoService {

    RankingAlumnosCursoResponse rankingAlumnosPorCurso(Long cursoId, int anio, int top);

    RankingAlumnosColegioResponse rankingAlumnosColegio(int anio, int top);

    RankingTodosCursosResponse rankingTodosCursos(int anio, int top);
}