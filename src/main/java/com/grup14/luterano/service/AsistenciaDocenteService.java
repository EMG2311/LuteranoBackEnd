package com.grup14.luterano.service;

import com.grup14.luterano.request.AsistenciaDocenteUpdateRequest;
import com.grup14.luterano.response.asistenciaDocente.AsistenciaDocenteResponse;
import com.grup14.luterano.response.asistenciaDocente.AsistenciaDocenteResponseList;

import java.time.LocalDate;

public interface AsistenciaDocenteService {

    AsistenciaDocenteResponse upsert(AsistenciaDocenteUpdateRequest req);

    AsistenciaDocenteResponseList listarPorDocenteYFecha(Long docenteId, LocalDate fecha);

    AsistenciaDocenteResponseList listarPorFecha(LocalDate fecha);
}
