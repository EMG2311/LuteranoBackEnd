// service/implementation/MesaExamenServiceImpl.java
package com.grup14.luterano.service.implementation;
import com.grup14.luterano.dto.MesaExamenAlumnoDto;
import com.grup14.luterano.dto.MesaExamenDto;
import com.grup14.luterano.entities.*;
import com.grup14.luterano.entities.enums.EstadoConvocado;
import com.grup14.luterano.entities.enums.EstadoMesaExamen;
import com.grup14.luterano.exeptions.MesaExamenException;
import com.grup14.luterano.mappers.MesaExamenMapper;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.request.mesaExamen.AgregarConvocadosRequest;
import com.grup14.luterano.request.mesaExamen.MesaExamenCreateRequest;
import com.grup14.luterano.request.mesaExamen.MesaExamenUpdateRequest;
import com.grup14.luterano.response.mesaExamen.MesaExamenListResponse;
import com.grup14.luterano.response.mesaExamen.MesaExamenResponse;
import com.grup14.luterano.service.MesaExamenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MesaExamenServiceImpl implements MesaExamenService {

    private static final Logger log = LoggerFactory.getLogger(MesaExamenServiceImpl.class);

    private final MesaExamenRepository mesaRepo;
    private final MesaExamenAlumnoRepository mesaAluRepo;
    private final MateriaCursoRepository materiaCursoRepo;
    private final TurnoExamenRepository turnoRepo;
    private final AlumnoRepository alumnoRepo;
    private final AulaRepository aulaRepo;

    @Override
    public MesaExamenResponse crear(MesaExamenCreateRequest req) {
        if (req.getMateriaCursoId()==null || req.getFecha()==null || req.getTurnoId()==null)
            return MesaExamenResponse.builder().code(-1).mensaje("materiaCursoId, turnoId y fecha son obligatorios").build();

        MateriaCurso mc = materiaCursoRepo.findById(req.getMateriaCursoId())
                .orElseThrow(() -> new MesaExamenException("MateriaCurso no encontrado"));

        TurnoExamen turno = turnoRepo.findById(req.getTurnoId())
                .orElseThrow(() -> new MesaExamenException("Turno no encontrado"));

        // Validación: fecha dentro del turno
        if (req.getFecha().isBefore(turno.getFechaInicio()) || req.getFecha().isAfter(turno.getFechaFin())) {
            throw new MesaExamenException("La fecha de la mesa debe estar dentro del turno ("+
                    turno.getFechaInicio()+" a "+ turno.getFechaFin()+")");
        }

        MesaExamen m = new MesaExamen();
        m.setMateriaCurso(mc);
        m.setTurno(turno);
        m.setFecha(req.getFecha());
        if (req.getAulaId()!=null) {
            Aula aula = aulaRepo.findById(req.getAulaId())
                    .orElseThrow(() -> new MesaExamenException("Aula no encontrada"));
            m.setAula(aula);
        }
        m.setEstado(EstadoMesaExamen.CREADA);
        mesaRepo.save(m);

        log.info("Mesa creada id={} mc={} turno={} fecha={}", m.getId(), mc.getId(), turno.getId(), m.getFecha());
        return MesaExamenResponse.builder().code(0).mensaje("Mesa creada").mesa(MesaExamenMapper.toDto(m, true)).build();
    }

    @Override
    public MesaExamenResponse actualizar(MesaExamenUpdateRequest req) {
        MesaExamen m = mesaRepo.findById(req.getId())
                .orElseThrow(() -> new MesaExamenException("Mesa no encontrada"));
        assertEditable(m);

        if (req.getFecha()!=null) {
            TurnoExamen t = m.getTurno();
            if (req.getFecha().isBefore(t.getFechaInicio()) || req.getFecha().isAfter(t.getFechaFin())) {
                throw new MesaExamenException("La nueva fecha no cae dentro del turno: " +
                        t.getFechaInicio() + " .. " + t.getFechaFin());
            }
            m.setFecha(req.getFecha());
        }
        if (req.getMateriaCursoId()!=null) {
            MateriaCurso mc = materiaCursoRepo.findById(req.getMateriaCursoId())
                    .orElseThrow(() -> new MesaExamenException("MateriaCurso no encontrado"));
            m.setMateriaCurso(mc);
        }
        if (req.getAulaId()!=null) {
            Aula aula = aulaRepo.findById(req.getAulaId())
                    .orElseThrow(() -> new MesaExamenException("Aula no encontrada"));
            m.setAula(aula);
        }
        mesaRepo.save(m);
        return MesaExamenResponse.builder().code(0).mensaje("Mesa actualizada").mesa(MesaExamenMapper.toDto(m, true)).build();
    }

    @Override
    public MesaExamenResponse eliminar(Long id) {
        MesaExamen m = mesaRepo.findById(id)
                .orElseThrow(() -> new MesaExamenException("Mesa no encontrada"));
        assertEditable(m);
        mesaRepo.delete(m);
        return MesaExamenResponse.builder().code(0).mensaje("Mesa eliminada").build();
    }

    @Override
    public MesaExamenResponse obtener(Long id) {
        MesaExamen m = mesaRepo.findById(id)
                .orElseThrow(() -> new MesaExamenException("Mesa no encontrada"));
        return MesaExamenResponse.builder().code(0).mensaje("OK").mesa(MesaExamenMapper.toDto(m, true)).build();
    }

    @Override
    public MesaExamenListResponse listarPorMateriaCurso(Long materiaCursoId) {
        var list = mesaRepo.findByMateriaCursoIdWithAlumnos(materiaCursoId);
        var dtos = list.stream().map(mm -> MesaExamenMapper.toDto(mm, true)).toList();
        return MesaExamenListResponse.builder().code(0).mensaje("OK").total(dtos.size()).mesas(dtos).build();
    }

    @Override
    public MesaExamenListResponse listarPorCurso(Long cursoId) {
        var list = mesaRepo.findByCursoIdWithAlumnos(cursoId);
        var dtos = list.stream().map(mm -> MesaExamenMapper.toDto(mm, true)).toList();
        return MesaExamenListResponse.builder().code(0).mensaje("OK").total(dtos.size()).mesas(dtos).build();
    }

    @Override
    public MesaExamenListResponse listarPorTurno(Long turnoId) {
        var list = mesaRepo.findByTurnoIdWithAlumnos(turnoId);
        var dtos = list.stream().map(mm -> MesaExamenMapper.toDto(mm, true)).toList();
        return MesaExamenListResponse.builder().code(0).mensaje("OK").total(dtos.size()).mesas(dtos).build();
    }

    @Override
    public MesaExamenResponse agregarConvocados(Long mesaId, AgregarConvocadosRequest req) {
        MesaExamen m = mesaRepo.findById(mesaId)
                .orElseThrow(() -> new MesaExamenException("Mesa no encontrada"));
        assertEditable(m);

        if (req.getAlumnoIds()==null || req.getAlumnoIds().isEmpty())
            throw new MesaExamenException("Debe enviar alumnoIds");

        for (Long aId : req.getAlumnoIds()) {
            if (mesaAluRepo.existsByMesaExamen_IdAndAlumno_Id(mesaId, aId)) continue;

            Alumno a = alumnoRepo.findById(aId)
                    .orElseThrow(() -> new MesaExamenException("Alumno no encontrado: " + aId));

            MesaExamenAlumno link = MesaExamenAlumno.builder()
                    .alumno(a)
                    .mesaExamen(m)
                    .estado(EstadoConvocado.CONVOCADO)
                    .notaFinal(null)
                    .turno(m.getTurno()) // sincronizado con la mesa
                    .build();
            m.getAlumnos().add(link);
        }
        mesaRepo.save(m);
        return MesaExamenResponse.builder().code(0).mensaje("Convocados agregados").mesa(MesaExamenMapper.toDto(m, true)).build();
    }

    @Override
    public MesaExamenResponse quitarConvocado(Long mesaId, Long alumnoId) {
        MesaExamen m = mesaRepo.findById(mesaId)
                .orElseThrow(() -> new MesaExamenException("Mesa no encontrada"));
        assertEditable(m);
        mesaAluRepo.deleteByMesaExamen_IdAndAlumno_Id(mesaId, alumnoId);
        m = mesaRepo.findById(mesaId).orElseThrow();
        return MesaExamenResponse.builder().code(0).mensaje("Convocado eliminado").mesa(MesaExamenMapper.toDto(m, true)).build();
    }

    @Override
    public MesaExamenResponse cargarNotasFinales(Long mesaId, Map<Long, Integer> notasPorAlumnoId) {
        MesaExamen m = mesaRepo.findById(mesaId)
                .orElseThrow(() -> new MesaExamenException("Mesa no encontrada"));
        assertEditable(m);
        if (notasPorAlumnoId==null || notasPorAlumnoId.isEmpty())
            throw new MesaExamenException("No se enviaron notas");

        Map<Long, MesaExamenAlumno> idx = m.getAlumnos().stream()
                .collect(Collectors.toMap(ma -> ma.getAlumno().getId(), ma -> ma));

        for (var e : notasPorAlumnoId.entrySet()) {
            Long alumnoId = e.getKey();
            Integer nota = e.getValue();
            MesaExamenAlumno ma = idx.get(alumnoId);
            if (ma != null) {
                ma.setNotaFinal(nota);
                if (nota != null) {
                    ma.setEstado(nota >= 6 ? EstadoConvocado.APROBADO : EstadoConvocado.DESAPROBADO);
                }
            }
        }
        mesaRepo.save(m);
        return MesaExamenResponse.builder().code(0).mensaje("Notas finales cargadas").mesa(MesaExamenMapper.toDto(m, true)).build();
    }

    @Override
    public MesaExamenResponse finalizar(Long mesaId) {
        MesaExamen m = mesaRepo.findById(mesaId)
                .orElseThrow(() -> new MesaExamenException("Mesa no encontrada"));
        if (m.getEstado() == EstadoMesaExamen.FINALIZADA) {
            return MesaExamenResponse.builder().code(0).mensaje("La mesa ya estaba finalizada").mesa(MesaExamenMapper.toDto(m, true)).build();
        }
        m.setEstado(EstadoMesaExamen.FINALIZADA);
        mesaRepo.save(m);
        return MesaExamenResponse.builder().code(0).mensaje("Mesa finalizada").mesa(MesaExamenMapper.toDto(m, true)).build();
    }

    // ------ helpers -------
    private static void assertEditable(MesaExamen m) {
        if (m.getEstado() == EstadoMesaExamen.FINALIZADA)
            throw new MesaExamenException("La mesa está finalizada y no puede modificarse.");
    }
}