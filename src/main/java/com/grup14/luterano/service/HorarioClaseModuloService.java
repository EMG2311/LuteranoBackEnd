package com.grup14.luterano.service;

import com.grup14.luterano.request.horarioClaseModulo.SlotHorarioRequest;
import com.grup14.luterano.response.horarioClaseModulo.HorarioClaseModuloResponse;

import java.util.List;

public interface HorarioClaseModuloService {
    HorarioClaseModuloResponse asignarHorariosAMateriaDeCurso(Long cursoId, Long materiaId, List<SlotHorarioRequest> slots);

    HorarioClaseModuloResponse desasignarHorariosAMateriaDeCurso(Long cursoId, Long materiaId, List<SlotHorarioRequest> slots);
}
