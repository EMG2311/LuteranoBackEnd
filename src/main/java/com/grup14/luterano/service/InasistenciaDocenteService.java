package com.grup14.luterano.service;

import com.grup14.luterano.request.docente.InasistenciaDocenteRequest;
import com.grup14.luterano.request.docente.InasistenciaDocenteUpdateRequest;
import com.grup14.luterano.response.docente.InasistenciaDocenteResponse;

public interface InasistenciaDocenteService {

    InasistenciaDocenteResponse crearInasistenciaDocente(InasistenciaDocenteRequest inasistenciaDocenteRequest);
    InasistenciaDocenteResponse updateInasistenciaDocente(InasistenciaDocenteUpdateRequest inasistenciaDocenteUpdateRequest);
    InasistenciaDocenteResponse deleteInasistenciaDocente(Long id);


}
