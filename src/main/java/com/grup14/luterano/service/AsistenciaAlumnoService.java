package com.grup14.luterano.service;

import com.grup14.luterano.request.asistenciaAlumno.AsistenciaAlumnoBulkRequest;
import com.grup14.luterano.request.asistenciaAlumno.AsistenciaAlumnoUpdateRequest;
import com.grup14.luterano.response.asistenciaAlumno.AsistenciaAlumnoResponse;
import com.grup14.luterano.response.asistenciaAlumno.AsistenciaAlumnoResponseList;

import java.time.LocalDate;

public interface AsistenciaAlumnoService {
    AsistenciaAlumnoResponseList registrarAsistenciaCurso(AsistenciaAlumnoBulkRequest req);
    AsistenciaAlumnoResponse actualizarAsistenciaAlumno(AsistenciaAlumnoUpdateRequest req);
    AsistenciaAlumnoResponseList listarAsistenciaCursoPorFecha(Long cursoId, LocalDate fecha);
}
