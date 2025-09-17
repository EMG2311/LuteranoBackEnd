package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.CalificacionDto;
import com.grup14.luterano.entities.Calificacion;

public class CalificacionMapper {


    public static CalificacionDto toDto(Calificacion entity) {
        if (entity == null) {
            return null;
        }
        return CalificacionDto.builder()
                .id(entity.getId())
                .nota(entity.getNota())
                .numeroNota(entity.getNumeroNota())
                .PG(entity.getPG())
                .fecha(entity.getFecha())
                .alumno(AlumnoMapper.toDto(entity.getAlumno()))
                .materia(MateriaMapper.toDto(entity.getMateria()))
                .cicloLectivo(CicloLectivoMapper.toDto(entity.getCicloLectivo()))
                .historialMaterias(HistorialMateriaMapper.toDto(entity.getHistorialCalificaciones()))
                .build();
    }

    public static Calificacion toEntity(CalificacionDto dto) {
        if (dto == null) {
            return null;
        }
        return Calificacion.builder()
                .id(dto.getId())
                .nota(dto.getNota())
                .numeroNota(dto.getNumeroNota())
                .PG(dto.getPG())
                .fecha(dto.getFecha())
                .alumno(AlumnoMapper.toEntity(dto.getAlumno()))
                .materia(MateriaMapper.toEntity(dto.getMateria()))
                .cicloLectivo(CicloLectivoMapper.toEntity(dto.getCicloLectivo()))
                .historialCalificaciones(HistorialMateriaMapper.toEntity(dto.getHistorialMaterias()))
                .build();
    }
}
