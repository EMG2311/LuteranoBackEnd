package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.AsistenciaAlumno;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import com.grup14.luterano.entities.enums.EstadoAsistencia;
import com.grup14.luterano.exeptions.AsistenciaException;
import com.grup14.luterano.mappers.AsistenciaAlumnoMapper;
import com.grup14.luterano.repository.AlumnoRepository;
import com.grup14.luterano.repository.AsistenciaAlumnoRepository;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.asistenciaAlumno.AsistenciaAlumnoBulkRequest;
import com.grup14.luterano.request.asistenciaAlumno.AsistenciaAlumnoUpdateRequest;
import com.grup14.luterano.response.asistenciaAlumno.AsistenciaAlumnoResponse;
import com.grup14.luterano.response.asistenciaAlumno.AsistenciaAlumnoResponseList;
import com.grup14.luterano.service.AsistenciaAlumnoService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class AsistenciaAlumnoServiceImpl implements AsistenciaAlumnoService {

    private final AsistenciaAlumnoRepository asistenciaAlumnoRepo;
    private final CursoRepository cursoRepo;
    private final AlumnoRepository alumnoRepo;
    private final UserRepository userRepo;

    private User currentUserOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        return userRepo.findByEmail(auth.getName()).orElse(null);
    }

    @Transactional
    public AsistenciaAlumnoResponseList registrarAsistenciaCurso(AsistenciaAlumnoBulkRequest req) {
        if (req.getCursoId() == null || req.getFecha() == null) {
            throw new AsistenciaException("Debe indicar cursoId y fecha");
        }
        Curso curso = cursoRepo.findById(req.getCursoId())
                .orElseThrow(() -> new AsistenciaException("Curso no encontrado"));

        var alumnos = alumnoRepo.findByCursoActual_Id(curso.getId()).stream()
                .filter(a -> a.getEstado() != EstadoAlumno.BORRADO)
                .toList();

        var presentes = new HashSet<>(Optional.ofNullable(req.getPresentesIds()).orElseGet(List::of));
        var overrides = Optional.ofNullable(req.getOverridesPorAlumnoId()).orElseGet(Map::of);

        User usuario = currentUserOrNull();

        List<AsistenciaAlumno> result = new ArrayList<>();

        for (Alumno a : alumnos) {
            EstadoAsistencia estadoBase = presentes.contains(a.getId()) ? EstadoAsistencia.PRESENTE : EstadoAsistencia.AUSENTE;
            EstadoAsistencia estadoFinal = overrides.getOrDefault(a.getId(), estadoBase);

            var existente = asistenciaAlumnoRepo.findByAlumno_IdAndFecha(a.getId(), req.getFecha()).orElse(null);
            if (existente == null) {
                existente = AsistenciaAlumno.builder()
                        .alumno(a)
                        .fecha(req.getFecha())
                        .estado(estadoFinal)
                        .usuario(usuario)
                        .build();
            } else {
                existente.setEstado(estadoFinal);
                existente.setUsuario(usuario);
            }
            asistenciaAlumnoRepo.save(existente);
            result.add(existente);
        }

        return AsistenciaAlumnoResponseList.builder()
                .items(result.stream().map(AsistenciaAlumnoMapper::toDto).toList())
                .code(200)
                .mensaje("OK")
                .build();
    }

    @Transactional
    public AsistenciaAlumnoResponse actualizarAsistenciaAlumno(AsistenciaAlumnoUpdateRequest req) {
        if (req.getAlumnoId() == null || req.getFecha() == null || req.getEstado() == null) {
            throw new AsistenciaException("Debe indicar alumnoId, fecha y estado");
        }

        Alumno alumno = alumnoRepo.findById(req.getAlumnoId())
                .orElseThrow(() -> new AsistenciaException("Alumno no encontrado"));

        User usuario = currentUserOrNull();

        AsistenciaAlumno asistencia = asistenciaAlumnoRepo.findByAlumno_IdAndFecha(req.getAlumnoId(), req.getFecha())
                .orElseGet(() -> AsistenciaAlumno.builder()
                        .alumno(alumno)
                        .fecha(req.getFecha())
                        .build());

        asistencia.setEstado(req.getEstado());
        asistencia.setUsuario(usuario);
        asistencia.setObservacion(req.getObservacion());
        asistenciaAlumnoRepo.save(asistencia);

        return AsistenciaAlumnoResponse.builder()
                .asistenciaAlumnoDto(AsistenciaAlumnoMapper.toDto(asistencia))
                .code(0)
                .mensaje("OK")
                .build();
    }

    @Transactional(readOnly = true)
    public AsistenciaAlumnoResponseList listarAsistenciaCursoPorFecha(Long cursoId, LocalDate fecha) {
        if (cursoId == null || fecha == null) {
            throw new AsistenciaException("Debe indicar cursoId y fecha");
        }
        cursoRepo.findById(cursoId).orElseThrow(() -> new AsistenciaException("Curso no encontrado"));

        var lista = asistenciaAlumnoRepo.findByAlumno_CursoActual_IdAndFecha(cursoId, fecha);

        return AsistenciaAlumnoResponseList.builder()
                .items(lista.stream().map(AsistenciaAlumnoMapper::toDto).toList())
                .code(200)
                .mensaje("OK")
                .build();
    }

    @Transactional
    public Long resetHistorialCompleto(Long alumnoId) {
        if (alumnoId == null) {
            throw new AsistenciaException("Debe indicar alumnoId");
        }

        // Validar que el alumno exista
        alumnoRepo.findById(alumnoId)
                .orElseThrow(() -> new AsistenciaException("Alumno no encontrado"));
        return asistenciaAlumnoRepo.deleteByAlumno_Id(alumnoId);

    }
}
