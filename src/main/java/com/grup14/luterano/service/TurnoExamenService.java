package com.grup14.luterano.service;

import com.grup14.luterano.dto.TurnoDto;
import com.grup14.luterano.response.turnoExamen.TurnoListResponse;
import com.grup14.luterano.response.turnoExamen.TurnoResponse;

public interface TurnoExamenService {
    TurnoListResponse listar(Integer anio);
    TurnoResponse crear(TurnoDto dto);        // requiere anio + mes
    TurnoResponse actualizar(Long id, TurnoDto dto);
    TurnoResponse eliminar(Long id);
}
