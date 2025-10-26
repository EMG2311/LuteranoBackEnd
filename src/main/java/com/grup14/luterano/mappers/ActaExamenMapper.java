package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.ActaExamenDto;
import com.grup14.luterano.entities.ActaExamen;

public class ActaExamenMapper {
    public static ActaExamenDto toDto(ActaExamen a) {
        var m  = a.getMesa();
        var mc = m.getMateriaCurso();
        var c  = mc.getCurso();
        var mat= mc.getMateria();
        var t  = m.getTurno();

        return ActaExamenDto.builder()
                .id(a.getId())
                .mesaId(m.getId())
                .numeroActa(a.getNumeroActa())
                .fechaCierre(a.getFechaCierre())
                .cerrada(a.isCerrada())
                .observaciones(a.getObservaciones())
                .turnoId(t!=null? t.getId(): null)
                .turnoNombre(t!=null? t.getNombre(): null)
                .materiaCursoId(mc.getId())
                .materiaNombre(mat.getNombre())
                .cursoId(c.getId())
                .cursoAnio(c.getAnio())
                .cursoNivel(c.getNivel()!=null? c.getNivel().name(): null)
                .cursoDivision(c.getDivision()!=null? c.getDivision().name(): null)
                .build();
    }
}
