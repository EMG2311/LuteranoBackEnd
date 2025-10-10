package com.grup14.luterano.dto.calificaciones;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionesMateriaResumenDto {
    private Long materiaId;
    private String materiaNombre;
    private Integer[] e1Notas;
    private Integer[] e2Notas;
    private Double e1;
    private Double e2;
    private Double pg;
    private String estado;
}