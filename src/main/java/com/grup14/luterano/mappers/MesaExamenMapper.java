package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.MesaExamenAlumnoDto;
import com.grup14.luterano.dto.MesaExamenDto;
import com.grup14.luterano.entities.MesaExamen;

import java.util.List;

public class MesaExamenMapper {
    public static MesaExamenDto toDto(MesaExamen m, boolean withAlumnos) {
        var mc = m.getMateriaCurso();
        var c = mc.getCurso();
        var mat = mc.getMateria();

        List<MesaExamenAlumnoDto> convocados = List.of();
        if (withAlumnos && m.getAlumnos() != null) {
            convocados = m.getAlumnos().stream().map(ma -> MesaExamenAlumnoDto.builder()
                    .id(ma.getId())
                    .alumnoId(ma.getAlumno().getId())
                    .dni(ma.getAlumno().getDni())
                    .apellido(ma.getAlumno().getApellido())
                    .nombre(ma.getAlumno().getNombre())
                    .estado(ma.getEstado())
                    .notaFinal(ma.getNotaFinal())
                    .turnoId(ma.getTurno() != null ? ma.getTurno().getId() : null)
                    .turnoNombre(ma.getTurno() != null ? ma.getTurno().getNombre() : null)
                    .build()
            ).toList();
        }

        return MesaExamenDto.builder()
                .id(m.getId())
                .fecha(m.getFecha())
                .materiaCursoId(mc.getId())
                .materiaNombre(mat.getNombre())
                .cursoId(c.getId())
                .cursoAnio(c.getAnio())
                .cursoNivel(c.getNivel() != null ? c.getNivel().name() : null)
                .cursoDivision(c.getDivision() != null ? c.getDivision().name() : null)
                .aulaId(m.getAula() != null ? m.getAula().getId() : null)
                .estado(m.getEstado())
                .alumnos(convocados)
                .build();
    }
}
