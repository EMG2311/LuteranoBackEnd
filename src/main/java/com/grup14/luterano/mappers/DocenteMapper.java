package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.DocenteDto;
import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.User;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DocenteMapper {
    public static DocenteDto toDto(Docente entity) {
        if (entity == null) {
            return null;
        }
        return DocenteDto.builder()
                // Campos de Persona
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
                // Campo de PersonaConUsuario
                .user(UserMapper.toDto(entity.getUser()))
                // Campo de Docente
                .materias(     entity.getMaterias() == null ?
                        null :
                        entity.getMaterias()
                                .stream()
                                .map(MateriaMapper::toDto)
                                .collect(Collectors.toSet()))
                .build();
    }

    public static Docente toEntity(DocenteDto dto, User user) {
        if (dto == null) {
            return null;
        }
        return Docente.builder()
                // Campos de Persona
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
                // Campo de PersonaConUsuario
                .user(user)
                // Campo de Docente
                .materias(     dto.getMaterias() == null ?
                        null :
                        dto.getMaterias()
                                .stream()
                                .map(MateriaMapper::toEntity)
                                .collect(Collectors.toSet()))
                .build();
    }
}
