package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.MateriaDto;
import com.grup14.luterano.entities.Materia;

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
        dto.setCursos(entity.getCursos());  // ideal mapear a CursoDto si tenés
        return dto;
    }

    public static Materia toEntity(MateriaDto dto) {
        if (dto == null) {
            return null;
        }

        Materia entity = Materia.builder()
                .id(dto.getId())
                .nombreMateria(dto.getNombreMateria())
                .descipcion(dto.getDescripcion()) // corregí nombre
                .nivel(dto.getNivel())
                .cursos(dto.getCursos())  // ideal mapear a entidad Curso
                .build();

        return entity;
    }
}
