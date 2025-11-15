package com.grup14.luterano.service;

import com.grup14.luterano.response.reporteRinden.ReporteRindenResponse;

public interface ReporteRindenService {
    ReporteRindenResponse listarRindenPorCurso(Long cursoId, int anio);
    
    /**
     * Devuelve TODOS los alumnos del curso (incluye aprobados por promoción o mesa)
     */
    ReporteRindenResponse listarTodosLosAlumnosPorCurso(Long cursoId, int anio);
    
    /**
     * Devuelve alumnos que deben rendir en el curso, opcionalmente incluyendo alumnos 
     * de otros cursos que tienen materias desaprobadas (previas) del curso consultado
     */
    ReporteRindenResponse listarRindenPorCurso(Long cursoId, int anio, boolean incluirPrevias);
}
