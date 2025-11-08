package com.grup14.luterano.service;

import com.grup14.luterano.request.alumno.AlumnoFiltrosRequest;
import com.grup14.luterano.request.alumno.AlumnoRequest;
import com.grup14.luterano.request.alumno.AlumnoUpdateRequest;
import com.grup14.luterano.request.alumno.AsignarTutoresRequest;
import com.grup14.luterano.request.historialCursoRequest.HistorialCursoRequest;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.response.alumno.AlumnoResponseList;

public interface AlumnoService {

    // Métodos para manejar las operaciones CRUD de los alumnos

    AlumnoResponse crearAlumno(AlumnoRequest alumnoRequest);

    AlumnoResponse updateAlumno(AlumnoUpdateRequest alumnoUpdateRequest);

    AlumnoResponse deleteAlumno(Long id);

    AlumnoResponse buscarPorDni(String dni);

    AlumnoResponseList listAlumnos();

    AlumnoResponseList listAlumnos(AlumnoFiltrosRequest alumnoFiltrosRequest);

    AlumnoResponseList listAlumnosEgresados(); // Para consultar egresados

    AlumnoResponseList listAlumnosExcluidos(); // Para consultar excluidos por repetición

    AlumnoResponse asignarCurso(HistorialCursoRequest historialCursoRequest);
    
    // Métodos para manejar múltiples tutores
    AlumnoResponse asignarTutores(AsignarTutoresRequest request);
    
    AlumnoResponse removerTutor(Long alumnoId, Long tutorId);
    
    ///luego hacer:registrar asistencia
    /// AlumnoResponse getAlumnoById(Long id);

}
