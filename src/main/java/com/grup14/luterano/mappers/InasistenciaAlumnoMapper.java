package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.InasistenciaAlumnoDto;
import com.grup14.luterano.entities.InasistenciaAlumno;

public class InasistenciaAlumnoMapper {

    public static InasistenciaAlumnoDto toDto(InasistenciaAlumno entity) {
        if (entity == null) {
            return null;
        }
        return InasistenciaAlumnoDto.builder()
                .id(entity.getId())
                .fecha(entity.getFecha())
                .estado(entity.getEstado())
                .alumnoId(entity.getAlumno() != null ? entity.getAlumno().getId() : null)
                .usuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null)
                .build();
    }
}
