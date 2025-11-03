package com.grup14.luterano.service;

import com.grup14.luterano.response.reporteExamenesConsecutivos.ReporteExamenesConsecutivosResponse;

public interface ReporteExamenesConsecutivosService {
    
    /**
     * Genera el reporte de alumnos que desaprobaron dos exámenes consecutivos
     * de la misma materia en el año especificado.
     * 
     * @param cicloLectivoAnio Año del ciclo lectivo
     * @return Reporte completo con casos detectados y estadísticas
     */
    ReporteExamenesConsecutivosResponse generarReporte(Integer cicloLectivoAnio);
    
    /**
     * Genera el reporte filtrado por materia específica
     * 
     * @param cicloLectivoAnio Año del ciclo lectivo
     * @param materiaId ID de la materia a analizar
     * @return Reporte filtrado por materia
     */
    ReporteExamenesConsecutivosResponse generarReportePorMateria(Integer cicloLectivoAnio, Long materiaId);
    
    /**
     * Genera el reporte filtrado por curso específico
     * 
     * @param cicloLectivoAnio Año del ciclo lectivo
     * @param cursoId ID del curso a analizar
     * @return Reporte filtrado por curso
     */
    ReporteExamenesConsecutivosResponse generarReportePorCurso(Integer cicloLectivoAnio, Long cursoId);
}