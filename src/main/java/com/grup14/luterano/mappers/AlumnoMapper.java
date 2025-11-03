package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.AlumnoDto;
import com.grup14.luterano.entities.Alumno;

import java.util.stream.Collectors;

public class AlumnoMapper {


    public static AlumnoDto toDto(Alumno entity) {
        if (entity == null) {
            return null;
        }
        return AlumnoDto.builder()
                // Campos PersonaDto (heredados)
                .id(entity.getId())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .genero(entity.getGenero())
                .tipoDoc(entity.getTipoDoc())
                .dni(entity.getDni())
                .email(entity.getEmail())
                .direccion(entity.getDireccion())
                .telefono(entity.getTelefono())
                .fechaNacimiento(entity.getFechaNacimiento())
                .fechaIngreso(entity.getFechaIngreso())

                // Campos específicos de AlumnoDto
                .cursoActual(CursoMapper.toDto(entity.getCursoActual()))
                .estado(entity.getEstado())
                .tutor(TutorMapper.toDto(entity.getTutor()))
                .historialCursos(entity.getHistorialCursos() == null ? null :
                        entity.getHistorialCursos()
                                .stream()
                                .map(HistorialCursoMapper::toDto)
                                .collect(Collectors.toList())
                )
                .build();
    }

    public static Alumno toEntity(AlumnoDto dto) {
        if (dto == null) {
            return null;
        }

        return Alumno.builder()
                // Campos Persona (heredados)
                .id(dto.getId() != null ? dto.getId() : null)
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .genero(dto.getGenero())
                .tipoDoc(dto.getTipoDoc())
                .dni(dto.getDni())
                .email(dto.getEmail())
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefono())
                .fechaNacimiento(dto.getFechaNacimiento())
                .fechaIngreso(dto.getFechaIngreso())

                // Campos específicos de Alumno
                .estado(dto.getEstado())
                .build();
    }
}
