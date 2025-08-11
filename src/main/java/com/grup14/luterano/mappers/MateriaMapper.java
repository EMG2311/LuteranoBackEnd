package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.MateriaDto;
import com.grup14.luterano.entities.Materia;

import java.util.stream.Collectors;

public class MateriaMapper {

    public static MateriaDto toDto(Materia entity) {
        if (entity == null) {
            return null;
        }

        MateriaDto dto = new MateriaDto();
        dto.setId(entity.getId());
        dto.setNombreMateria(entity.getNombreMateria());
        dto.setDescripcion(entity.getDescipcion());  // corregí nombre
        dto.setNivel(entity.getNivel());
        dto.setCursos(entity.getCursos().stream().map(CursoMapper::toDto).collect(Collectors.toList()));
        return dto;
    }

    public static Materia toEntity(MateriaDto dto) {
        if (dto == null) {
            return null;
        }

        return Materia.builder()
                .id(dto.getId())
                .nombreMateria(dto.getNombreMateria())
                .descipcion(dto.getDescripcion())
                .nivel(dto.getNivel())
                .cursos(dto.getCursos() == null ? null :
                        dto.getCursos()
                                .stream()
                                .map(CursoMapper::toEntity)
                                .collect(Collectors.toList()))
                .build();
    }
}
