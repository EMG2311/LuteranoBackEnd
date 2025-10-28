package com.grup14.luterano.service;

import com.grup14.luterano.response.reporteAsistenciaPerfecta.AsistenciaPerfectaResponse;

public interface ReporteAsistenciaPerfectaService {
    AsistenciaPerfectaResponse listarPorAnio(int anio);
}
