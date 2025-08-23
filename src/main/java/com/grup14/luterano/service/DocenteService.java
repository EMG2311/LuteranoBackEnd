package com.grup14.luterano.service;

import com.grup14.luterano.request.docente.DocenteRequest;
import com.grup14.luterano.request.docente.DocenteUpdateRequest;
import com.grup14.luterano.response.docente.DocenteResponse;
import com.grup14.luterano.response.docente.DocenteResponseList;

import java.util.List;

public interface DocenteService {
    DocenteResponse crearDocente(DocenteRequest docenteRequest);
    DocenteResponse updateDocente(DocenteUpdateRequest docenteRequest);
    DocenteResponse deleteDocente(Long id);
    DocenteResponseList listDocentes();
}
