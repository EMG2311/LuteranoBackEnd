package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.asistencia.AsistenciaAlumnoDto;
import com.grup14.luterano.entities.AsistenciaAlumno;

public class AsistenciaAlumnoMapper {
    public static AsistenciaAlumnoDto toDto(AsistenciaAlumno a) {
        var al = a.getAlumno();
        return AsistenciaAlumnoDto.builder()
                .id(a.getId())
                .alumnoId(al != null ? al.getId() : null)
                .alumnoNombre(al != null ? al.getNombre() : null)
                .alumnoApellido(al != null ? al.getApellido() : null)
                .fecha(a.getFecha())
                .observacion(a.getObservacion() != null ? a.getObservacion() : null)
                .estado(a.getEstado())
                .build();
    }
}
