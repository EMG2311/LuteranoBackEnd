package com.grup14.luterano.dto.modulo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuloReservaEstadoDto {
    private Long id;
    private String nombre;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer orden;
    private boolean ocupado;
    private String motivoOcupacion; // "Reserva por Juan Pérez" o "Clase regular 3°A"
}