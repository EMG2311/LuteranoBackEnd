package com.grup14.luterano.service;

import com.grup14.luterano.request.historialCursoRequest.HistorialCursoRequest;
import com.grup14.luterano.response.historialCurso.HistorialCursoResponse;
import com.grup14.luterano.response.historialCurso.HistorialCursoResponseList;

public interface HistorialCursoService {
    HistorialCursoResponseList listarHistorialAlumnoFiltrado(Long alumnoId, Long cicloLectivoId, Long cursoId);
    HistorialCursoResponse getHistorialCursoActual(Long alumnoId);
}
