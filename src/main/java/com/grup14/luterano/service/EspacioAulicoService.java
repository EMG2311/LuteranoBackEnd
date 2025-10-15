package com.grup14.luterano.service;

import com.grup14.luterano.request.espacioAulico.EspacioAulicoRequest;
import com.grup14.luterano.request.espacioAulico.EspacioAulicoUpdateRequest;
import com.grup14.luterano.response.espacioAulico.EspacioAulicoResponse;
import com.grup14.luterano.response.espacioAulico.EspacioAulicoResponseList;

public interface EspacioAulicoService {

    EspacioAulicoResponse crearEspacioAulico(EspacioAulicoRequest request);
    EspacioAulicoResponse updateEspacioAulico(EspacioAulicoUpdateRequest request);
    EspacioAulicoResponse deleteEspacioAulico(Long id);
    EspacioAulicoResponseList listEspacioAulico();

}
