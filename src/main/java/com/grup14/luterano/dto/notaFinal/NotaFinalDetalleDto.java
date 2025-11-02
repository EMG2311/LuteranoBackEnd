package com.grup14.luterano.dto.notaFinal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotaFinalDetalleDto {
    private Integer notaFinal;
    private String origen; // "MESA_EXAMEN" o "PROMEDIO_GENERAL"
    private Double promedioGeneral; // Solo si origen es PROMEDIO_GENERAL
    private Long mesaExamenId; // Solo si origen es MESA_EXAMEN
    
    public static NotaFinalDetalleDto desdeMesa(Integer notaFinal, Long mesaExamenId) {
        return NotaFinalDetalleDto.builder()
                .notaFinal(notaFinal)
                .origen("MESA_EXAMEN")
                .mesaExamenId(mesaExamenId)
                .build();
    }
    
    public static NotaFinalDetalleDto desdePromedio(Integer notaFinal, Double promedioGeneral) {
        return NotaFinalDetalleDto.builder()
                .notaFinal(notaFinal)
                .origen("PROMEDIO_GENERAL")
                .promedioGeneral(promedioGeneral)
                .build();
    }
}