package com.grup14.luterano.service;

import com.grup14.luterano.entities.HistorialCurso;
import com.grup14.luterano.request.historialCursoRequest.HistorialCursoRequest;
import com.grup14.luterano.response.historialCurso.HistorialCursoResponse;
import com.grup14.luterano.response.historialCurso.HistorialCursoResponseList;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface HistorialCursoService {
    HistorialCursoResponseList listarHistorialAlumnoFiltrado(Long alumnoId, Long cicloLectivoId, Long cursoId);
    HistorialCursoResponse getHistorialCursoActual(Long alumnoId);
}
