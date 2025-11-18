package com.grup14.luterano.dto.asistencia;

import com.grup14.luterano.entities.enums.EstadoAsistencia;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AsistenciaDetalleDto {
    private LocalDate fecha;
    private EstadoAsistencia estado;
    private String observacion;
}