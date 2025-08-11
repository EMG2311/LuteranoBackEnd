package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.HistorialCalificacionesDto;
import com.grup14.luterano.entities.HistorialCalificaciones;

import java.util.stream.Collectors;

public class HistorialCalificacionesMapper {


    public static HistorialCalificacionesDto toDto(HistorialCalificaciones entity) {
        if (entity == null) {
            return null;
        }

        return HistorialCalificacionesDto.builder()
                .id(entity.getId())
                .materia(MateriaMapper.toDto(entity.getMateria()))
                .cicloLectivo(CicloLectivoMapper.toDto(entity.getCicloLectivo()))
                .promedio(entity.getPromedio())
                .calificaciones(entity.getCalificaciones() == null ? null :
                        entity.getCalificaciones()
                                .stream()
                                .map(CalificacionMapper::toDto)
                                .collect(Collectors.toList())
                )
                .build();
    }

    public static HistorialCalificaciones toEntity(HistorialCalificacionesDto dto) {
        if (dto == null) {
            return null;
        }

        return HistorialCalificaciones.builder()
                .id(dto.getId())
                .materia(MateriaMapper.toEntity(dto.getMateria()))
                .cicloLectivo(CicloLectivoMapper.toEntity(dto.getCicloLectivo()))
                .promedio(dto.getPromedio())
                .calificaciones(dto.getCalificaciones() == null ? null :
                        dto.getCalificaciones()
                                .stream()
                                .map(CalificacionMapper::toEntity)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
