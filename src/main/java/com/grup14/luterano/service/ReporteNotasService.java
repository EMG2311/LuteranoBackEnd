package com.grup14.luterano.service;

import com.grup14.luterano.response.reporteNotas.CalificacionesAlumnoAnioResponse;
import com.grup14.luterano.response.reporteNotas.CalificacionesCursoAnioResponse;

public interface ReporteNotasService {
    CalificacionesCursoAnioResponse listarResumenCursoPorAnio(Long cursoId, int anio);
    CalificacionesAlumnoAnioResponse listarResumenPorAnio(Long alumnoId, int anio);
}
