package com.grup14.luterano.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialCalificacionesDto {
    private Long id;
    private HistorialCursoDto historialCurso;
    private MateriaCursoDto materiaCurso;

    private float promedio;
    private List<CalificacionDto> calificaciones;
}
