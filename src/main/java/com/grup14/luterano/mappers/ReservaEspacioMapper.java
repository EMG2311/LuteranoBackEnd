package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.ReservaEspacioDto;
import com.grup14.luterano.entities.ReservaEspacio;

import java.util.List;
import java.util.stream.Collectors;

public class ReservaEspacioMapper {

    public static ReservaEspacioDto toDto(ReservaEspacio entity) {
        if (entity == null) return null;
        return ReservaEspacioDto.builder()
                .id(entity.getId())
                .fecha(entity.getFecha())
                .cursoId(entity.getCurso().getId())
                .cantidadAlumnos(entity.getCantidadAlumnos())
                .espacioAulicoId(entity.getEspacioAulico().getId())
                .modulo(ModuloMapper.toDto(entity.getModulo()))
                .usuarioId(entity.getUsuario().getId())
                .motivoSolicitud(entity.getMotivoSolicitud())
                .estado(entity.getEstado())
                .motivoDenegacion(entity.getMotivoDenegacion())
                .build();
    }

    public static List<ReservaEspacioDto> toDtoList(List<ReservaEspacio> entities) {
        if (entities == null || entities.isEmpty()) return List.of();
        return entities.stream().map(ReservaEspacioMapper::toDto)
                .collect(Collectors.toList());
    }
}
