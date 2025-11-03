package com.grup14.luterano.request.alumno;

import com.grup14.luterano.entities.enums.Division;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoFiltrosRequest {
    private String nombre;
    private String apellido;
    private String dni;
    private Integer anio;
    private Division division;
}
