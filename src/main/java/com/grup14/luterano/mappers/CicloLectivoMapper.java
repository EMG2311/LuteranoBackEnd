package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.CicloLectivoDto;
import com.grup14.luterano.entities.CicloLectivo;

public class CicloLectivoMapper {

    public static CicloLectivoDto toDto(CicloLectivo entity) {
        if (entity == null) {
            return null;
        }
        return CicloLectivoDto.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())  // adapta según campos reales
                .build();
    }

    public static CicloLectivo toEntity(CicloLectivoDto dto) {
        if (dto == null) {
            return null;
        }
        return CicloLectivo.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .build();
    }
}
