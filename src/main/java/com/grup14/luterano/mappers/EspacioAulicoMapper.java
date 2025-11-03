package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.EspacioAulicoDto;
import com.grup14.luterano.entities.EspacioAulico;
import com.grup14.luterano.request.espacioAulico.EspacioAulicoRequest;

import java.util.List;
import java.util.stream.Collectors;

public class EspacioAulicoMapper {
    // Entity a DTO
    public static EspacioAulicoDto toDto(EspacioAulico entity) {
        if (entity == null) return null;
        return EspacioAulicoDto.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .ubicacion(entity.getUbicacion())
                .capacidad(entity.getCapacidad())
                .build();
    }

    // Convierte Request a Entity (para creación)
    public static EspacioAulico toEntity(EspacioAulicoRequest request) {
        if (request == null) return null;
        return EspacioAulico.builder()
                .nombre(request.getNombre())
                .ubicacion(request.getUbicacion())
                .capacidad(request.getCapacidad())
                .build();
    }

    // Convierte lista de Entities a lista de DTOs
    public static List<EspacioAulicoDto> toDtoList(List<EspacioAulico> entities) {
        return entities.stream()
                .map(EspacioAulicoMapper::toDto)
                .collect(Collectors.toList());
    }
}
