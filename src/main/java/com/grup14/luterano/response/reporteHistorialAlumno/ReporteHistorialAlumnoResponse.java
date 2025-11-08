package com.grup14.luterano.response.reporteHistorialAlumno;

import com.grup14.luterano.dto.reporteHistorialAlumno.ReporteHistorialAlumnoDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReporteHistorialAlumnoResponse {
    private Integer code;
    private String mensaje;
    private ReporteHistorialAlumnoDto historial;
}