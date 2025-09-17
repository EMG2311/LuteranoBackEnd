package com.grup14.luterano.dto;

import com.grup14.luterano.entities.HistorialMateria;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CalificacionDto {
    private Long id;
    private Integer nota;
    private int numeroNota;
    private LocalDate fecha;
    private HistorialMateriaDto historialMateria;
}
