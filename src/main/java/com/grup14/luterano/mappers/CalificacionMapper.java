package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.CalificacionDto;
import com.grup14.luterano.entities.Calificacion;

public class CalificacionMapper {

    public static CalificacionDto toDto(Calificacion entity) {
        if (entity == null) return null;

        return CalificacionDto.builder()
                .id(entity.getId())
                .nota(entity.getNota())
                .numeroNota(entity.getNumeroNota())
                .fecha(entity.getFecha())
                .historialMateria(HistorialMateriaMapper.toDto(entity.getHistorialMateria()))
                .build();
    }

    public static Calificacion toEntity(CalificacionDto dto) {
        if (dto == null) return null;

        return Calificacion.builder()
                .id(dto.getId())
                .nota(dto.getNota())
                .numeroNota(dto.getNumeroNota())
                .fecha(dto.getFecha())
                .historialMateria(HistorialMateriaMapper.toEntity(dto.getHistorialMateria()))
                .build();
    }
}
