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
                .historialMateriaDtos(entity.getHistorialMaterias() == null ? null :
                        entity.getHistorialMaterias()
                                .stream()
                                .map(HistorialMateriaMapper::toDto)
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
                .historialMaterias(dto.getHistorialMateriaDtos() == null ? null :
                        dto.getHistorialMateriaDtos()
                                .stream()
                                .map(HistorialMateriaMapper::toEntity)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
