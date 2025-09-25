package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.InasistenciaAlumnoDto;
import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.InasistenciaAlumno;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.mappers.InasistenciaAlumnoMapper;
import com.grup14.luterano.repository.AlumnoRepository;
import com.grup14.luterano.repository.InasistenciaAlumnoRepository;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.alumno.InasistenciaAlumnoRequest;
import com.grup14.luterano.request.alumno.InasistenciaAlumnoUpdateRequest;
import com.grup14.luterano.response.alumno.InasistenciaAlumnoResponse;
import com.grup14.luterano.response.alumno.InasistenciaAlumnoResponseList;
import com.grup14.luterano.service.InasistenciaAlumnoService;
import com.grup14.luterano.service.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;


@Service
@Transactional
public class InasistenciaAlumnoServiceImpl implements InasistenciaAlumnoService {


    @Autowired
    private InasistenciaAlumnoRepository inasistenciaAlumnoRepository;
    @Autowired
    private AlumnoRepository alumnoRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(InasistenciaAlumnoServiceImpl.class);

    @Override
    public InasistenciaAlumnoResponse crearInasistenciaAlumno(InasistenciaAlumnoRequest inasistenciaAlumnoRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }
        User usuario = optionalUser.get();

        //  Validar que el alumno exista
        Optional<Alumno> alumnoOptional = alumnoRepository.findById(inasistenciaAlumnoRequest.getAlumnoId());
        if (alumnoOptional.isEmpty()) {
            throw new IllegalArgumentException("El Alumno con el ID proporcionado no existe.");
        }
        Alumno alumno = alumnoOptional.get();

        //   Construcción de la entidad InasistenciaAlumno
        InasistenciaAlumno inasistencia = InasistenciaAlumno.builder()
                .fecha(LocalDate.now())
                .estado(inasistenciaAlumnoRequest.getEstado())
                .alumno(alumno)
                .usuario(usuario) // Asigna el usuario capturado de la sesión
                .build();

        // Guardar la entidad
        InasistenciaAlumno savedInasistencia = inasistenciaAlumnoRepository.save(inasistencia);

        // Usar el mapper solo para convertir la entidad a DTO para la respuesta
        InasistenciaAlumnoDto savedDto = InasistenciaAlumnoMapper.toDto(savedInasistencia);

        // Construir y devolver la respuesta
        return InasistenciaAlumnoResponse.builder()
                .InasistenciaAlumno(savedDto)
                .code(201)
                .mensaje("Inasistencia registrada exitosamente.")
                .build();

    }

    @Override
    @Transactional
    public InasistenciaAlumnoResponse updateInasistenciaAlumno(Long id, InasistenciaAlumnoUpdateRequest request) {
        return null;
    }

    @Override
    @Transactional
    public InasistenciaAlumnoResponse deleteInasistenciaAlumno(Long id) {
        return null;
    }

    @Override
    public InasistenciaAlumnoResponse getInasistenciaAlumnoById(Long id) {
        return null;
    }

    @Override
    public InasistenciaAlumnoResponseList listInasistenciaAlumno() {
        return null;
    }
}
