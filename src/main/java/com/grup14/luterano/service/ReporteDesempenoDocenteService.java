package com.grup14.luterano.service;

import com.grup14.luterano.response.reporteDesempeno.ReporteDesempenoResponse;

public interface ReporteDesempenoDocenteService {

    /**
     * Genera un reporte completo de desempeño por docente y materia
     * para un ciclo lectivo específico.
     * <p>
     * Calcula:
     * - % de aprobación y reprobación por docente/materia
     * - Estadísticas comparativas entre docentes
     * - Análisis de variación en rendimiento
     *
     * @param cicloLectivoAnio Año del ciclo lectivo a analizar
     * @return Reporte completo con estadísticas y análisis
     */
    ReporteDesempenoResponse generarReporteDesempeno(int cicloLectivoAnio);

    /**
     * Genera reporte filtrado por materia específica
     *
     * @param cicloLectivoAnio Año del ciclo lectivo
     * @param materiaId        ID de la materia a analizar
     * @return Reporte filtrado por materia
     */
    ReporteDesempenoResponse generarReportePorMateria(int cicloLectivoAnio, Long materiaId);

    /**
     * Genera reporte filtrado por docente específico
     *
     * @param cicloLectivoAnio Año del ciclo lectivo
     * @param docenteId        ID del docente a analizar
     * @return Reporte filtrado por docente
     */
    ReporteDesempenoResponse generarReportePorDocente(int cicloLectivoAnio, Long docenteId);
}