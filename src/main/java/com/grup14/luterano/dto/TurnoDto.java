package com.grup14.luterano.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TurnoDto {
    private Long id;
    private String nombre;      // "FEBRERO 2026" (opcional, lo podemos derivar)
    private Integer anio;       // REQ
    private Integer mes;        // REQ (1-12)  <<---
    private LocalDate fechaInicio; // se setea solo
    private LocalDate fechaFin;    // se setea solo
    private Boolean activo;
}
