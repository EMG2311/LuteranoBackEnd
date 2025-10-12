package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.AsistenciaDocenteDto;
import com.grup14.luterano.entities.AsistenciaDocente;
import com.grup14.luterano.entities.Docente;

import java.util.Collections;
import java.util.List;

public class AsistenciaDocenteMapper {

    private AsistenciaDocenteMapper() {}

    public static AsistenciaDocenteDto toDto(AsistenciaDocente a) {
        if (a == null) return null;
        Docente d = a.getDocente();
        return AsistenciaDocenteDto.builder()
                .id(a.getId())
                .docenteId(d != null ? d.getId() : null)
                .docenteNombre(d != null ? d.getNombre() : null)
                .docenteApellido(d != null ? d.getApellido() : null)
                .fecha(a.getFecha())
                .estado(a.getEstado())
                .observacion(a.getObservacion())
                .build();
    }

    public static List<AsistenciaDocenteDto> toDtoList(List<AsistenciaDocente> list) {
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return list.stream().map(AsistenciaDocenteMapper::toDto).toList();
    }


}
