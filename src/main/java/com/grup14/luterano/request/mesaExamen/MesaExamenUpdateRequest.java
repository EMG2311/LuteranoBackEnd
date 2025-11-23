package com.grup14.luterano.request.mesaExamen;

import com.grup14.luterano.entities.enums.TipoMesa;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MesaExamenUpdateRequest {
    private Long id;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Long materiaCursoId;
    private Long aulaId; // opcional
    private TipoMesa tipoMesa;  // Permitir actualizar el tipo de mesa
}