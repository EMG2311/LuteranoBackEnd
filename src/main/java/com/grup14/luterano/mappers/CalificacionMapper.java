package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.CalificacionDto;
import com.grup14.luterano.entities.Calificacion;
import com.grup14.luterano.entities.HistorialMateria;
import com.grup14.luterano.entities.MateriaCurso;

public class CalificacionMapper {

    public static CalificacionDto toDto(Calificacion c) {
        if (c == null) return null;

        HistorialMateria hm = c.getHistorialMateria();
        MateriaCurso mc = (hm != null) ? hm.getMateriaCurso() : null;

        Long materiaCursoId = (mc != null) ? mc.getId() : null;
        Long materiaId = (mc != null && mc.getMateria() != null) ? mc.getMateria().getId() : null;
        String materiaNombre = (mc != null && mc.getMateria() != null) ? mc.getMateria().getNombre() : null;

        return CalificacionDto.builder()
                .id(c.getId())
                .nota(c.getNota())
                .etapa(c.getEtapa())
                .numeroNota(c.getNumeroNota())
                .fecha(c.getFecha())
                .materiaCursoId(materiaCursoId)
                .materiaId(materiaId)
                .materiaNombre(materiaNombre)
                .build();
    }

    public static Calificacion toEntity(CalificacionDto dto) {
        if (dto == null) return null;

        return Calificacion.builder()
                .id(dto.getId())
                .nota(dto.getNota())
                .numeroNota(dto.getNumeroNota())
                .fecha(dto.getFecha())
                .build();
    }
}
