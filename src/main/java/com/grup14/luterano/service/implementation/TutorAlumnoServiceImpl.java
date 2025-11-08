package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.Tutor;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import com.grup14.luterano.exeptions.TutorAlumnoException;
import com.grup14.luterano.exeptions.TutorException;
import com.grup14.luterano.mappers.AlumnoMapper;
import com.grup14.luterano.repository.AlumnoRepository;
import com.grup14.luterano.repository.TutorRepository;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.response.alumno.AlumnoResponseList;
import com.grup14.luterano.service.TutorAlumnoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TutorAlumnoServiceImpl implements TutorAlumnoService {
    private final AlumnoRepository alumnoRepository;
    private final TutorRepository tutorRepository;

    public TutorAlumnoServiceImpl(AlumnoRepository alumnoRepository, TutorRepository tutorRepository) {
        this.alumnoRepository = alumnoRepository;
        this.tutorRepository = tutorRepository;
    }

    @Override
    public AlumnoResponse asignarTutorAAlumno(Long idTutor, Long idAlumno) {
        Tutor tutor = tutorRepository.findById(idTutor)
                .orElseThrow(() -> new TutorAlumnoException("No existe el tutor con id " + idTutor));
        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(() -> new TutorAlumnoException("No existe el alumno con id " + idAlumno));

        // Verificar si el tutor ya está asignado
        if (alumno.getTutores() != null && alumno.getTutores().stream()
                .anyMatch(t -> Objects.equals(t.getId(), idTutor))) {
            throw new TutorAlumnoException("El alumno ya tiene este tutor asignado");
        }
        
        // Agregar el tutor a la lista de tutores
        if (alumno.getTutores() == null) {
            alumno.setTutores(new ArrayList<>());
        }
        alumno.getTutores().add(tutor);
        alumnoRepository.save(alumno);
        
        return AlumnoResponse.builder()
                .alumno(AlumnoMapper.toDto(alumno))
                .code(0)
                .mensaje("Se asignó correctamente el tutor")
                .build();
    }

    @Override
    public AlumnoResponse desasignarTutorAlumno(Long idTutor, Long idAlumno) {
        // Verificar que el tutor existe
        tutorRepository.findById(idTutor)
                .orElseThrow(() -> new TutorAlumnoException("No existe el tutor con id " + idTutor));
        
        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(() -> new TutorAlumnoException("No existe el alumno con id " + idAlumno));

        // Verificar si el tutor está asignado
        if (alumno.getTutores() == null || alumno.getTutores().stream()
                .noneMatch(t -> Objects.equals(t.getId(), idTutor))) {
            throw new TutorAlumnoException("El tutor no está asignado a este alumno");
        }
        
        // Remover el tutor de la lista
        alumno.getTutores().removeIf(t -> Objects.equals(t.getId(), idTutor));
        alumnoRepository.save(alumno);
        
        return AlumnoResponse.builder()
                .alumno(AlumnoMapper.toDto(alumno))
                .code(0)
                .mensaje("Se desasignó correctamente el tutor")
                .build();
    }

    @Transactional(readOnly = true)
    public AlumnoResponseList listarAlumnosACargo(Long tutorId) {
        if (tutorId == null) {
            throw new TutorException("Debe indicar tutorId");
        }

        tutorRepository.findById(tutorId)
                .orElseThrow(() -> new TutorException("No existe tutor con id=" + tutorId));

        List<Alumno> alumnos = alumnoRepository.findByTutores_IdAndEstadoNot(tutorId, EstadoAlumno.BORRADO);

        return AlumnoResponseList.builder()
                .alumnoDtos(alumnos.stream().map(AlumnoMapper::toDto).toList())
                .code(200)
                .mensaje("OK")
                .build();
    }

    @Override
    @Transactional
    public AlumnoResponse asignarMultiplesTutoresAAlumno(List<Long> tutorIds, Long idAlumno) {
        if (tutorIds == null || tutorIds.isEmpty()) {
            throw new TutorAlumnoException("Debe proporcionar al menos un tutor para asignar");
        }

        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(() -> new TutorAlumnoException("No existe el alumno con id " + idAlumno));

        List<Tutor> tutoresParaAsignar = new ArrayList<>();
        List<String> tutoresYaAsignados = new ArrayList<>();

        // Verificar que todos los tutores existen y cuáles ya están asignados
        for (Long tutorId : tutorIds) {
            Tutor tutor = tutorRepository.findById(tutorId)
                    .orElseThrow(() -> new TutorAlumnoException("No existe el tutor con id " + tutorId));

            // Verificar si el tutor ya está asignado
            if (alumno.getTutores() != null && alumno.getTutores().stream()
                    .anyMatch(t -> Objects.equals(t.getId(), tutorId))) {
                tutoresYaAsignados.add(tutor.getNombre() + " " + tutor.getApellido());
            } else {
                tutoresParaAsignar.add(tutor);
            }
        }

        // Inicializar la lista de tutores si es null
        if (alumno.getTutores() == null) {
            alumno.setTutores(new ArrayList<>());
        }

        // Agregar solo los tutores que no estaban asignados
        alumno.getTutores().addAll(tutoresParaAsignar);
        alumnoRepository.save(alumno);

        // Construir mensaje de respuesta
        StringBuilder mensaje = new StringBuilder();
        if (!tutoresParaAsignar.isEmpty()) {
            mensaje.append("Se asignaron ").append(tutoresParaAsignar.size()).append(" tutores correctamente");
        }
        if (!tutoresYaAsignados.isEmpty()) {
            if (mensaje.length() > 0) {
                mensaje.append(". ");
            }
            mensaje.append("Los siguientes tutores ya estaban asignados: ")
                    .append(String.join(", ", tutoresYaAsignados));
        }

        return AlumnoResponse.builder()
                .alumno(AlumnoMapper.toDto(alumno))
                .code(0)
                .mensaje(mensaje.toString())
                .build();
    }

    @Override
    @Transactional
    public AlumnoResponse desasignarMultiplesTutoresDeAlumno(List<Long> tutorIds, Long idAlumno) {
        if (tutorIds == null || tutorIds.isEmpty()) {
            throw new TutorAlumnoException("Debe proporcionar al menos un tutor para desasignar");
        }

        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(() -> new TutorAlumnoException("No existe el alumno con id " + idAlumno));

        if (alumno.getTutores() == null || alumno.getTutores().isEmpty()) {
            throw new TutorAlumnoException("El alumno no tiene tutores asignados");
        }

        List<String> tutoresDesasignados = new ArrayList<>();
        List<String> tutoresNoAsignados = new ArrayList<>();

        // Verificar que todos los tutores existen y procesar la desasignación
        for (Long tutorId : tutorIds) {
            Tutor tutor = tutorRepository.findById(tutorId)
                    .orElseThrow(() -> new TutorAlumnoException("No existe el tutor con id " + tutorId));

            // Verificar si el tutor está asignado
            boolean removed = alumno.getTutores().removeIf(t -> Objects.equals(t.getId(), tutorId));
            
            if (removed) {
                tutoresDesasignados.add(tutor.getNombre() + " " + tutor.getApellido());
            } else {
                tutoresNoAsignados.add(tutor.getNombre() + " " + tutor.getApellido());
            }
        }

        alumnoRepository.save(alumno);

        // Construir mensaje de respuesta
        StringBuilder mensaje = new StringBuilder();
        if (!tutoresDesasignados.isEmpty()) {
            mensaje.append("Se desasignaron ").append(tutoresDesasignados.size()).append(" tutores correctamente");
        }
        if (!tutoresNoAsignados.isEmpty()) {
            if (mensaje.length() > 0) {
                mensaje.append(". ");
            }
            mensaje.append("Los siguientes tutores no estaban asignados: ")
                    .append(String.join(", ", tutoresNoAsignados));
        }

        return AlumnoResponse.builder()
                .alumno(AlumnoMapper.toDto(alumno))
                .code(0)
                .mensaje(mensaje.toString())
                .build();
    }
}
