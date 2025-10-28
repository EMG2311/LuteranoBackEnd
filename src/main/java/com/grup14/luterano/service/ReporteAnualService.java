package com.grup14.luterano.service;

import com.grup14.luterano.response.reporteAnual.ReporteAnualAlumnoResponse;

public interface ReporteAnualService {
    ReporteAnualAlumnoResponse informeAnualAlumno(Long alumnoId, int anio);
}
