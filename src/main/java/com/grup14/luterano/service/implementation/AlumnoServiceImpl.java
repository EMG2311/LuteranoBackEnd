package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.AlumnoDto;
import com.grup14.luterano.dto.TutorDto;
import com.grup14.luterano.entities.*;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import com.grup14.luterano.entities.enums.EstadoMateriaAlumno;
import com.grup14.luterano.exeptions.AlumnoException;
import com.grup14.luterano.mappers.AlumnoMapper;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.request.alumno.AlumnoFiltrosRequest;
import com.grup14.luterano.request.alumno.AlumnoRequest;
import com.grup14.luterano.request.alumno.AlumnoUpdateRequest;
import com.grup14.luterano.request.alumno.AsignarTutoresRequest;
import com.grup14.luterano.request.historialCursoRequest.HistorialCursoRequest;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.response.alumno.AlumnoResponseList;
import com.grup14.luterano.service.AlumnoService;
import com.grup14.luterano.specification.AlumnoSpecification;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AlumnoServiceImpl implements AlumnoService {


    private final AlumnoRepository alumnoRepository;

    private final CursoRepository cursoRepository;

    private final TutorRepository tutorRepository;

    private final HistorialMateriaRepository historialMateriaRepository;
    private final HistorialCursoRepository historialCursoRepository;

    private final CicloLectivoRepository cicloLectivoRepository;

    public AlumnoServiceImpl(AlumnoRepository alumnoRepository, CursoRepository cursoRepository,
                             TutorRepository tutorRepository, CicloLectivoRepository cicloLectivoRepository,
                             HistorialCursoRepository historialCursoRepository, HistorialMateriaRepository historialMateriaRepository) {
        this.alumnoRepository = alumnoRepository;
        this.cursoRepository = cursoRepository;
        this.tutorRepository = tutorRepository;
        this.historialCursoRepository = historialCursoRepository;
        this.cicloLectivoRepository = cicloLectivoRepository;
        this.historialMateriaRepository = historialMateriaRepository;
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

        // Validación obligatoria: el curso debe estar especificado
        if (alumnoRequest.getCursoActual() == null || alumnoRequest.getCursoActual().getId() == null) {
            throw new AlumnoException("Es obligatorio asignar un curso al crear un alumno");
        }

        Curso curso = cursoRepository.findById(alumnoRequest.getCursoActual().getId())
                .orElseThrow(() -> new AlumnoException("El curso especificado no existe"));

        // Manejar múltiples tutores
        List<Tutor> tutores = new ArrayList<>();
        if (alumnoRequest.getTutores() != null && !alumnoRequest.getTutores().isEmpty()) {
            for (TutorDto tutorDto : alumnoRequest.getTutores()) {
                if (tutorDto != null && tutorDto.getId() != null) {
                    Tutor tutor = tutorRepository.findById(tutorDto.getId())
                            .orElseThrow(() -> new AlumnoException("El tutor con ID " + tutorDto.getId() + " no existe"));
                    tutores.add(tutor);
                }
            }
        }

        Alumno alumno = AlumnoMapper.toEntity(alumnoRequest);
        alumno.setCursoActual(curso);
        alumno.setTutores(tutores);
        alumno.setEstado(EstadoAlumno.REGULAR);
        alumno = alumnoRepository.save(alumno);

        // Crear el historial de curso automáticamente (ahora siempre se ejecuta)
        // Obtener el ciclo lectivo activo
        LocalDate hoy = LocalDate.now();
        CicloLectivo cicloActivo = cicloLectivoRepository
                .findByFechaDesdeLessThanEqualAndFechaHastaGreaterThanEqual(hoy, hoy)
                .orElseThrow(() -> new AlumnoException("No hay un ciclo lectivo activo para la fecha actual"));

        HistorialCurso historialCurso = HistorialCurso.builder()
                .alumno(alumno)
                .curso(curso)
                .cicloLectivo(cicloActivo)
                .fechaDesde(hoy)
                .fechaHasta(null) // Se cerrará cuando termine el ciclo lectivo o cambie de curso
                .build();
        historialCursoRepository.save(historialCurso);

        logger.info("Historial de curso creado automáticamente para alumno: DNI={}, Curso={}, Ciclo={}",
                alumno.getDni(), curso.getAnio() + "°" + curso.getDivision().toString(), cicloActivo.getNombre());

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
        List<AlumnoDto> alumnos = alumnoRepository.findByEstadoNotIn(
                        List.of(EstadoAlumno.EGRESADO, EstadoAlumno.BORRADO, EstadoAlumno.EXCLUIDO_POR_REPETICION))
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
                .and(AlumnoSpecification.divisionEquals(alumnoFiltrosRequest.getDivision()))
                .and(AlumnoSpecification.alumnosActivos()); // Excluir egresados y borrados

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
    public AlumnoResponseList listAlumnosEgresados() {
        List<AlumnoDto> alumnos = alumnoRepository.findByEstado(EstadoAlumno.EGRESADO)
                .stream()
                .map(AlumnoMapper::toDto)
                .collect(Collectors.toList());

        return AlumnoResponseList.builder()
                .alumnoDtos(alumnos)
                .code(0)
                .mensaje("Lista de alumnos egresados obtenida correctamente")
                .build();
    }

    @Override
    public AlumnoResponseList listAlumnosExcluidos() {
        List<AlumnoDto> alumnos = alumnoRepository.findByEstado(EstadoAlumno.EXCLUIDO_POR_REPETICION)
                .stream()
                .map(AlumnoMapper::toDto)
                .collect(Collectors.toList());

        return AlumnoResponseList.builder()
                .alumnoDtos(alumnos)
                .code(0)
                .mensaje("Lista de alumnos excluidos por repetición obtenida correctamente")
                .build();
    }


    @Override
    @Transactional
    public AlumnoResponse asignarCurso(HistorialCursoRequest request) {
        Alumno alumno = alumnoRepository.findById(request.getAlumnoId())
                .orElseThrow(() -> new AlumnoException("Alumno no encontrado"));
        Curso cursoDestino = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new AlumnoException("Curso no encontrado"));
        CicloLectivo ciclo = cicloLectivoRepository.findById(request.getCicloLectivoId())
                .orElseThrow(() -> new AlumnoException("Ciclo lectivo no encontrado"));

        LocalDate hoy = LocalDate.now();

        var hcAbiertoOpt = historialCursoRepository
                .findByAlumno_IdAndCicloLectivo_IdAndFechaHastaIsNull(alumno.getId(), ciclo.getId());

        if (hcAbiertoOpt.isPresent() &&
                hcAbiertoOpt.get().getCurso().getId().equals(cursoDestino.getId())) {
            throw new AlumnoException("El alumno ya está asignado a ese curso en el ciclo actual.");
        }

        if (hcAbiertoOpt.isPresent()) {
            HistorialCurso hcOrigen = hcAbiertoOpt.get();

            List<HistorialMateria> hms = historialMateriaRepository.findAllByHistorialCursoId(hcOrigen.getId());
            for (HistorialMateria hm : hms) {
                if (hm.getEstado() == null || hm.getEstado() == EstadoMateriaAlumno.CURSANDO) {
                    hm.setEstado(EstadoMateriaAlumno.TRASLADADA);
                }
            }
            historialMateriaRepository.saveAll(hms);

            hcOrigen.setFechaHasta(hoy.minusDays(1));
            historialCursoRepository.save(hcOrigen);
        }

        HistorialCurso nuevo = HistorialCurso.builder()
                .alumno(alumno)
                .curso(cursoDestino)
                .cicloLectivo(ciclo)
                .fechaDesde(hoy)
                .build();
        historialCursoRepository.save(nuevo);

        alumno.setCursoActual(cursoDestino);
        alumno.getHistorialCursos().add(nuevo);
        alumnoRepository.save(alumno);

        return AlumnoResponse.builder()
                .alumno(AlumnoMapper.toDto(alumno))
                .code(0)
                .mensaje("Alumno asignado correctamente")
                .build();
    }

    @Override
    public AlumnoResponse buscarPorDni(String dni) {
        if (dni == null || dni.isBlank()) {
            throw new AlumnoException("Debe indicar un DNI válido.");
        }

        Alumno alumno = alumnoRepository.findByDni(dni.trim())
                .orElseThrow(() -> new AlumnoException("No existe un alumno con DNI " + dni));

        return AlumnoResponse.builder()
                .alumno(AlumnoMapper.toDto(alumno))
                .code(0)
                .mensaje("OK")
                .build();
    }

    @Override
    @Transactional
    public AlumnoResponse asignarTutores(AsignarTutoresRequest request) {
        // Validar que el alumno existe
        Alumno alumno = alumnoRepository.findById(request.getAlumnoId())
                .orElseThrow(() -> new AlumnoException("No existe el alumno con ID " + request.getAlumnoId()));

        // Validar y obtener todos los tutores
        List<Tutor> tutores = new ArrayList<>();
        for (Long tutorId : request.getTutorIds()) {
            Tutor tutor = tutorRepository.findById(tutorId)
                    .orElseThrow(() -> new AlumnoException("No existe el tutor con ID " + tutorId));
            tutores.add(tutor);
        }

        // Asignar los tutores al alumno
        alumno.setTutores(tutores);
        alumno = alumnoRepository.save(alumno);

        return AlumnoResponse.builder()
                .alumno(AlumnoMapper.toDto(alumno))
                .code(0)
                .mensaje("Tutores asignados correctamente")
                .build();
    }

    @Override
    @Transactional
    public AlumnoResponse removerTutor(Long alumnoId, Long tutorId) {
        // Validar que el alumno existe
        Alumno alumno = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new AlumnoException("No existe el alumno con ID " + alumnoId));

        // Validar que el tutor existe
        Tutor tutorARemover = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new AlumnoException("No existe el tutor con ID " + tutorId));

        // Remover el tutor de la lista de tutores del alumno
        boolean removed = alumno.getTutores().removeIf(tutor -> tutor.getId().equals(tutorId));
        
        if (!removed) {
            throw new AlumnoException("El tutor no estaba asignado al alumno");
        }

        alumno = alumnoRepository.save(alumno);

        return AlumnoResponse.builder()
                .alumno(AlumnoMapper.toDto(alumno))
                .code(0)
                .mensaje("Tutor removido correctamente")
                .build();
    }

}
