package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.MateriaCursoDto;
import com.grup14.luterano.entities.MateriaCurso;

public class MateriaCursoMapper {

    public static MateriaCursoDto toDto(MateriaCurso entity) {
        if (entity == null) {
            return null;
        }
        return MateriaCursoDto.builder()
                .id(entity.getId())
                .materia(MateriaMapper.toDto(entity.getMateria()))
                .curso(CursoMapper.toDto(entity.getCurso()))
                .docente(DocenteMapper.toDto(entity.getDocente()))
                .build();
    }
}
