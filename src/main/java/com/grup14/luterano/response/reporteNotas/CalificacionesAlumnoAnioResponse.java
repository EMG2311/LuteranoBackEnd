package com.grup14.luterano.response.reporteNotas;

import com.grup14.luterano.dto.reporteNotas.CalificacionesAlumnoResumenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionesAlumnoAnioResponse {
    private CalificacionesAlumnoResumenDto calificacionesAlumnoResumenDto;
    private Integer code;
    private String mensaje;
}
