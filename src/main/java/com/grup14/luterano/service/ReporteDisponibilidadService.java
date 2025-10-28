package com.grup14.luterano.service;

import com.grup14.luterano.response.reporteDisponibilidad.DocenteDisponibilidadResponse;

public interface ReporteDisponibilidadService {
    DocenteDisponibilidadResponse disponibilidadDocente(Long docenteId);
}
