package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.docente.InasistenciaDocenteDto;
import com.grup14.luterano.entities.Aula;
import com.grup14.luterano.entities.InasistenciaDocente;
import com.grup14.luterano.entities.enums.EstadoAsistencia;
import com.grup14.luterano.request.docente.InasistenciaDocenteRequest;

import java.time.LocalDate;

public class InasistenciaDocenteMapper {
    public static InasistenciaDocenteDto toDto(InasistenciaDocente entity) {
        if (entity == null) {
            return null;
        }
        return InasistenciaDocenteDto.builder()
                .id(entity.getId())
                .docenteId(entity.getDocente() != null ? entity.getDocente().getId() : null)
                .usuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null)
                .fecha(entity.getFecha())
                .estado(entity.getEstado())
                .build();
    }

}




