package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.docente.DocenteDto;
import com.grup14.luterano.dto.docente.DocenteLigeroDto;
import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.User;

public class DocenteMapper {

    // DTO completo, sin mapear la lista de dictados para evitar ciclos
    public static DocenteDto toDto(Docente entity) {
        if (entity == null) return null;

        return DocenteDto.builder()
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
                .user(UserMapper.toDto(entity.getUser()))
                // dictados se maneja aparte si hace falta
                .build();
    }

    // DTO ligero, solo datos básicos
    public static DocenteLigeroDto toLigeroDto(Docente entity) {
        if (entity == null) return null;
        return DocenteLigeroDto.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .mail(entity.getEmail())
                .build();
    }

    // Conversión de DTO a entidad
    public static Docente toEntity(DocenteDto dto, User user) {
        if (dto == null) return null;

        return Docente.builder()
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
                .user(user)
                // dictados se maneja en servicio
                .build();
    }
}
