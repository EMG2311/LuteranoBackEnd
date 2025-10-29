package com.grup14.luterano.service;

import com.grup14.luterano.dto.reporteDesempenoDocente.ReporteDesempenoDocenteDto;
import com.grup14.luterano.request.ReporteDesempenoDocente.ReporteDesempenoDocenteFiltroRequest;
import com.grup14.luterano.response.reporteDesempeñoDocente.ReporteDesempenoDocenteResponse;

import java.util.List;

public interface ReporteDesempenoDocenteService {

    ReporteDesempenoDocenteResponse generarReporteDesempenoDocente(ReporteDesempenoDocenteFiltroRequest filtros);
}
