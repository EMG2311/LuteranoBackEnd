package com.grup14.luterano.dto.promocion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoPromocionDto {
    
    private Long alumnoId;
    private String dni;
    private String apellido;
    private String nombre;
    private String cursoAnterior;
    private String cursoNuevo;
    private String accion; // PROMOCIONADO, REPITENTE, EGRESADO, NO_PROCESADO
    private Integer materiasDesaprobadas;
    private String motivo; // Razón si no se procesó
    private Integer repeticionesActuales;
}