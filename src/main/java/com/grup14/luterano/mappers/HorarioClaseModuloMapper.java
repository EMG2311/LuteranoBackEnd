package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.HorarioClaseModuloDto;
import com.grup14.luterano.entities.HorarioClaseModulo;

import java.util.List;
import java.util.stream.Collectors;

public class HorarioClaseModuloMapper {

    public static HorarioClaseModuloDto toDto(HorarioClaseModulo h) {
        if (h == null) return null;
        return HorarioClaseModuloDto.builder()
                .diaSemana(h.getDiaSemana())
                .modulo(ModuloMapper.toDto(h.getModulo()))
                .materiaCurso(MateriaCursoMapper.toDto(h.getMateriaCurso()))
                .build();
    }

    public static List<HorarioClaseModuloDto> toDtoList(List<HorarioClaseModulo> items) {
        if (items == null || items.isEmpty()) return List.of();
        return items.stream()
                .map(HorarioClaseModuloMapper::toDto)
                .collect(Collectors.toList());
    }
}
