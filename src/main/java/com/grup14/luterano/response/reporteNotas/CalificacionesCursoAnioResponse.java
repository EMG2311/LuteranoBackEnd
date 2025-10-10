package com.grup14.luterano.response.reporteNotas;

import com.grup14.luterano.dto.CursoDto;
import com.grup14.luterano.dto.reporteNotas.CalificacionesAlumnoResumenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Builder@Data@AllArgsConstructor@NoArgsConstructor
public class CalificacionesCursoAnioResponse {
    private CursoDto curso;
    private Integer anio;
    private List<CalificacionesAlumnoResumenDto> alumnos;
    private int total;   // cantidad de alumnos
    private int code;
    private String mensaje;
}
