package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.docente.InasistenciaDocenteDto;
import com.grup14.luterano.entities.Aula;
import com.grup14.luterano.entities.InasistenciaDocente;
import com.grup14.luterano.entities.enums.EstadoAsistencia;
import com.grup14.luterano.request.docente.InasistenciaDocenteRequest;

import java.time.LocalDate;

public class InasistenciaDocenteMapper {
    public static InasistenciaDocenteDto toDto(InasistenciaDocente entity) {
        if (entity == null) {
            return null;
        }
        return InasistenciaDocenteDto.builder()
                .id(entity.getId())
                .docenteId(entity.getDocente() != null ? entity.getDocente().getId() : null)
                .preceptorId(entity.getPreceptor() != null ? entity.getPreceptor().getId() : null)
                .fecha(entity.getFecha())
                .estado(entity.getEstado())
                .build();
    }

    // Convierte un objeto InasistenciaDocenteRequest a una entidad InasistenciaDocente
    public static InasistenciaDocente toEntity(InasistenciaDocenteRequest request) {
            if (request == null) {
                return null;
            }

            return InasistenciaDocente.builder()
                    .fecha(request.getFecha())
                    .estado(request.getEstado())
                    // La búsqueda del docente y preceptor por ID debe hacerse en el servicio, no en el mapper.
                    .build();
        }


   // convertir de dto a entity (para actualización)

    public static InasistenciaDocente toEntity(InasistenciaDocenteDto dto) {
         if (dto == null) {
              return null;
         }
         return InasistenciaDocente.builder()
                .id(dto.getId())
                .fecha(dto.getFecha())
                .estado(dto.getEstado())
                // La búsqueda del docente y preceptor por ID debe hacerse en el servicio, no en el mapper.
                .build();
    }
}



