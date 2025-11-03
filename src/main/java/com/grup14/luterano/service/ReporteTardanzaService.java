package com.grup14.luterano.service;

import com.grup14.luterano.response.reporteTardanza.ReporteTardanzasResponseList;

import java.time.LocalDate;

public interface ReporteTardanzaService {
    ReporteTardanzasResponseList listarPorCurso(Long cursoId, LocalDate desde, LocalDate hasta, Integer limit);

    ReporteTardanzasResponseList listarTodos(LocalDate desde, LocalDate hasta, Integer limit);

}
