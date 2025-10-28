package com.grup14.luterano.response.reporteAnual;

import com.grup14.luterano.dto.reporteAnual.ReporteAnualAlumnoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteAnualAlumnoResponse {
    private ReporteAnualAlumnoDto data;
    private Integer code;
    private String mensaje;
}
