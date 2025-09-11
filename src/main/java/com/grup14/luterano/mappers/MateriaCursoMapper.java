package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.materiaCurso.MateriaCursoDto;
import com.grup14.luterano.dto.materiaCurso.MateriaCursoLigeroDto;
import com.grup14.luterano.entities.MateriaCurso;

public class MateriaCursoMapper {

    // DTO completo
    public static MateriaCursoDto toDto(MateriaCurso entity) {
        if (entity == null) return null;

        return MateriaCursoDto.builder()
                .id(entity.getId())
                .materia(MateriaMapper.toDto(entity.getMateria()))
                .cursoId(entity.getCurso() != null ? entity.getCurso().getId() : null)
                .docente(DocenteMapper.toLigeroDto(entity.getDocente())) // solo datos básicos
                .build();
    }
    public static MateriaCursoLigeroDto toLigeroDto(MateriaCurso entity) {
        if (entity == null) return null;

        return MateriaCursoLigeroDto.builder()
                .id(entity.getId())
                .materiaNombre(entity.getMateria() != null ? entity.getMateria().getNombre() : null)
                .cursoId(entity.getCurso() != null ? entity.getCurso().getId() : null)
                .build();
    }
    // Conversión de DTO a entidad
    public static MateriaCurso toEntity(MateriaCursoDto dto) {
        if (dto == null) return null;

        return MateriaCurso.builder()
                .id(dto.getId())
                .materia(MateriaMapper.toEntity(dto.getMateria()))
                // curso y docente se asignan en servicio
                .build();
    }
}
