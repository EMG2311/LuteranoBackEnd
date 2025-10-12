package com.grup14.luterano.service;

import com.grup14.luterano.dto.AsistenciaAlumnoDto;
import com.grup14.luterano.request.AsistenciaAlumnoBulkRequest;
import com.grup14.luterano.request.AsistenciaAlumnoUpdateRequest;
import com.grup14.luterano.response.asistenciaAlumno.AsistenciaAlumnoResponseList;

import java.time.LocalDate;

public interface AsistenciaAlumnoService {
    AsistenciaAlumnoResponseList registrarAsistenciaCurso(AsistenciaAlumnoBulkRequest req);
    AsistenciaAlumnoDto actualizarAsistenciaAlumno(AsistenciaAlumnoUpdateRequest req);
    AsistenciaAlumnoResponseList listarAsistenciaCursoPorFecha(Long cursoId, LocalDate fecha);
}
