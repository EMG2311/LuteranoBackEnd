package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.CursoDto;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.MateriaCurso;

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
                // Se mapea la lista de MateriaCurso a una lista de MateriaCursoDto
                .dictados(entity.getDictados().stream()
                        .map(MateriaCursoMapper::toDto)
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
                // La conversión de 'dictados' (lista de DTOs) a entidades
                // es una operación de servicio. No se hace en el mapper.
                .build();
    }
}