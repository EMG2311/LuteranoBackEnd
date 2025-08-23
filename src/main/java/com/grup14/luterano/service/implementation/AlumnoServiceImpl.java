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
import com.grup14.luterano.request.alumno.AlumnoRequest;
import com.grup14.luterano.request.alumno.AlumnoUpdateRequest;
import com.grup14.luterano.request.alumno.AsignarCursoRequest;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.response.alumno.AlumnoResponseList;
import com.grup14.luterano.response.docente.DocenteResponse;
import com.grup14.luterano.service.AlumnoService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        Optional<Alumno> existentePorDni = alumnoRepository.findByDni(alumnoRequest.getDni());
        if (existentePorDni.isPresent()) {
            throw new AlumnoException("Ya existe un alumno registrado con ese DNI");
        }

        // 2. Validar que el curso exista
        Curso curso = cursoRepository.findById(alumnoRequest.getCursoActual().getId())
                .orElseThrow(() -> new AlumnoException("El curso especificado no existe"));

        // 3. Validar que el tutor exista
        Tutor tutor = tutorRepository.findById(alumnoRequest.getTutor().getId())
                .orElseThrow(() -> new AlumnoException("El tutor especificado no existe"));

        // 4. Mapear el request a entidad usando el mapper
        Alumno alumno = AlumnoMapper.toEntity(alumnoRequest);
        alumno.setCursoActual(curso);
        alumno.setTutor(tutor);

        // 5. Guardar el alumno
        alumnoRepository.save(alumno);

        logger.info("Alumno creado correctamente con: {} {} {}", alumno.getDni(), alumno.getNombre(), alumno.getApellido());

        // 6. Retornar respuesta usando el mapper
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

        // 2. Actualizar campos simples si no son nulos
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

        // NO SE ACTUALIZA CURSO NI TUTO

        // 3. Guardar cambios en BD
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
    public AlumnoResponse asignarCurso(Long alumnoId, Long cursoId) {
        return null;
    }

    @Override
    public AlumnoResponse desasignarCurso(Long alumnoId, Long cursoId) {
        return null;
    }

}
