package com.grup14.luterano.mappers;

import com.grup14.luterano.dto.CursoDto;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.MateriaCurso;
import com.grup14.luterano.request.curso.CursoRequest;

import java.util.stream.Collectors;

public class CursoMapper {
    // Convierte una entidad Curso a un DTO (CursoDto)
    public static CursoDto toDto(Curso entity) {
        if (entity == null) {
            return null;
        }

        return CursoDto.builder()
                .id(entity.getId())
                .anio(entity.getAnio())
                .division(entity.getDivision())
                .nivel(entity.getNivel())
                // Mapea el aula si existe
                .aula(AulaMapper.toDto(entity.getAula()))
                // Se mapea la lista de MateriaCurso a una lista de MateriaCursoDto
                .dictados(entity.getDictados().stream()
                        .map(MateriaCursoMapper::toDto)
                        .collect(Collectors.toList()))
                .preceptorId(entity.getPreceptor() != null ? entity.getPreceptor().getId():null)
                .build();
    }



    // Convierte un objeto CursoRequest a una entidad Curso
    public static Curso toEntity(CursoRequest request) {
        if (request == null) {
            return null;
        }

        return Curso.builder()
                .anio(request.getAnio())
                .division(request.getDivision())
                .nivel(request.getNivel())
                // El aula y los dictados se manejan en el servicio
                .build();
    }

    // convertir de CursoDto a la entidad Curso (para actualización o mappers anidados)
    public static Curso toEntity(CursoDto dto) {
        if (dto == null) {
            return null;
        }
        return Curso.builder()
                .id(dto.getId())
                .anio(dto.getAnio())
                .division(dto.getDivision())
                .nivel(dto.getNivel())
                // El aula y los dictados se manejan en el servicio
                .build();
    }



}