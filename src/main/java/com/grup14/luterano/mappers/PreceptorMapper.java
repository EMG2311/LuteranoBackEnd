package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.PreceptorDto;
import com.grup14.luterano.dto.TutorDto;
import com.grup14.luterano.entities.Preceptor;
import com.grup14.luterano.entities.Tutor;

public class PreceptorMapper {
    public static PreceptorDto toDto(Preceptor entity) {
        if (entity == null) {
            return null;
        }

        return PreceptorDto.builder()
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

    public static Preceptor toEntity(PreceptorDto dto) {
        if (dto == null) {
            return null;
        }

        return Preceptor.builder()
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
