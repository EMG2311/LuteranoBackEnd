package com.grup14.luterano.response.reporteDesempeñoDocente;


import com.grup14.luterano.dto.reporteDesempenoDocente.FiltrosReporteDto;
import com.grup14.luterano.dto.reporteDesempenoDocente.ReporteDesempenoDocenteDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReporteDesempenoDocenteResponse {

    private FiltrosReporteDto filtros;

    private List<ReporteDesempenoDocenteDto> filas;

    private int code;
    private String mensaje;

}
