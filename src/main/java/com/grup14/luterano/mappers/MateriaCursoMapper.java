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
               // .cursoId(CursoMapper.toDto(entity.getCurso()))
                // ¡CORRECCIÓN CLAVE! Ya no mapeas el objeto Curso completo. Solo su ID.
                .cursoId(entity.getCurso() != null ? entity.getCurso().getId() : null)
                .docente(DocenteMapper.toDto(entity.getDocente()))
                .build();
    }

    // Conversión de DTO a entidad

    public static MateriaCurso toEntity(MateriaCursoDto dto) {
        if (dto == null) {
            return null;
        }
        return MateriaCurso.builder()
                .id(dto.getId())
                .materia(MateriaMapper.toEntity(dto.getMateria()))
                // ¡No se mapea el curso aquí! La lógica de asociación. Va en servicio.
              //  .curso(CursoMapper.toEntity(dto.getCurso()))
               // .docente(DocenteMapper.toEntity(dto.getDocente(), null)) // Aquí se pasa null para el usuario
                .build();
    }
}
