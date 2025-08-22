package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.HistorialCalificacionesDto;
import com.grup14.luterano.entities.HistorialCalificaciones;

import java.util.stream.Collectors;

public class HistorialCalificacionesMapper {


public static HistorialCalificacionesDto toDto(HistorialCalificaciones entity) {
    if (entity == null) {
        return null;
    }

    return HistorialCalificacionesDto.builder()
            .id(entity.getId())
            // Se mapea la relación con HistorialCurso (que contiene el alumno y el curso)
            .historialCurso(HistorialCursoMapper.toDto(entity.getHistorialCurso()))
            // Se mapea la relación con MateriaCurso (que contiene la materia y el docente)
            .materiaCurso(MateriaCursoMapper.toDto(entity.getMateriaCurso()))
            .promedio(entity.getPromedio())
            .calificaciones(entity.getCalificaciones() == null ? null :
                    entity.getCalificaciones()
                            .stream()
                            .map(CalificacionMapper::toDto)
                            .collect(Collectors.toList())
            )
            .build();
}

public static HistorialCalificaciones toEntity(HistorialCalificacionesDto dto) {
    if (dto == null) {
        return null;
    }


    return HistorialCalificaciones.builder()
            .id(dto.getId())
            .promedio(dto.getPromedio())
            .calificaciones(dto.getCalificaciones() == null ? null :
                    dto.getCalificaciones()
                            .stream()
                            .map(CalificacionMapper::toEntity)
                            .collect(Collectors.toList())
            )
            .build();
}

}
