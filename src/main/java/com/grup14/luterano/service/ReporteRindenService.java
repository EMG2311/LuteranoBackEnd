package com.grup14.luterano.service;

import com.grup14.luterano.response.reporteRinden.ReporteRindenResponse;

public interface ReporteRindenService {
    ReporteRindenResponse listarRindenPorCurso(Long cursoId, int anio);
    
    /**
     * Devuelve TODOS los alumnos del curso (incluye aprobados por promoción o mesa)
     */
    ReporteRindenResponse listarTodosLosAlumnosPorCurso(Long cursoId, int anio);
}
