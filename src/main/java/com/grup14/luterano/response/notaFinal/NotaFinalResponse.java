package com.grup14.luterano.response.notaFinal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotaFinalResponse {
    private Integer notaFinal;
    private String origen; // "MESA_EXAMEN" o "PROMEDIO_GENERAL"
    private Double promedioGeneral; // Solo si origen es PROMEDIO_GENERAL
    private Long mesaExamenId; // Solo si origen es MESA_EXAMEN
    private Long alumnoId;
    private Long materiaId;
    private Integer anio;
    private int code;
    private String mensaje;
}