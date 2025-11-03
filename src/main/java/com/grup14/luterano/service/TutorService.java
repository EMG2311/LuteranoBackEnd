package com.grup14.luterano.service;


import com.grup14.luterano.request.tutor.TutorRequest;
import com.grup14.luterano.request.tutor.TutorUpdateRequest;
import com.grup14.luterano.response.tutor.TutorResponse;
import com.grup14.luterano.response.tutor.TutorResponseList;

public interface TutorService {
    TutorResponse crearTutor(TutorRequest TutorRequest);

    TutorResponse updateTutor(TutorUpdateRequest TutorRequest);

    TutorResponse deleteTutor(Long id);

    TutorResponseList listTutores();
}
