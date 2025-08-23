package com.grup14.luterano.service;

import com.grup14.luterano.entities.enums.DiaSemana;

import java.time.LocalTime;

public interface HorarioService {
    boolean docenteDisponible(Long docenteId, DiaSemana dia, LocalTime desde, LocalTime hasta);


    }

