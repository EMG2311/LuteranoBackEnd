package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.MateriaDto;
import com.grup14.luterano.entities.Materia;

public class MateriaMapper {

    public static MateriaDto toDto(Materia entity) {
        if (entity == null) {
            return null;
        }

        // Se crea el DTO usando el Builder para un código más limpio y legible.
        return MateriaDto.builder()
            .id(entity.getId())
            .nombreMateria(entity.getNombre())
            .descripcion(entity.getDescripcion())
            .nivel(entity.getNivel())
            // .activa(entity.isActiva()) // Si se agrega en el DTO
            .build();
    }

    public static Materia toEntity(MateriaDto dto) {
        if (dto == null) {
            return null;
        }

        // Se crea la entidad Materia usando el Builder.
        // Importante: No se mapea la lista 'dictados' de vuelta a la entidad aquí.
        // La lógica para crear o actualizar la entidad de unión MateriaCurso
        // debe residir en la capa de servicio para asegurar la integridad de la base de datos.
        return Materia.builder()
                .id(dto.getId())
                .nombre(dto.getNombreMateria())
                .descripcion(dto.getDescripcion())
                .nivel(dto.getNivel())
                .build();
    }
}
