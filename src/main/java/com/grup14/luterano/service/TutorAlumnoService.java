package com.grup14.luterano.service;

import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.response.alumno.AlumnoResponse;

public interface TutorAlumnoService {
    AlumnoResponse asignarTutorAAlumno(Long idATutor, Long idAlumno);
    AlumnoResponse desasignarTutorAlumno(Long idTutor,Long idAlumno);
}
