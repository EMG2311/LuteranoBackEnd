package com.grup14.luterano.service;

import com.grup14.luterano.request.actaExamen.ActaCreateRequest;
import com.grup14.luterano.request.actaExamen.ActaUpdateRequest;
import com.grup14.luterano.response.actaExamen.ActaExamenListResponse;
import com.grup14.luterano.response.actaExamen.ActaExamenResponse;

import java.time.LocalDate;

public interface ActaExamenService {
    ActaExamenResponse generar(ActaCreateRequest req);          // genera o devuelve existente (idempotente)
    ActaExamenResponse actualizar(ActaUpdateRequest req);
    ActaExamenResponse eliminar(Long id);

    ActaExamenResponse obtenerPorId(Long id);
    ActaExamenResponse obtenerPorMesa(Long mesaId);
    ActaExamenResponse obtenerPorNumero(String numeroActa);

    ActaExamenListResponse buscarPorNumeroLike(String q);
    ActaExamenListResponse listarPorTurno(Long turnoId);
    ActaExamenListResponse listarPorCurso(Long cursoId);
    ActaExamenListResponse listarEntreFechas(LocalDate desde, LocalDate hasta);
}
