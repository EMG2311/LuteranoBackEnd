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
                // Campo de Docente: materias que dicta
                .dictados(entity.getDictados() == null ? null :
                        entity.getDictados()
                                .stream()
                                .map(MateriaCursoMapper::toDto)
                                .collect(Collectors.toList()))
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
                // Se omite la conversión de la lista 'dictados' en el mapper.
                // Esta lógica debe ser manejada en la capa de servicio
                // para garantizar que las relaciones de la base de datos se manejen correctamente.
                .build();
    }
}
