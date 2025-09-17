package com.grup14.luterano.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialCursoDto {
    private Long id;
    private CursoDto curso;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private float promedio;
    private CicloLectivoDto cicloLectivo;
    private List<HistorialMateriaDto> historialMateriaDtos;
}