package com.grup14.luterano.dto.reporteNotas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionesMateriaResumenDto {
    private Long materiaId;
    private String materiaNombre;
    private Integer[] e1Notas;
    private Integer[] e2Notas;
    private Double e1;
    private Double e2;
    private Double pg;
    private String estado;
    
    // NUEVOS CAMPOS: Notas de mesa de examen
    private Integer co;  // Nota de coloquio (si rindió)
    private Integer ex;  // Nota de examen final (si rindió)
    private Double pfa;  // Promedio Final Anual (nota final calculada)
}