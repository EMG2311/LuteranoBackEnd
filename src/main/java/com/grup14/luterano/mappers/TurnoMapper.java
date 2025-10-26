package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.TurnoDto;
import com.grup14.luterano.entities.TurnoExamen;

public class TurnoMapper {
    public static TurnoDto toDto(TurnoExamen t){
        return TurnoDto.builder()
                .id(t.getId())
                .nombre(t.getNombre())
                .anio(t.getAnio())
                .fechaInicio(t.getFechaInicio())
                .fechaFin(t.getFechaFin())
                .activo(t.isActivo())
                .build();
    }
}
