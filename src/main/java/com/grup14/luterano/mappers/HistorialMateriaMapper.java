package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.HistorialMateriaDto;
import com.grup14.luterano.entities.HistorialMateria;

import java.util.stream.Collectors;

public class HistorialMateriaMapper {


    public static HistorialMateriaDto toDto(HistorialMateria entity) {
        if (entity == null) {
            return null;
        }

        return HistorialMateriaDto.builder()
                .id(entity.getId())
                // Se mapea la relación con HistorialCurso (que contiene el alumno y el curso)
                //.historialCurso(HistorialCursoMapper.toDto(entity.getHistorialCurso()))
                // Se mapea la relación con MateriaCurso (que contiene la materia y el docente)
                .materiaCurso(MateriaCursoMapper.toDto(entity.getMateriaCurso()))
                .calificaciones(entity.getCalificaciones() == null ? null :
                        entity.getCalificaciones()
                                .stream()
                                .map(CalificacionMapper::toDto)
                                .collect(Collectors.toList())
                )
                .estadoMateriaAlumno(entity.getEstado())
                .build();
    }

    public static HistorialMateria toEntity(HistorialMateriaDto dto) {
        if (dto == null) {
            return null;
        }


        return HistorialMateria.builder()
                .id(dto.getId())
                .calificaciones(dto.getCalificaciones() == null ? null :
                        dto.getCalificaciones()
                                .stream()
                                .map(CalificacionMapper::toEntity)
                                .collect(Collectors.toList())
                )
                .build();
    }

}
