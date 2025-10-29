package com.grup14.luterano.dto.reporteDesempenoDocente;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetalleDictadoDto {

    private String materiaCursoNombre; // MATEMATICAS - CUARTO B
    private Double promedioAlumnos;     // 7.5
    private Double tasaAprobacion;      // 85%
}
