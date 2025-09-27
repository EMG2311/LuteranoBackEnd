package com.grup14.luterano.service;

import com.grup14.luterano.request.alumno.InasistenciaAlumnoRequest;
import com.grup14.luterano.request.alumno.InasistenciaAlumnoUpdateRequest;
import com.grup14.luterano.response.alumno.InasistenciaAlumnoResponse;
import com.grup14.luterano.response.alumno.InasistenciaAlumnoResponseList;

public interface InasistenciaAlumnoService {

    InasistenciaAlumnoResponse crearInasistenciaAlumno(InasistenciaAlumnoRequest inasistenciaAlumnoRequest);

    InasistenciaAlumnoResponse updateInasistenciaAlumno(Long id , InasistenciaAlumnoUpdateRequest request);

    InasistenciaAlumnoResponse deleteInasistenciaAlumno(Long id);

    InasistenciaAlumnoResponse getInasistenciaAlumnoById(Long id);

    InasistenciaAlumnoResponseList listInasistenciaAlumno();

}
