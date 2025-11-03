package com.grup14.luterano.response.reporteExamenesConsecutivos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenPorMateriaDto {
    private Long materiaId;
    private String materiaNombre;
    private Integer totalCasos;
    private Integer casosCriticos;
    private Integer casosAltos;
    private Integer casosMedios;
    private Double porcentajeAfectacion; // Sobre total de alumnos en esa materia
}