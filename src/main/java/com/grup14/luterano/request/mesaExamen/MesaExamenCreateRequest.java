package com.grup14.luterano.request.mesaExamen;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MesaExamenCreateRequest {
    private LocalDate fecha;
    private Long materiaCursoId;
    private Long aulaId;   // opcional
    private Long turnoId;  // NEW: obligatorio ahora
}