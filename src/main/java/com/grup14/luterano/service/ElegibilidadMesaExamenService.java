package com.grup14.luterano.service;

import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.MateriaCurso;
import com.grup14.luterano.entities.enums.CondicionRinde;

import java.time.LocalDate;
import java.util.Optional;

public interface ElegibilidadMesaExamenService {
    Optional<CondicionRinde> determinarCondicionRinde(
            Alumno alumno,
            MateriaCurso materiaCurso,
            LocalDate fechaMesa
    );
}
