package com.grup14.luterano.service;

import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.response.alumno.AlumnoResponseList;

import java.util.List;

public interface TutorAlumnoService {
    AlumnoResponse asignarTutorAAlumno(Long idATutor, Long idAlumno);

    AlumnoResponse desasignarTutorAlumno(Long idTutor, Long idAlumno);

    AlumnoResponseList listarAlumnosACargo(Long tutorId);
    
    // Nuevos métodos para manejar múltiples tutores
    AlumnoResponse asignarMultiplesTutoresAAlumno(List<Long> tutorIds, Long idAlumno);
    
    AlumnoResponse desasignarMultiplesTutoresDeAlumno(List<Long> tutorIds, Long idAlumno);
}
