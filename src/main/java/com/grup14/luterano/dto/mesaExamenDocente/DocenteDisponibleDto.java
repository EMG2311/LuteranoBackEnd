package com.grup14.luterano.dto.mesaExamenDocente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocenteDisponibleDto {
    private Long docenteId;
    private String apellido;
    private String nombre;
    private String nombreCompleto;
    private boolean daLaMateria;
    private String nombreMateria; // solo si daLaMateria = true
    private boolean tieneConflictoHorario; // true si ya está en otra mesa ese día
    private String detalleConflicto; // información sobre la mesa en conflicto
}