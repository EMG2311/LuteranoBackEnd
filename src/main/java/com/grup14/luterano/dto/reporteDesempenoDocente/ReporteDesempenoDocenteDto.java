package com.grup14.luterano.dto.reporteDesempenoDocente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDesempenoDocenteDto {

    private String docenteNombreCompleto;
    private Double tasaInasistencias; // 2%
    private List<DetalleDictadoDto> detallesDictado;

}
