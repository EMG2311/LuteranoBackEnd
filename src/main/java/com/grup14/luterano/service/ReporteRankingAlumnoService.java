package com.grup14.luterano.service;

import com.grup14.luterano.response.reporteRankingAlumno.RankingAlumnosCursoResponse;
import com.grup14.luterano.response.reporteRankingAlumno.RankingAlumnosColegioResponse;
import com.grup14.luterano.response.reporteRankingAlumno.RankingTodosCursosResponse;

public interface ReporteRankingAlumnoService {
    
    /**
     * Obtiene el ranking de alumnos con mejor promedio de un curso específico.
     * Si hay empates en el top 3, devuelve más de 3 alumnos.
     */
    RankingAlumnosCursoResponse rankingAlumnosPorCurso(Long cursoId, int anio);
    
    /**
     * Obtiene el ranking de alumnos con mejor promedio de todo el colegio.
     * Si hay empates en el top 3, devuelve más de 3 alumnos.
     */
    RankingAlumnosColegioResponse rankingAlumnosColegio(int anio);
    
    /**
     * Obtiene todos los cursos con el ranking de sus 3 mejores alumnos.
     * Si hay empates en el top 3, devuelve más de 3 alumnos por curso.
     */
    RankingTodosCursosResponse rankingTodosCursos(int anio);
}