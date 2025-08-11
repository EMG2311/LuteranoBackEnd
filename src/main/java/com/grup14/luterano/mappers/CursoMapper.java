package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.CursoDto;
import com.grup14.luterano.entities.Curso;

import java.util.stream.Collectors;

public class CursoMapper {

    public static CursoDto toDto(Curso entity) {
        if (entity == null) {
            return null;
        }

        return CursoDto.builder()
                .id(entity.getId())
                .numero(entity.getNumero())
                .division(entity.getDivision())
                .nivel(entity.getNivel())
                .aula(AulaMapper.toDto(entity.getAula()))
                .materias(entity.getMaterias() == null ? null :
                        entity.getMaterias().stream()
                                .map(MateriaMapper::toDto)
                                .collect(Collectors.toList()))
                .build();
    }

    public static Curso toEntity(CursoDto dto) {
        if (dto == null) {
            return null;
        }

        return Curso.builder()
                .id(dto.getId())
                .numero(dto.getNumero())
                .division(dto.getDivision())
                .nivel(dto.getNivel())
                .aula(AulaMapper.toEntity(dto.getAula()))
                .materias(dto.getMaterias() == null ? null :
                        dto.getMaterias().stream()
                                .map(MateriaMapper::toEntity)
                                .collect(Collectors.toList()))
                .build();
    }
}