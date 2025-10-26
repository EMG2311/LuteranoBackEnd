package com.grup14.luterano.service;

import com.grup14.luterano.request.mesaExamen.AgregarConvocadosRequest;
import com.grup14.luterano.request.mesaExamen.MesaExamenCreateRequest;
import com.grup14.luterano.request.mesaExamen.MesaExamenUpdateRequest;
import com.grup14.luterano.response.mesaExamen.MesaExamenListResponse;
import com.grup14.luterano.response.mesaExamen.MesaExamenResponse;

import java.util.List;
import java.util.Map;
public interface MesaExamenService {
    MesaExamenResponse crear(MesaExamenCreateRequest req);     // requiere turnoId
    MesaExamenResponse actualizar(MesaExamenUpdateRequest req);
    MesaExamenResponse eliminar(Long id);

    MesaExamenResponse obtener(Long id);
    MesaExamenListResponse listarPorMateriaCurso(Long materiaCursoId);
    MesaExamenListResponse listarPorCurso(Long cursoId);
    MesaExamenListResponse listarPorTurno(Long turnoId);       // útil para vista por turno

    MesaExamenResponse agregarConvocados(Long mesaId, AgregarConvocadosRequest req); // NO pide turno: se toma de la mesa
    MesaExamenResponse quitarConvocado(Long mesaId, Long alumnoId);

    MesaExamenResponse cargarNotasFinales(Long mesaId, Map<Long, Integer> notasPorAlumnoId);
    MesaExamenResponse finalizar(Long mesaId); // pasa a FINALIZADA
}