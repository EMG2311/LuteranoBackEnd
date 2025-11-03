package com.grup14.luterano.service;

import com.grup14.luterano.response.inasistenciasAlumno.InasistenciasAlumnoResponse;

public interface InasistenciasAlumnoService {

    InasistenciasAlumnoResponse listarInasistenciasPorAlumno(Long alumnoId);

    InasistenciasAlumnoResponse listarInasistenciasPorDni(String dni);
}