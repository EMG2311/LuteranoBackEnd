package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.modulo.ModuloDto;
import com.grup14.luterano.entities.Modulo;

public class ModuloMapper {
    public static ModuloDto toDto(Modulo m) {
        if (m == null) return null;
        return ModuloDto.builder()
                .id(m.getId())
                .orden(m.getOrden())
                .desde(m.getHoraDesde() != null ? m.getHoraDesde().toString() : null)
                .hasta(m.getHoraHasta() != null ? m.getHoraHasta().toString() : null)
                .build();
    }
}
