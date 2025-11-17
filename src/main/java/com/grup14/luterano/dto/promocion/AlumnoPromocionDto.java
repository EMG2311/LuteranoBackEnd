package com.grup14.luterano.dto.promocion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    // Nuevo: lista de estados finales de materias
    private List<MateriaEstadoFinalDto> materiasEstadoFinal;
}