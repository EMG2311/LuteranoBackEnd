package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.AlumnoDto;
import com.grup14.luterano.dto.DocenteDto;
import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.exeptions.AlumnoException;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import com.grup14.luterano.exeptions.DocenteException;
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
                .tutor(alumnoRequest.getTutor())
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
                        //.estado(EstadoAlumno.REGULAR) // Asignar estado por defecto
                       // .cursoActual(alumno.getCursoActual())
                        //.tutor(alumno.getTutor())
                        .build())
                .code(0)
                .mensaje("Alumno creado correctamente")
                .build();

    }

    @Override
    public AlumnoResponse updateAlumno(AlumnoUpdateRequest updateRequest){

        // 1. Buscar al alumno por su ID y lanzar una excepción si no se encuentra
        Alumno alumno = alumnoRepository.findById(updateRequest.getId())
                .orElseThrow(() -> new AlumnoException("No existe alumno con id: " + updateRequest.getId()));

        // 2. Actualizar los campos del alumno  si no son nulos
        if (updateRequest.getNombre() != null) {
            alumno.setNombre(updateRequest.getNombre());
        }
        if (updateRequest.getApellido() != null) {
            alumno.setApellido(updateRequest.getApellido());
        }
        if (updateRequest.getGenero() != null) {
            alumno.setGenero(updateRequest.getGenero());
        }
        if (updateRequest.getTipoDoc() != null) {
            alumno.setTipoDoc(updateRequest.getTipoDoc());
        }
        if (updateRequest.getDni() != null) {
            alumno.setDni(updateRequest.getDni());
        }
        if (updateRequest.getEmail() != null) {
            alumno.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getDireccion() != null) {
            alumno.setDireccion(updateRequest.getDireccion());
        }
        if (updateRequest.getTelefono() != null) {
            alumno.setTelefono(updateRequest.getTelefono());
        }
        if (updateRequest.getFechaNacimiento() != null) {
            alumno.setFechaNacimiento(updateRequest.getFechaNacimiento());
        }
        if (updateRequest.getFechaIngreso() != null) {
            alumno.setFechaIngreso(updateRequest.getFechaIngreso());
        }

        // 3. Actualizar las relaciones (Curso y Tutor) si se proporcionan los IDs ???


        // 4. Guardar los cambios en el alumno
        alumno = alumnoRepository.save(alumno);

        logger.info("Alumno actualizado correctamente con ID: {}", alumno.getId());

        // 5. Construir y devolver la respuesta
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
                       // .estado(EstadoAlumno.REGULAR) // Asignar estado por defecto
                       // .cursoActual(alumno.getCursoActual())
                        //.tutor(alumno.getTutor())
                        .build())
                .code(0)
                .mensaje("Alumno creado correctamente")
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
        List<AlumnoDto> alumnos = new ArrayList<>();
        alumnoRepository.findAll().forEach( alumno->{
            alumnos.add(AlumnoDto.builder()
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
                   // .estado(EstadoAlumno.REGULAR) // Asignar estado por defecto
                   // .cursoActual(alumno1.getCursoActual())
                    //.tutor(alumno1.getTutor())
                    .build());
        });
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
