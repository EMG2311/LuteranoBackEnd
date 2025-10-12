package com.grup14.luterano.service;

import com.grup14.luterano.request.docente.InasistenciaDocenteRequest;
import com.grup14.luterano.request.docente.InasistenciaDocenteUpdateRequest;
import com.grup14.luterano.response.docente.InasistenciaDocenteResponse;
import com.grup14.luterano.response.docente.InasistenciaDocenteResponseList;

public interface InasistenciaDocenteService {

    InasistenciaDocenteResponse crearInasistenciaDocente(InasistenciaDocenteRequest inasistenciaDocenteRequest);
    InasistenciaDocenteResponse updateInasistenciaDocente(Long id,InasistenciaDocenteUpdateRequest request);
    InasistenciaDocenteResponse deleteInasistenciaDocente(Long id);
    InasistenciaDocenteResponse getInasistenciaDocenteById(Long id);
    InasistenciaDocenteResponseList listInasistenciasDocente();


}
