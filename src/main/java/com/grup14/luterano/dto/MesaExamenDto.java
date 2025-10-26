package com.grup14.luterano.dto;

import com.grup14.luterano.entities.enums.EstadoMesaExamen;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data @Builder
public class MesaExamenDto {
    private Long id;
    private LocalDate fecha;

    private Long turnoId;          // NEW
    private String turnoNombre;    // NEW

    private Long materiaCursoId;
    private String materiaNombre;

    private Long cursoId;
    private Integer cursoAnio;
    private String cursoNivel;
    private String cursoDivision;

    private Long aulaId;
    private EstadoMesaExamen estado;

    private List<MesaExamenAlumnoDto> alumnos;
}

