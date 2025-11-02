package com.grup14.luterano.service;

import com.grup14.luterano.entities.enums.DiaSemana;
import com.grup14.luterano.response.modulo.ModuloEstadoListResponse;
import com.grup14.luterano.response.modulo.ModuloEstadoSemanaResponse;
import com.grup14.luterano.response.modulo.ModuloListResponse;
import com.grup14.luterano.response.modulo.ModuloReservaEstadoResponse;
import com.grup14.luterano.response.modulo.ModuloSemanaResponse;

import java.time.LocalDate;

public interface ModuloService {
    ModuloListResponse modulosLibresDelCursoPorDia(Long cursoId, DiaSemana dia);
    ModuloSemanaResponse modulosLibresDelCursoTodaLaSemana(Long cursoId);
    ModuloListResponse todosLosModulos();
    ModuloEstadoListResponse modulosDelCursoPorDiaConEstado(Long cursoId, DiaSemana dia);
    ModuloEstadoSemanaResponse modulosDelCursoSemanaConEstado(Long cursoId);
    
    // Nuevo método para reservas de espacios áulicos
    ModuloReservaEstadoResponse obtenerModulosConReservas(Long espacioAulicoId, LocalDate fecha);
}
