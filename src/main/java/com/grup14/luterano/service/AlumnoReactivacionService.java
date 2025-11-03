package com.grup14.luterano.service;

import com.grup14.luterano.response.alumno.AlumnoResponse;

public interface AlumnoReactivacionService {

    /**
     * Reactiva un alumno excluido por repetición:
     * - Cambia estado a REGULAR
     * - Borra calificaciones del último curso
     * - Resetea contador de repeticiones
     * - Mantiene historial de materias de otros cursos
     */
    AlumnoResponse reactivarAlumno(Long alumnoId);
}