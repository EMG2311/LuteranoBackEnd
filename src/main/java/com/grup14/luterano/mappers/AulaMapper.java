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
                    .curso(CursoMapper.toDto(entity.getCurso()))
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
                    .curso(CursoMapper.toEntity(dto.getCurso()))
                    .build();
        }
}
