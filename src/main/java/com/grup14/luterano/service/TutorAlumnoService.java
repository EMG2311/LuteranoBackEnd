package com.grup14.luterano.service;

import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.response.alumno.AlumnoResponseList;

public interface TutorAlumnoService {
    AlumnoResponse asignarTutorAAlumno(Long idATutor, Long idAlumno);
    AlumnoResponse desasignarTutorAlumno(Long idTutor,Long idAlumno);
    AlumnoResponseList listarAlumnosACargo(Long tutorId);
}
