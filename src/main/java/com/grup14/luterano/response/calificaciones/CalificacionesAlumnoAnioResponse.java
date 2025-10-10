package com.grup14.luterano.response.calificaciones;

import com.grup14.luterano.dto.calificaciones.CalificacionesAlumnoResumenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionesAlumnoAnioResponse {
    private CalificacionesAlumnoResumenDto calificacionesAlumnoResumenDto;
    private Integer code;
    private String mensaje;
}
