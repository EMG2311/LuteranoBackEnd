package com.grup14.luterano.service;

import com.grup14.luterano.response.reporteRinden.ReporteRindenResponse;

public interface ReporteRindenService {
    ReporteRindenResponse listarRindenPorCurso(Long cursoId, int anio);
}
