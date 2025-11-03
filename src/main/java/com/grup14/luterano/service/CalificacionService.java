package com.grup14.luterano.service;

import com.grup14.luterano.request.calificacion.CalificacionRequest;
import com.grup14.luterano.request.calificacion.CalificacionUpdateRequest;
import com.grup14.luterano.response.calificaciones.CalificacionListResponse;
import com.grup14.luterano.response.calificaciones.CalificacionResponse;

public interface CalificacionService {

    CalificacionResponse crearCalificacion(CalificacionRequest req);

    CalificacionResponse obtener(Long alumnoId, Long materiaId, Long califId);

    CalificacionResponse actualizar(CalificacionUpdateRequest req);

    CalificacionResponse eliminar(Long alumnoId, Long materiaId, Long califId);

    // Listar por año del alumno (todas las materias)
    CalificacionListResponse listarPorAnio(Long alumnoId, int anio);

    CalificacionListResponse listarPorAnioYEtapa(Long alumnoId, int anio, int etapa);

    CalificacionListResponse listarPorMateria(Long alumnoId, Long materiaId);

}
