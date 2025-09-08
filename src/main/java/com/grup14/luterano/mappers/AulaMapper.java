package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.AulaDto;
import com.grup14.luterano.entities.Aula;

public class AulaMapper {
        public static AulaDto toDto(Aula entity) {
            if (entity == null) {
                return null;
            }
            return AulaDto.builder()
                    .id(entity.getId())
                    .nombre(entity.getNombre())
                    .ubicacion(entity.getUbicacion())
                    .capacidad(entity.getCapacidad())
                    .cursoId(entity.getCurso() != null ? entity.getCurso().getId() : null)
                    .build();
        }

        public static Aula toEntity(AulaDto dto) {
            if (dto == null) {
                return null;
            }
            return Aula.builder()
                    .id(dto.getId())
                    .nombre(dto.getNombre())
                    .ubicacion(dto.getUbicacion())
                    .capacidad(dto.getCapacidad())
                    // La búsqueda del curso por ID debe hacerse en el servicio, no en el mapper.
                    .build();
        }
}
