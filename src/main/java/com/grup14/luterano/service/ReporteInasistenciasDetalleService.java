package com.grup14.luterano.service;

import com.grup14.luterano.response.asistenciaAlumno.ReporteInasistenciasDetalleResponse;

public interface ReporteInasistenciasDetalleService {
    /**
     * Si cursoId != null → reporte por curso (todos los alumnos).
     * Si alumnoId != null → reporte solo para ese alumno.
     * Si ambos vienen, se aplica el filtro por alumno dentro del curso.
     */
    ReporteInasistenciasDetalleResponse inasistenciasDetalle(Integer anio, Long cursoId, Long alumnoId);
}
