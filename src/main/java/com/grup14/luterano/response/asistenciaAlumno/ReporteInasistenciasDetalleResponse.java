package com.grup14.luterano.response.asistenciaAlumno;

import com.grup14.luterano.dto.asistencia.InasistenciasAlumnoDetalleDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReporteInasistenciasDetalleResponse {

    private Integer anio;
    private Long cursoId;
    private Long alumnoId;

    private List<InasistenciasAlumnoDetalleDto> filas;

    private int code;
    private String mensaje;
}
