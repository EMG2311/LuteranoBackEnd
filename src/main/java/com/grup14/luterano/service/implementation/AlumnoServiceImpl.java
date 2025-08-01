package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.AlumnoDto;
import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.exeptions.AlumnoException;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import com.grup14.luterano.repository.AlumnoRepository;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.repository.TutorRepository;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.alumno.AlumnoRequest;
import com.grup14.luterano.request.alumno.AlumnoUpdateRequest;
import com.grup14.luterano.request.alumno.AsignarCursoRequest;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.response.alumno.AlumnoResponseList;
import com.grup14.luterano.service.AlumnoService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AlumnoServiceImpl implements AlumnoService {

    /// Implementa logica de negocio para manejar las operaciones CRUD de AlumnoService
    @Autowired  ///  inyecta el repositorio de Alumno (dependencia)
    private AlumnoRepository alumnoRepository;
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private TutorRepository tutorRepository;


    ///  ¿FALTAN MAS ??///
    /// NOSE QUE HACE!!--------------
    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(AlumnoServiceImpl.class);

    ///------------------------------------------------///
    @Override
    @Transactional
    public AlumnoResponse crearAlumno(AlumnoRequest alumnoRequest) {
        // 1. Validar si ya existe un alumno con el mismo DNI.
        Optional<Alumno> existentePorDni = alumnoRepository.findByDni(alumnoRequest.getDni());
        if (existentePorDni.isPresent()) {
            throw new AlumnoException("Ya existe un alumno registrado con ese DNI");
        }
        // Construir la entidad Alumno a partir del Request DTO.

        Alumno alumno = Alumno.builder()
                .nombre(alumnoRequest.getNombre())
                .apellido(alumnoRequest.getApellido())
                .genero(alumnoRequest.getGenero())
                .tipoDoc(alumnoRequest.getTipoDoc())
                .dni(alumnoRequest.getDni())
                .email(alumnoRequest.getEmail())
                .direccion(alumnoRequest.getDireccion())
                .telefono(alumnoRequest.getTelefono())
                .fechaNacimiento(alumnoRequest.getFechaNacimiento())
                .fechaIngreso(alumnoRequest.getFechaIngreso())
                .estado(alumnoRequest.getEstado())    ///  preguntar si esta bien !!----------
                .cursoActual(alumnoRequest.getCursoActual())
                //.tutor(alumnoRequest.getTutor())
                .build();
        // Guardar el alumno en la base de datos.
        alumnoRepository.save(alumno);
        logger.info("Alumno creado correctamente con: {} {} {}", alumno.getDni(),alumno.getNombre(),alumno.getApellido());
        return AlumnoResponse.builder()
                .alumno(AlumnoDto.builder()
                        .id(alumno.getId())
                        .nombre(alumno.getNombre())
                        .apellido(alumno.getApellido())
                        .genero(alumno.getGenero())
                        .tipoDoc(alumno.getTipoDoc())
                        .dni(alumno.getDni())
                        .email(alumno.getEmail())
                        .direccion(alumno.getDireccion())
                        .telefono(alumno.getTelefono())
                        .fechaNacimiento(alumno.getFechaNacimiento())
                        .fechaIngreso(alumno.getFechaIngreso())
                        .estado(EstadoAlumno.REGULAR) // Asignar estado por defecto
                        .cursoActual(alumno.getCursoActual())
                        //.tutor(alumno.getTutor())
                        .build())
                .code(201)  // Código HTTP 201 para creación exitosa
                .mensaje("Alumno creado correctamente")
                .build();

    }

    @Override
    public AlumnoResponse updateAlumno(AlumnoUpdateRequest alumnoUpdateRequest) {
        return null;
    }

    @Override
    public AlumnoResponse deleteAlumno(Long id) {
        return null;
    }

    @Override
    public AlumnoResponseList listAlumnos() {
        return null;
    }

    @Override
    public AlumnoResponse asignarCurso(Long alumnoId, Long cursoId) {
        return null;
    }

    @Override
    public AlumnoResponse desasignarCurso(Long alumnoId, Long cursoId) {
        return null;
    }

}
