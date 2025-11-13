package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.ActaExamenDto;
import com.grup14.luterano.entities.ActaExamen;
import com.grup14.luterano.entities.enums.EstadoConvocado;

import java.util.ArrayList;
import java.util.List;

public class ActaExamenMapper {
    public static ActaExamenDto toDto(ActaExamen a) {
        var m = a.getMesa();
        var mc = m.getMateriaCurso();
        var c = mc.getCurso();
        var mat = mc.getMateria();
        var t = m.getTurno();

        // Construir lista de docentes
        List<ActaExamenDto.DocenteSimpleDto> docentes = new ArrayList<>();
        if (m.getDocentes() != null) {
            for (var mesaDocente : m.getDocentes()) {
                var docente = mesaDocente.getDocente();
                docentes.add(ActaExamenDto.DocenteSimpleDto.builder()
                        .id(docente.getId())
                        .nombreCompleto(docente.getApellido() + ", " + docente.getNombre())
                        .dni(docente.getDni())
                        .build());
            }
        }

        // Construir lista de alumnos
        List<ActaExamenDto.ItemAlumnoDto> alumnos = new ArrayList<>();
        if (m.getAlumnos() != null) {
            for (var mesaAlumno : m.getAlumnos()) {
                var alumno = mesaAlumno.getAlumno();
                
                // Determinar observación según reglas de negocio
                String observacion = determinarObservacion(mesaAlumno);

                alumnos.add(ActaExamenDto.ItemAlumnoDto.builder()
                        .alumnoId(alumno.getId())
                        .apellido(alumno.getApellido())
                        .nombre(alumno.getNombre())
                        .dni(alumno.getDni())
                        .nota(mesaAlumno.getNotaFinal())
                        .observacion(observacion)
                        .condicionRinde(mesaAlumno.getCondicionRinde() != null ? 
                            mesaAlumno.getCondicionRinde().name() : null)
                        .build());
            }
        }

        return ActaExamenDto.builder()
                .id(a.getId())
                .numeroActa(a.getNumeroActa())
                .fechaCierre(a.getFechaCierre())
                .cerrada(a.isCerrada())
                .observaciones(a.getObservaciones())
                .cursoAnio(c.getAnio())
                .cursoDivision(c.getDivision() != null ? c.getDivision().name() : null)
                .cursoNivel(c.getNivel() != null ? c.getNivel().name() : null)
                .materiaId(mat.getId())
                .materiaNombre(mat.getNombre())
                .turnoId(t != null ? t.getId() : null)
                .turnoNombre(t != null ? t.getNombre() : null)
                .fecha(m.getFecha() != null ? m.getFecha() : a.getFechaCierre())
                .docentes(docentes)
                .alumnos(alumnos)
                .build();
    }

    private static String determinarObservacion(com.grup14.luterano.entities.MesaExamenAlumno mesaAlumno) {
        if (mesaAlumno.getEstado() == EstadoConvocado.AUSENTE) {
            return "Ausente";
        }
        
        Integer nota = mesaAlumno.getNotaFinal();
        if (nota == null) {
            return "Ausente";
        }
        
        if (nota >= 6) {
            return "Aprobado";
        } else {
            return "Desaprobado";
        }
    }
}
