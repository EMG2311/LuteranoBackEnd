package com.grup14.luterano.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionDto {
    private Long id;
    private float nota;
    private int numeroNota;
    private float PG;
    private LocalDate fecha;

    private AlumnoDto alumno;
    private MateriaDto materia;
    private CicloLectivoDto cicloLectivo;
    private HistorialMateriaDto historialMaterias;
}