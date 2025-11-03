package com.grup14.luterano.service;

import com.grup14.luterano.request.Preceptor.PreceptorRequest;
import com.grup14.luterano.request.Preceptor.PreceptorUpdateRequest;
import com.grup14.luterano.response.Preceptor.PreceptorResponse;
import com.grup14.luterano.response.Preceptor.PreceptorResponseList;

public interface PreceptorService {
    PreceptorResponse crearPreceptor(PreceptorRequest PreceptorRequest);

    PreceptorResponse updatePreceptor(PreceptorUpdateRequest PreceptorRequest);

    PreceptorResponse deletePreceptor(Long id);

    PreceptorResponseList listPreceptores();

    PreceptorResponseList listAllPreceptores();
}
