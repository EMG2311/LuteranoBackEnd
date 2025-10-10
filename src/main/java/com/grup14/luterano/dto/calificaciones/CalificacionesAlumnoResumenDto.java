package com.grup14.luterano.dto.calificaciones;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionesAlumnoResumenDto {
    private Long alumnoId;
    private String dni;
    private String apellido;
    private String nombre;
    private Integer anio;
    private List<CalificacionesMateriaResumenDto> materias;

}
