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
    private MateriaDto materia;
    private CicloLectivoDto cicloLectivo;
    private float promedio;
    private List<CalificacionDto> calificaciones;
}
