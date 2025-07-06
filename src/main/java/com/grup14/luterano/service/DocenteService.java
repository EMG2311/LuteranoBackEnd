package com.grup14.luterano.service;

import com.grup14.luterano.request.docente.DocenteRequest;
import com.grup14.luterano.response.docente.DocenteResponse;

public interface DocenteService {
    DocenteResponse crearDocente(DocenteRequest docenteRequest);
    DocenteResponse updateDocente(DocenteRequest docenteRequest);
    DocenteResponse deleteDocente(Long id);
    DocenteResponse asignarMateria(Long docenteId,Long materiaId);
    DocenteResponse desasignarMateria(Long docenteId,Long materiaId);

}
