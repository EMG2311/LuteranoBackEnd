package com.grup14.luterano.service;

import com.grup14.luterano.response.ReporteLibresResponse;

public interface ReporteLibreService {
    ReporteLibresResponse listarLibres(Integer anio, Long cursoId);
}
