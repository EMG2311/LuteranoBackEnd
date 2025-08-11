package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.HistorialCursoDto;
import com.grup14.luterano.entities.HistorialCurso;

import java.util.stream.Collectors;

public class HistorialCursoMapper {

    public static HistorialCursoDto toDto(HistorialCurso entity) {
        if (entity == null) {
            return null;
        }

        return HistorialCursoDto.builder()
                .id(entity.getId())
                .curso(CursoMapper.toDto(entity.getCurso()))
                .fechaDesde(entity.getFechaDesde())
                .fechaHasta(entity.getFechaHasta())
                .promedio(entity.getPromedio())
                .cicloLectivo(CicloLectivoMapper.toDto(entity.getCicloLectivo()))
                .historialesCalificaciones(entity.getHistorialesCalificaciones() == null ? null :
                        entity.getHistorialesCalificaciones()
                                .stream()
                                .map(HistorialCalificacionesMapper::toDto)
                                .collect(Collectors.toList())
                )
                .build();
    }

    public static HistorialCurso toEntity(HistorialCursoDto dto) {
        if (dto == null) {
            return null;
        }

        return HistorialCurso.builder()
                .id(dto.getId())
                .curso(CursoMapper.toEntity(dto.getCurso()))
                .fechaDesde(dto.getFechaDesde())
                .fechaHasta(dto.getFechaHasta())
                .promedio(dto.getPromedio())
                .cicloLectivo(CicloLectivoMapper.toEntity(dto.getCicloLectivo()))
                .historialesCalificaciones(dto.getHistorialesCalificaciones() == null ? null :
                        dto.getHistorialesCalificaciones()
                                .stream()
                                .map(HistorialCalificacionesMapper::toEntity)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
