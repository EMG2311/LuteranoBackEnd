package com.grup14.luterano.dto.asistencia;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InasistenciasAlumnoDetalleDto {

    private Long alumnoId;
    private String dni;
    private String apellido;
    private String nombre;

    private Long cursoId;
    private String cursoEtiqueta;

    // Totales ponderados
    private double totalInasistencias;        // todas
    private double totalJustificadas;         // JUSTIFICADO + CON_LICENCIA
    private double totalNoJustificadas;       // resto (AUSENTE, TARDE, RETIRO, etc.)

    private List<AsistenciaDetalleDto> detalles;
}