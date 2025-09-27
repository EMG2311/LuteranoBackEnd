package com.grup14.luterano.service;

import com.grup14.luterano.request.alumno.AlumnoFiltrosRequest;
import com.grup14.luterano.request.alumno.AlumnoRequest;
import com.grup14.luterano.request.alumno.AlumnoUpdateRequest;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.response.alumno.AlumnoResponseList;

public interface AlumnoService {

    // Métodos para manejar las operaciones CRUD de los alumnos

    AlumnoResponse crearAlumno(AlumnoRequest alumnoRequest);
    AlumnoResponse updateAlumno(AlumnoUpdateRequest  alumnoUpdateRequest);
    AlumnoResponse deleteAlumno(Long id);

    AlumnoResponseList listAlumnos();
    AlumnoResponseList listAlumnos(AlumnoFiltrosRequest alumnoFiltrosRequest);
    AlumnoResponse asignarCurso(Long alumnoId, Long cursoId);
    AlumnoResponse desasignarCurso(Long alumnoId, Long cursoId);

    ///luego hacer:registrar asistencia
    /// AlumnoResponse getAlumnoById(Long id);

}
