package com.grup14.luterano.response;

import com.grup14.luterano.dto.ReporteLibreDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data@Builder
public class ReporteLibresResponse {
    private Integer code;
    private String mensaje;

    private Integer anio;
    private Long cursoId;         // null = todo el colegio
    private String cursoNombre;   // si aplica

    private Integer totalLibres;
    private List<ReporteLibreDto> filas;
}
