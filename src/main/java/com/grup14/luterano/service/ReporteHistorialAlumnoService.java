package com.grup14.luterano.service;

import com.grup14.luterano.response.reporteHistorialAlumno.ReporteHistorialAlumnoResponse;

public interface ReporteHistorialAlumnoService {

    /**
     * Genera el historial académico completo de un alumno
     * incluyendo todas las calificaciones de todos los ciclos lectivos.
     *
     * @param alumnoId ID del alumno
     * @return ReporteHistorialAlumnoResponse con el historial completo
     */
    ReporteHistorialAlumnoResponse generarHistorialCompleto(Long alumnoId);

    /**
     * Genera el historial académico de un alumno para un ciclo específico.
     *
     * @param alumnoId ID del alumno
     * @param cicloLectivoId ID del ciclo lectivo específico
     * @return ReporteHistorialAlumnoResponse con el historial del ciclo
     */
    ReporteHistorialAlumnoResponse generarHistorialPorCiclo(Long alumnoId, Long cicloLectivoId);
}