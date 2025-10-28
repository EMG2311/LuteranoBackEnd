package com.grup14.luterano.dto.reporteAnual;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InasistenciasResumenDto {
    private Double ponderado; // suma ponderada (AUSENTE=1, TARDE/RETIRO=0.25, JUSTIFICADO/LICENCIA=1)
    private Long ausentes;
    private Long tardes;
    private Long justificados;
    private Long conLicencia;
    private Long retiros;
}
