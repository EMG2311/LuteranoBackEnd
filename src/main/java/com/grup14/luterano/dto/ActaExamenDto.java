package com.grup14.luterano.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder

public class ActaExamenDto {
    private Long id;
    private Long mesaId;
    private String numeroActa;
    private LocalDate fechaCierre;
    private boolean cerrada;
    private String observaciones;

    // Datos de contexto útiles
    private Long turnoId;
    private String turnoNombre;

    private Long materiaCursoId;
    private String materiaNombre;

    private Long cursoId;
    private Integer cursoAnio;
    private String cursoNivel;
    private String cursoDivision;
}
