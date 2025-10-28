package com.grup14.luterano.dto.reporteAnual;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MateriaAnualDetalleDto {
    private Long materiaId;
    private String materiaNombre;

    // Resumen de calificaciones por etapa (reuso del esquema de ReporteNotas)
    private Integer[] e1Notas; // tamaño 4 (puede tener nulls)
    private Integer[] e2Notas; // tamaño 4 (puede tener nulls)
    private Double e1;         // promedio etapa 1
    private Double e2;         // promedio etapa 2
    private Double pg;         // promedio general
    private String estado;     // Aprobado/Desaprobado por promedio


    private Integer notaFinal;     // última nota final de mesa en el año (si existe)
    private String estadoFinal;    // APROBADO/DESAPROBADO (según notaFinal)
    private String estadoMateria;  // estado en HistorialMateria (APROBADA/DESAPROBADA/CURSANDO/..)
}
