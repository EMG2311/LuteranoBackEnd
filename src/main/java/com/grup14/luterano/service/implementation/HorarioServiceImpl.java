package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.enums.DiaSemana;
import com.grup14.luterano.repository.DocenteRepository;
import com.grup14.luterano.repository.HorarioRepository;
import com.grup14.luterano.service.HorarioService;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
@RequiredArgsConstructor
public class HorarioServiceImpl implements HorarioService {

    private final HorarioRepository horarioRepository;
    private final DocenteRepository docenteRepository;
    public boolean docenteDisponible(Long docenteId, DiaSemana dia, LocalTime desde, LocalTime hasta) {
        Docente docente=docenteRepository.findById(docenteId).orElseThrow(()->
                new RuntimeException("No existe el docente con id "+docenteId));

        return horarioRepository.findByDocenteAndDiaSemana(docente, dia)
                .stream()
                .noneMatch(h ->
                        (desde.isBefore(h.getHoraHasta()) && hasta.isAfter(h.getHoraDesde()))
                );
    }
}
