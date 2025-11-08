package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.TutorDto;
import com.grup14.luterano.entities.Tutor;

import java.util.stream.Collectors;

public class TutorMapper {
    public static TutorDto toDto(Tutor entity) {
        if (entity == null) {
            return null;
        }

        return TutorDto.builder()
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
                .build();
    }

    public static TutorDto toDtoWithAlumnos(Tutor entity) {
        if (entity == null) {
            return null;
        }

        return TutorDto.builder()
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
                .alumnos(entity.getAlumnos() == null ? null :
                        entity.getAlumnos()
                                .stream()
                                .map(AlumnoMapper::toDto)
                                .collect(Collectors.toList())
                )
                .build();
    }

    public static Tutor toEntity(TutorDto dto) {
        if (dto == null) {
            return null;
        }

        return Tutor.builder()
                .id(dto.getId())
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
                .build();
    }
}
