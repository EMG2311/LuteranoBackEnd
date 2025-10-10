package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.AlumnoDto;
import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.Tutor;
import com.grup14.luterano.exeptions.TutorAlumnoException;
import com.grup14.luterano.mappers.AlumnoMapper;
import com.grup14.luterano.repository.AlumnoRepository;
import com.grup14.luterano.repository.TutorRepository;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.service.TutorAlumnoService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TutorAlumnoServiceImpl implements TutorAlumnoService {
    private final AlumnoRepository alumnoRepository;
    private final TutorRepository tutorRepository;
    public TutorAlumnoServiceImpl(AlumnoRepository alumnoRepository,TutorRepository tutorRepository){
        this.alumnoRepository=alumnoRepository;
        this.tutorRepository=tutorRepository;
    }
    @Override
    public AlumnoResponse asignarTutorAAlumno(Long idTutor, Long idAlumno) {
        Tutor tutor = tutorRepository.findById(idTutor)
                .orElseThrow(()-> new TutorAlumnoException("No existe el tutor con id " + idTutor));
        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(()-> new TutorAlumnoException("No existe el alumno con id "+ idAlumno));

        if(Objects.equals(alumno.getTutor().getId(), idTutor)){
            throw new TutorAlumnoException("El alumno ya tiene este tutor asignado");
        }
        alumno.setTutor(tutor);
        alumnoRepository.save(alumno);
        return AlumnoResponse.builder()
                .alumno(AlumnoMapper.toDto(alumno))
                .code(0)
                .mensaje("Se asigno correctamente el tutor")
                .build();
    }

    @Override
    public AlumnoResponse desasignarTutorAlumno(Long idTutor, Long idAlumno) {
        Tutor tutor = tutorRepository.findById(idTutor)
                .orElseThrow(()-> new TutorAlumnoException("No existe el tutor con id " + idTutor));
        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(()-> new TutorAlumnoException("No existe el alumno con id "+ idAlumno));

        if(!Objects.equals(alumno.getTutor().getId(), idTutor)){
            throw new TutorAlumnoException("El tutor no esta asignado a este alumno");
        }
        alumno.setTutor(null);
        alumnoRepository.save(alumno);
        return AlumnoResponse.builder()
                .alumno(AlumnoMapper.toDto(alumno))
                .code(0)
                .mensaje("Se desasigno correctamente el tutor")
                .build();
    }
}
