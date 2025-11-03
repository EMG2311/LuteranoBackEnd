package com.grup14.luterano.service;

import com.grup14.luterano.request.aula.AulaRequest;
import com.grup14.luterano.request.aula.AulaUpdateRequest;
import com.grup14.luterano.response.aula.AulaResponse;
import com.grup14.luterano.response.aula.AulaResponseList;

public interface AulaService {

    AulaResponse crearAula(AulaRequest aulaRequest);

    AulaResponse updateAula(AulaUpdateRequest aulaUpdateRequest);

    AulaResponse deleteAula(Long id);

    AulaResponse getAulaById(Long id);

    AulaResponseList listAulas();

    AulaResponseList listarAulasSinCurso();

}
