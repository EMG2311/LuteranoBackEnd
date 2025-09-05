package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.AlumnoDto;
import com.grup14.luterano.dto.DocenteDto;
import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.Tutor;
import com.grup14.luterano.exeptions.AlumnoException;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import com.grup14.luterano.exeptions.DocenteException;
import com.grup14.luterano.mappers.AlumnoMapper;
import com.grup14.luterano.mappers.CursoMapper;
import com.grup14.luterano.mappers.TutorMapper;
import com.grup14.luterano.repository.AlumnoRepository;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.repository.TutorRepository;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.alumno.AlumnoFiltrosRequest;
import com.grup14.luterano.request.alumno.AlumnoRequest;
import com.grup14.luterano.request.alumno.AlumnoUpdateRequest;
import com.grup14.luterano.request.alumno.AsignarCursoRequest;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.response.alumno.AlumnoResponseList;
import com.grup14.luterano.response.docente.DocenteResponse;
import com.grup14.luterano.service.AlumnoService;
import com.grup14.luterano.specification.AlumnoSpecification;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AlumnoServiceImpl implements AlumnoService {


    private final AlumnoRepository alumnoRepository;

    private final CursoRepository cursoRepository;

    private final TutorRepository tutorRepository;

    public AlumnoServiceImpl(AlumnoRepository alumnoRepository, CursoRepository cursoRepository,TutorRepository tutorRepository){
        this.alumnoRepository=alumnoRepository;
        this.cursoRepository=cursoRepository;
        this.tutorRepository=tutorRepository;
    }

    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(AlumnoServiceImpl.class);

    @Override
    @Transactional
    public AlumnoResponse crearAlumno(AlumnoRequest alumnoRequest) {  // 1. Validar si ya existe un alumno con el mismo DNI.

        alumnoRepository.findByDni(alumnoRequest.getDni())
                .ifPresent(a -> {
                    throw new AlumnoException("Ya existe un alumno registrado con ese DNI");
                });


        Curso curso = null;
        if (alumnoRequest.getCursoActual() != null && alumnoRequest.getCursoActual().getId() != null) {
            curso = cursoRepository.findById(alumnoRequest.getCursoActual().getId())
                    .orElseThrow(() -> new AlumnoException("El curso especificado no existe"));
        }

        Tutor tutor = null;
        if (alumnoRequest.getTutor() != null && alumnoRequest.getTutor().getId() != null) {
            tutor = tutorRepository.findById(alumnoRequest.getTutor().getId())
                    .orElseThrow(() -> new AlumnoException("El tutor especificado no existe"));
        }

        Alumno alumno = AlumnoMapper.toEntity(alumnoRequest);
        alumno.setCursoActual(curso);
        alumno.setTutor(tutor);

        alumnoRepository.save(alumno);

        logger.info("Alumno creado correctamente: DNI={}, Nombre={} {}",
                alumno.getDni(), alumno.getNombre(), alumno.getApellido());

        return AlumnoResponse.builder()
                .alumno(AlumnoMapper.toDto(alumno))
                .code(0)
                .mensaje("Alumno creado correctamente")
                .build();
    }

    @Override
    @Transactional
    public AlumnoResponse updateAlumno(AlumnoUpdateRequest updateRequest) {
        // 1. Buscar al alumno por su ID y lanzar excepción si no existe
        Alumno alumno = alumnoRepository.findById(updateRequest.getId())
                .orElseThrow(() -> new AlumnoException("No existe alumno con id: " + updateRequest.getId()));


        if (updateRequest.getNombre() != null) alumno.setNombre(updateRequest.getNombre());
        if (updateRequest.getApellido() != null) alumno.setApellido(updateRequest.getApellido());
        if (updateRequest.getGenero() != null) alumno.setGenero(updateRequest.getGenero());
        if (updateRequest.getTipoDoc() != null) alumno.setTipoDoc(updateRequest.getTipoDoc());
        if (updateRequest.getDni() != null) alumno.setDni(updateRequest.getDni());
        if (updateRequest.getEmail() != null) alumno.setEmail(updateRequest.getEmail());
        if (updateRequest.getDireccion() != null) alumno.setDireccion(updateRequest.getDireccion());
        if (updateRequest.getTelefono() != null) alumno.setTelefono(updateRequest.getTelefono());
        if (updateRequest.getFechaNacimiento() != null) alumno.setFechaNacimiento(updateRequest.getFechaNacimiento());
        if (updateRequest.getFechaIngreso() != null) alumno.setFechaIngreso(updateRequest.getFechaIngreso());




        alumno = alumnoRepository.save(alumno);

        logger.info("Alumno actualizado correctamente con ID: {}", alumno.getId());

        AlumnoDto alumnoDto = AlumnoMapper.toDto(alumno);

        return AlumnoResponse.builder()
                .alumno(alumnoDto)
                .code(0)
                .mensaje("Alumno actualizado correctamente")
                .build();
    }

    @Override
    public AlumnoResponse deleteAlumno(Long id) {
        Alumno alumno = alumnoRepository.findById(id).orElseThrow(() -> new AlumnoException("No existe el alumno id " + id));
        alumnoRepository.deleteById(id);
        logger.info("Se elimino correctamente el alumno " + id);
        return AlumnoResponse.builder()
                .alumno(new AlumnoDto())
                .code(0)
                .mensaje("Se elimino correctamente el alumno ")
                .build();
    }

    @Override
    public AlumnoResponseList listAlumnos() {
        List<AlumnoDto> alumnos = alumnoRepository.findAll()
                .stream()
                .map(AlumnoMapper::toDto)
                .collect(Collectors.toList());

        return AlumnoResponseList.builder()
                .alumnoDtos(alumnos)
                .code(0)
                .mensaje("Lista de alumnos obtenida correctamente")
                .build();
    }

    @Override
    public AlumnoResponseList listAlumnos(AlumnoFiltrosRequest alumnoFiltrosRequest) {
        Specification<Alumno> spec = Specification.where(AlumnoSpecification.nombreContains(alumnoFiltrosRequest.getNombre()))
                .and(AlumnoSpecification.apellidoContains(alumnoFiltrosRequest.getApellido()))
                .and(AlumnoSpecification.dniContains(alumnoFiltrosRequest.getDni()))
                .and(AlumnoSpecification.cursoAnioEquals(alumnoFiltrosRequest.getAnio()))
                .and(AlumnoSpecification.divisionEquals(alumnoFiltrosRequest.getDivision()));

        List<AlumnoDto> alumnos = alumnoRepository.findAll(spec).stream()
                .map(AlumnoMapper::toDto)
                .collect(Collectors.toList());

        return AlumnoResponseList.builder()
                .alumnoDtos(alumnos)
                .code(0)
                .mensaje("OK")
                .build();

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
