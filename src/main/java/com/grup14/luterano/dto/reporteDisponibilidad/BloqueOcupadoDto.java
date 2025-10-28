package com.grup14.luterano.dto.reporteDisponibilidad;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloqueOcupadoDto {
    private Long moduloId;
    private Integer orden;
    private LocalTime horaDesde;
    private LocalTime horaHasta;

    private Long cursoId;
    private Integer cursoAnio;
    private String cursoDivision;

    private Long materiaId;
    private String materiaNombre;

    private double horas; // duración en horas del bloque
}
