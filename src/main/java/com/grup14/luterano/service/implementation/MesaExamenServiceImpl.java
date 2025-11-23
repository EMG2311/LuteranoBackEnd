package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.mesaExamen.AlumnoDebeMateriaDto;
import com.grup14.luterano.dto.mesaExamenDocente.DocenteDisponibleDto;
import com.grup14.luterano.dto.mesaExamenDocente.MesaExamenDocenteDto;
import com.grup14.luterano.entities.*;
import com.grup14.luterano.entities.enums.*;
import com.grup14.luterano.exeptions.MesaExamenException;
import com.grup14.luterano.mappers.MesaExamenMapper;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.request.mesaExamen.AgregarConvocadosRequest;
import com.grup14.luterano.request.mesaExamen.MesaExamenCreateRequest;
import com.grup14.luterano.request.mesaExamen.MesaExamenUpdateRequest;
import com.grup14.luterano.request.mesaExamen.MesasExamenMasivasRequest;
import com.grup14.luterano.request.mesaExamenDocente.AsignarDocentesRequest;
import com.grup14.luterano.response.AlumnosDebenMateriaResponse;
import com.grup14.luterano.response.mesaExamen.MesaExamenListResponse;
import com.grup14.luterano.response.mesaExamen.MesaExamenResponse;
import com.grup14.luterano.response.mesaExamenDocente.DocentesDisponiblesResponse;
import com.grup14.luterano.response.mesaExamenDocente.MesaExamenDocentesResponse;
import com.grup14.luterano.service.MesaExamenService;
import com.grup14.luterano.service.MesaExamenValidacionService;
import com.grup14.luterano.service.ReporteRindenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MesaExamenServiceImpl implements MesaExamenService {

    private static final Logger log = LoggerFactory.getLogger(MesaExamenServiceImpl.class);

    private final CursoRepository cursoRepository;
    private final HistorialCursoRepository historialCursoRepository;
    private final MesaExamenRepository mesaRepo;
    private final MesaExamenAlumnoRepository mesaAluRepo;
    private final MateriaCursoRepository materiaCursoRepo;
    private final TurnoExamenRepository turnoRepo;
    private final AlumnoRepository alumnoRepo;
    private final AulaRepository aulaRepo;
    private final DocenteRepository docenteRepo;
    private final MesaExamenDocenteRepository mesaExamenDocenteRepo;
    private final MesaExamenValidacionService validacionService;
    private final ReporteRindenService reporteRindenService;
    private final HistorialMateriaRepository historialMateriaRepository;

    // ================================================================
    // CRUD MESA
    // ================================================================
    @Override
    public MesaExamenResponse crear(MesaExamenCreateRequest req) {
        if (req.getMateriaCursoId() == null || req.getFecha() == null || req.getTurnoId() == null) {
            return MesaExamenResponse.builder()
                    .code(-1)
                    .mensaje("materiaCursoId, turnoId y fecha son obligatorios")
                    .build();
        }

        MateriaCurso mc = materiaCursoRepo.findById(req.getMateriaCursoId())
                .orElseThrow(() -> new MesaExamenException("MateriaCurso no encontrado"));

        TurnoExamen turno = turnoRepo.findById(req.getTurnoId())
                .orElseThrow(() -> new MesaExamenException("Turno no encontrado"));

        // Validación: fecha dentro del turno
        if (req.getFecha().isBefore(turno.getFechaInicio()) || req.getFecha().isAfter(turno.getFechaFin())) {
            throw new MesaExamenException("La fecha de la mesa debe estar dentro del turno (" +
                    turno.getFechaInicio() + " a " + turno.getFechaFin() + ")");
        }

        MesaExamen m = new MesaExamen();
        m.setMateriaCurso(mc);
        m.setTurno(turno);
        m.setFecha(req.getFecha());
        m.setTipoMesa(req.getTipoMesa());

        // horario (puede ser null, pero se exigirá para docentes)
        m.setHoraInicio(req.getHoraInicio());
        m.setHoraFin(req.getHoraFin());

        if (req.getAulaId() != null) {
            Aula aula = aulaRepo.findById(req.getAulaId())
                    .orElseThrow(() -> new MesaExamenException("Aula no encontrada"));
            m.setAula(aula);
        }
        m.setEstado(EstadoMesaExamen.CREADA);

        // Validar configuración (sin docentes todavía)
        validacionService.validarConfiguracionMesa(m);

        mesaRepo.save(m);

        // Sincronizar otras mesas finales de la misma materia/turno (por si ya existían)
        sincronizarMesasFinalMateria(m);

        log.info("Mesa creada id={} mc={} turno={} fecha={} hora={}–{}",
                m.getId(), mc.getId(), turno.getId(), m.getFecha(), m.getHoraInicio(), m.getHoraFin());

        return MesaExamenResponse.builder()
                .code(0)
                .mensaje("Mesa creada")
                .mesa(MesaExamenMapper.toDto(m, true))
                .build();
    }

    @Override
    public MesaExamenResponse actualizar(MesaExamenUpdateRequest req) {
        MesaExamen m = mesaRepo.findById(req.getId())
                .orElseThrow(() -> new MesaExamenException("Mesa no encontrada"));
        assertEditable(m);

        if (req.getFecha() != null) {
            TurnoExamen t = m.getTurno();
            if (req.getFecha().isBefore(t.getFechaInicio()) || req.getFecha().isAfter(t.getFechaFin())) {
                throw new MesaExamenException("La nueva fecha no cae dentro del turno: " +
                        t.getFechaInicio() + " .. " + t.getFechaFin());
            }
            m.setFecha(req.getFecha());
        }
        if (req.getHoraInicio() != null) {
            m.setHoraInicio(req.getHoraInicio());
        }
        if (req.getHoraFin() != null) {
            m.setHoraFin(req.getHoraFin());
        }
        if (req.getMateriaCursoId() != null) {
            MateriaCurso mc = materiaCursoRepo.findById(req.getMateriaCursoId())
                    .orElseThrow(() -> new MesaExamenException("MateriaCurso no encontrado"));
            m.setMateriaCurso(mc);
        }
        if (req.getAulaId() != null) {
            Aula aula = aulaRepo.findById(req.getAulaId())
                    .orElseThrow(() -> new MesaExamenException("Aula no encontrada"));
            m.setAula(aula);
        }
        if (req.getTipoMesa() != null) {
            // Validar que se pueda cambiar el tipo de mesa
            try {
                validacionService.validarCambioTipoMesa(m, req.getTipoMesa());
            } catch (IllegalArgumentException e) {
                throw new MesaExamenException(e.getMessage());
            }
            m.setTipoMesa(req.getTipoMesa());
        }

        // Validar configuración antes de guardar
        validacionService.validarConfiguracionMesa(m);

        mesaRepo.save(m);

        // Si es mesa final, sincronizar fecha/hora/aula/docentes con las otras de la misma materia+turno
        sincronizarMesasFinalMateria(m);

        return MesaExamenResponse.builder()
                .code(0)
                .mensaje("Mesa actualizada")
                .mesa(MesaExamenMapper.toDto(m, true))
                .build();
    }

    @Override
    public MesaExamenResponse eliminar(Long id) {
        MesaExamen m = mesaRepo.findById(id)
                .orElseThrow(() -> new MesaExamenException("Mesa no encontrada"));
        assertEditable(m);
        mesaRepo.delete(m);
        return MesaExamenResponse.builder()
                .code(0)
                .mensaje("Mesa eliminada")
                .build();
    }

    @Override
    public MesaExamenResponse obtener(Long id) {
        MesaExamen m = mesaRepo.findByIdWithAlumnos(id)
                .orElseThrow(() -> new MesaExamenException("Mesa no encontrada"));
        return MesaExamenResponse.builder()
                .code(0)
                .mensaje("OK")
                .mesa(MesaExamenMapper.toDto(m, true))
                .build();
    }

    @Override
    public MesaExamenListResponse listarPorMateriaCurso(Long materiaCursoId) {
        var list = mesaRepo.findByMateriaCursoIdWithAlumnos(materiaCursoId);
        var dtos = list.stream().map(mm -> MesaExamenMapper.toDto(mm, true)).toList();
        return MesaExamenListResponse.builder()
                .code(0)
                .mensaje("OK")
                .total(dtos.size())
                .mesas(dtos)
                .build();
    }

    @Override
    public MesaExamenListResponse listarPorCurso(Long cursoId) {
        var list = mesaRepo.findByCursoIdWithAlumnos(cursoId);
        var dtos = list.stream().map(mm -> MesaExamenMapper.toDto(mm, true)).toList();
        return MesaExamenListResponse.builder()
                .code(0)
                .mensaje("OK")
                .total(dtos.size())
                .mesas(dtos)
                .build();
    }

    @Override
    public MesaExamenListResponse listarPorTurno(Long turnoId) {
        var list = mesaRepo.findByTurnoIdWithAlumnos(turnoId);
        var dtos = list.stream().map(mm -> MesaExamenMapper.toDto(mm, true)).toList();
        return MesaExamenListResponse.builder()
                .code(0)
                .mensaje("OK")
                .total(dtos.size())
                .mesas(dtos)
                .build();
    }

    // ================================================================
    // CONVOCADOS / ALUMNOS
    // ================================================================
    @Override
    public MesaExamenResponse agregarConvocados(Long mesaId, AgregarConvocadosRequest req) {
        MesaExamen m = mesaRepo.findById(mesaId)
                .orElseThrow(() -> new MesaExamenException("Mesa no encontrada"));
        assertEditable(m);

        if (req.getAlumnoIds() == null || req.getAlumnoIds().isEmpty()) {
            throw new MesaExamenException("Debe enviar alumnoIds");
        }

        // Año lectivo de la cursada (enero-febrero-marzo pertenecer al año anterior)
        int anioCursada = resolverAnioCursada(m.getFecha());
        Long materiaId = m.getMateriaCurso().getMateria().getId();

        var reporteRinden = reporteRindenService
                .listarRindenPorCurso(m.getMateriaCurso().getCurso().getId(), anioCursada);

        for (Long aId : req.getAlumnoIds()) {
            if (mesaAluRepo.existsByMesaExamen_IdAndAlumno_Id(mesaId, aId)) {
                continue;
            }

            Alumno a = alumnoRepo.findById(aId)
                    .orElseThrow(() -> new MesaExamenException("Alumno no encontrado: " + aId));

            var filaAlumnoOpt = reporteRinden.getFilas().stream()
                    .filter(f -> f.getAlumnoId().equals(aId)
                            && f.getMateriaId().equals(materiaId))
                    .findFirst();

            CondicionRinde condicionRinde;
            if (filaAlumnoOpt.isPresent()) {
                condicionRinde = filaAlumnoOpt.get().getCondicion();
            } else {
                // Buscar si tiene la materia pendiente en años previos
                condicionRinde = obtenerCondicionDesdeHistorial(m, a, materiaId, anioCursada);
            }

            MesaExamenAlumno link = MesaExamenAlumno.builder()
                    .alumno(a)
                    .mesaExamen(m)
                    .estado(EstadoConvocado.CONVOCADO)
                    .condicionRinde(condicionRinde)
                    .notaFinal(null)
                    .turno(m.getTurno())
                    .build();

            try {
                validacionService.validarInscribirAlumno(m, link);
            } catch (IllegalArgumentException e) {
                throw new MesaExamenException("No se puede agregar el alumno " +
                        a.getApellido() + ", " + a.getNombre() + ": " + e.getMessage());
            }

            m.getAlumnos().add(link);
        }

        mesaRepo.save(m);
        return MesaExamenResponse.builder()
                .code(0)
                .mensaje("Convocados agregados")
                .mesa(MesaExamenMapper.toDto(m, true))
                .build();
    }

    @Override
    public MesaExamenResponse quitarConvocado(Long mesaId, Long alumnoId) {
        MesaExamen m = mesaRepo.findByIdWithAlumnos(mesaId)
                .orElseThrow(() -> new MesaExamenException("Mesa no encontrada"));
        assertEditable(m);

        log.info("Antes de quitar: Mesa {} tiene {} alumnos convocados", mesaId, m.getAlumnos().size());

        boolean removed = m.getAlumnos().removeIf(mesaAlumno ->
                mesaAlumno.getAlumno().getId().equals(alumnoId));

        if (!removed) {
            throw new MesaExamenException("El alumno no está convocado a esta mesa");
        }

        log.info("Después de quitar: Mesa {} tiene {} alumnos convocados", mesaId, m.getAlumnos().size());

        mesaRepo.save(m);

        return MesaExamenResponse.builder()
                .code(0)
                .mensaje("Convocado eliminado")
                .mesa(MesaExamenMapper.toDto(m, true))
                .build();
    }

    @Override
    public MesaExamenResponse cargarNotasFinales(Long mesaId, Map<Long, Integer> notasPorAlumnoId) {
        MesaExamen m = mesaRepo.findById(mesaId)
                .orElseThrow(() -> new MesaExamenException("Mesa no encontrada"));
        assertEditable(m);
        if (notasPorAlumnoId == null || notasPorAlumnoId.isEmpty()) {
            throw new MesaExamenException("No se enviaron notas");
        }

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

                    // Si aprobó la mesa, marcar la previa como aprobada
                    if (nota >= 6) {
                        actualizarMateriaPendienteComoAprobada(m, ma);
                    }
                }
            }
        }
        mesaRepo.save(m);
        return MesaExamenResponse.builder()
                .code(0)
                .mensaje("Notas finales cargadas")
                .mesa(MesaExamenMapper.toDto(m, true))
                .build();
    }

    @Override
    public MesaExamenResponse finalizar(Long mesaId) {
        MesaExamen m = mesaRepo.findById(mesaId)
                .orElseThrow(() -> new MesaExamenException("Mesa no encontrada"));
        if (m.getEstado() == EstadoMesaExamen.FINALIZADA) {
            return MesaExamenResponse.builder()
                    .code(0)
                    .mensaje("La mesa ya estaba finalizada")
                    .mesa(MesaExamenMapper.toDto(m, true))
                    .build();
        }
        m.setEstado(EstadoMesaExamen.FINALIZADA);
        mesaRepo.save(m);
        return MesaExamenResponse.builder()
                .code(0)
                .mensaje("Mesa finalizada")
                .mesa(MesaExamenMapper.toDto(m, true))
                .build();
    }

    // ================================================================
    // DOCENTES
    // ================================================================
    @Override
    public DocentesDisponiblesResponse listarDocentesDisponibles(Long mesaExamenId) {
        MesaExamen mesa = mesaRepo.findById(mesaExamenId)
                .orElseThrow(() -> new MesaExamenException("Mesa de examen no encontrada con ID: " + mesaExamenId));

        if (!mesa.tieneHorarioDefinido()) {
            throw new MesaExamenException(
                    "Debe definir fecha y horario (horaInicio / horaFin) de la mesa antes de consultar docentes disponibles."
            );
        }

        Long materiaId = mesa.getMateriaCurso().getMateria().getId();
        String nombreMateria = mesa.getMateriaCurso().getMateria().getNombre();
        LocalDate fechaMesa = mesa.getFecha();
        LocalTime horaInicio = mesa.getHoraInicio();
        LocalTime horaFin = mesa.getHoraFin();

        List<Docente> todosDocentes = docenteRepo.findAll();

        // 🔹 1) docentes que dan la materia en el año (MateriaCurso)
        Set<Long> docentesQueDALaMateria = materiaCursoRepo.findByMateriaId(materiaId)
                .stream()
                .filter(mc -> mc.getDocente() != null)
                .map(mc -> mc.getDocente().getId())
                .collect(Collectors.toSet());

        // 🔹 2) docentes que dan la materia en ESTA mesa (jurado marcado como esDocenteMateria)
        mesa.getDocentes().stream()
                .filter(MesaExamenDocente::isEsDocenteMateria)
                .map(med -> med.getDocente().getId())
                .forEach(docentesQueDALaMateria::add);

        List<Long> docenteIds = todosDocentes.stream().map(Docente::getId).toList();

        List<MesaExamen> mesasConflicto = mesaRepo.findMesasConflictoParaDocentesEnHorario(
                fechaMesa, horaInicio, horaFin, docenteIds, mesaExamenId
        );

        Map<Long, List<MesaExamen>> conflictosPorDocente = new HashMap<>();
        for (MesaExamen meConf : mesasConflicto) {
            for (MesaExamenDocente med : meConf.getDocentes()) {
                Long dId = med.getDocente().getId();
                if (!docenteIds.contains(dId)) continue;
                conflictosPorDocente
                        .computeIfAbsent(dId, k -> new ArrayList<>())
                        .add(meConf);
            }
        }

        List<DocenteDisponibleDto> docentesDto = todosDocentes.stream()
                .map(docente -> {
                    Long dId = docente.getId();
                    List<MesaExamen> listaConflictos = conflictosPorDocente.getOrDefault(dId, List.of());

                    Optional<MesaExamen> conflictoRealOpt = listaConflictos.stream()
                            .filter(otra -> !esMismaMesaFinalDeMateria(mesa, otra))
                            .findFirst();

                    boolean tieneConflicto = conflictoRealOpt.isPresent();
                    String detalleConflicto = null;

                    if (tieneConflicto) {
                        MesaExamen primeraConflicto = conflictoRealOpt.get();
                        detalleConflicto = String.format("Ya asignado a %s - %s %s (%s a %s)",
                                primeraConflicto.getMateriaCurso().getMateria().getNombre(),
                                primeraConflicto.getMateriaCurso().getCurso().getAnio(),
                                primeraConflicto.getMateriaCurso().getCurso().getDivision(),
                                primeraConflicto.getHoraInicio(),
                                primeraConflicto.getHoraFin()
                        );
                    }

                    boolean esDocenteMateriaAqui = docentesQueDALaMateria.contains(docente.getId());

                    return DocenteDisponibleDto.builder()
                            .docenteId(docente.getId())
                            .apellido(docente.getApellido())
                            .nombre(docente.getNombre())
                            .nombreCompleto(docente.getApellido() + ", " + docente.getNombre())
                            // 🔹 true si la da en el año O en esta mesa como esDocenteMateria
                            .daLaMateria(esDocenteMateriaAqui)
                            // podés dejar así o siempre setear nombreMateria = nombreMateria
                            .nombreMateria(esDocenteMateriaAqui ? nombreMateria : null)
                            .tieneConflictoHorario(tieneConflicto)
                            .detalleConflicto(detalleConflicto)
                            .build();
                })
                .sorted(Comparator.comparing((DocenteDisponibleDto d) -> d.isTieneConflictoHorario())
                        .thenComparing(d -> !d.isDaLaMateria())
                        .thenComparing(DocenteDisponibleDto::getApellido)
                        .thenComparing(DocenteDisponibleDto::getNombre))
                .collect(Collectors.toList());

        return DocentesDisponiblesResponse.builder()
                .docentes(docentesDto)
                .mesaExamenId(mesaExamenId)
                .nombreMateria(nombreMateria)
                .totalDocentes(docentesDto.size())
                .docentesQueDALaMateria((int) docentesQueDALaMateria.size())
                .code(0)
                .mensaje("OK")
                .build();
    }

    @Override
    public MesaExamenDocentesResponse asignarDocentes(Long mesaExamenId, AsignarDocentesRequest request) {
        MesaExamen mesa = mesaRepo.findById(mesaExamenId)
                .orElseThrow(() -> new MesaExamenException("Mesa de examen no encontrada con ID: " + mesaExamenId));

        if (!mesa.tieneHorarioDefinido()) {
            throw new MesaExamenException("Antes de asignar docentes debe estar definida la fecha y horario de la mesa");
        }

        // ---- validaciones básicas ----
        List<Docente> docentes = docenteRepo.findAllById(request.getDocenteIds());
        if (docentes.size() != request.getDocenteIds().size()) {
            throw new MesaExamenException("Uno o más docentes no existen");
        }

        Set<Long> uniqueDocentes = new HashSet<>(request.getDocenteIds());
        if (uniqueDocentes.size() != request.getDocenteIds().size()) {
            throw new MesaExamenException("No se pueden asignar docentes duplicados");
        }

        // ---- conflictos de horario (fecha + hora) ----
        LocalDate fechaMesa = mesa.getFecha();
        LocalTime horaInicio = mesa.getHoraInicio();
        LocalTime horaFin = mesa.getHoraFin();

        List<MesaExamen> mesasConflicto = mesaRepo.findMesasConflictoParaDocentesEnHorario(
                fechaMesa, horaInicio, horaFin, request.getDocenteIds(), mesaExamenId
        );

        Map<Long, Boolean> conflictoRealPorDocente = new HashMap<>();
        for (MesaExamen meConf : mesasConflicto) {
            for (MesaExamenDocente med : meConf.getDocentes()) {
                Long dId = med.getDocente().getId();
                if (!request.getDocenteIds().contains(dId)) continue;
                boolean esConflictoReal = !esMismaMesaFinalDeMateria(mesa, meConf);
                if (esConflictoReal) {
                    conflictoRealPorDocente.put(dId, true);
                }
            }
        }

        List<Long> docentesConConflictoReal = conflictoRealPorDocente.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList();

        if (!docentesConConflictoReal.isEmpty()) {
            List<String> nombresConflicto = new ArrayList<>();
            for (Long docenteId : docentesConConflictoReal) {
                Docente docente = docentes.stream()
                        .filter(d -> d.getId().equals(docenteId))
                        .findFirst()
                        .orElse(null);
                if (docente != null) {
                    nombresConflicto.add(docente.getApellido() + ", " + docente.getNombre());
                }
            }
            throw new MesaExamenException("Los siguientes docentes ya están asignados a otra mesa en ese horario: "
                    + String.join(", ", nombresConflicto));
        }

        // ---- docentes que dan la materia ----
        Long materiaId = mesa.getMateriaCurso().getMateria().getId();
        Set<Long> docentesQueDALaMateria = materiaCursoRepo.findByMateriaId(materiaId)
                .stream()
                .filter(mc -> mc.getDocente() != null)
                .map(mc -> mc.getDocente().getId())
                .collect(Collectors.toSet());

        // reglas de cantidad / etc
        try {
            validacionService.validarAsignacionDocentes(mesa, request.getDocenteIds(), docentesQueDALaMateria);
        } catch (IllegalArgumentException e) {
            throw new MesaExamenException(e.getMessage());
        }

        // ---- ACTUALIZAR COLECCIÓN SIN DUPLICADOS ----
        // indexo actuales por docenteId
        Map<Long, MesaExamenDocente> actualesPorDocenteId = mesa.getDocentes().stream()
                .collect(Collectors.toMap(med -> med.getDocente().getId(), med -> med));

        // 1) elimino los que ya no están en el request
        mesa.getDocentes().removeIf(med -> !uniqueDocentes.contains(med.getDocente().getId()));

        // 2) para cada docente solicitado:
        for (Long docenteId : uniqueDocentes) {
            MesaExamenDocente existente = actualesPorDocenteId.get(docenteId);
            if (existente != null) {
                // ya estaba asignado -> sólo actualizo esDocenteMateria
                existente.setEsDocenteMateria(docentesQueDALaMateria.contains(docenteId));
            } else {
                // es nuevo -> creo la relación
                Docente docente = docentes.stream()
                        .filter(d -> d.getId().equals(docenteId))
                        .findFirst()
                        .orElseThrow();

                MesaExamenDocente asignacion = MesaExamenDocente.builder()
                        .mesaExamen(mesa)
                        .docente(docente)
                        .esDocenteMateria(docentesQueDALaMateria.contains(docenteId))
                        .build();

                mesa.getDocentes().add(asignacion);
            }
        }

        // persisto SOLO la mesa; JPA se encarga del delete/insert sin duplicar (mesa,docente)
        mesaRepo.save(mesa);

        // sincronizo con 1A/1B/etc
        sincronizarMesasFinalMateria(mesa);

        return listarDocentesAsignados(mesaExamenId);
    }

    @Override
    public MesaExamenDocentesResponse listarDocentesAsignados(Long mesaExamenId) {
        MesaExamen mesa = mesaRepo.findById(mesaExamenId)
                .orElseThrow(() -> new MesaExamenException("Mesa de examen no encontrada con ID: " + mesaExamenId));

        List<MesaExamenDocente> asignaciones = mesaExamenDocenteRepo.findByMesaExamen_Id(mesaExamenId);

        List<MesaExamenDocenteDto> docentesDto = asignaciones.stream()
                .map(this::mapDocenteToDto)
                .sorted(Comparator.comparing((MesaExamenDocenteDto d) -> !d.isEsDocenteMateria())
                        .thenComparing(MesaExamenDocenteDto::getApellidoDocente)
                        .thenComparing(MesaExamenDocenteDto::getNombreDocente))
                .collect(Collectors.toList());

        long docentesMateria = asignaciones.stream()
                .mapToLong(a -> a.isEsDocenteMateria() ? 1 : 0)
                .sum();

        return MesaExamenDocentesResponse.builder()
                .docentes(docentesDto)
                .mesaExamenId(mesaExamenId)
                .nombreMateria(mesa.getMateriaCurso().getMateria().getNombre())
                .totalDocentes(docentesDto.size())
                .docentesQueDALaMateria((int) docentesMateria)
                .code(0)
                .mensaje("OK")
                .build();
    }

    @Override
    public MesaExamenDocentesResponse modificarDocente(Long mesaExamenId, Long docenteActualId, Long nuevoDocenteId) {
        MesaExamen mesa = mesaRepo.findById(mesaExamenId)
                .orElseThrow(() -> new MesaExamenException("Mesa de examen no encontrada con ID: " + mesaExamenId));

        if (!mesa.tieneHorarioDefinido()) {
            throw new MesaExamenException(
                    "Debe definir fecha y horario (horaInicio / horaFin) de la mesa antes de modificar docentes."
            );
        }

        List<MesaExamenDocente> asignaciones = mesaExamenDocenteRepo.findByMesaExamen_Id(mesaExamenId);
        MesaExamenDocente asignacionActual = asignaciones.stream()
                .filter(a -> a.getDocente().getId().equals(docenteActualId))
                .findFirst()
                .orElseThrow(() -> new MesaExamenException("El docente no está asignado a esta mesa de examen"));

        Docente nuevoDocente = docenteRepo.findById(nuevoDocenteId)
                .orElseThrow(() -> new MesaExamenException("Docente no encontrado con ID: " + nuevoDocenteId));

        boolean yaAsignado = asignaciones.stream()
                .anyMatch(a -> a.getDocente().getId().equals(nuevoDocenteId));
        if (yaAsignado) {
            throw new MesaExamenException("El docente ya está asignado a esta mesa de examen");
        }

        LocalDate fechaMesa = mesa.getFecha();
        LocalTime horaInicio = mesa.getHoraInicio();
        LocalTime horaFin = mesa.getHoraFin();

        List<MesaExamen> mesasConflicto = mesaRepo.findMesasConflictoParaDocenteEnHorario(
                nuevoDocenteId, fechaMesa, horaInicio, horaFin, mesaExamenId
        );

        boolean tieneConflictoReal = mesasConflicto.stream()
                .anyMatch(otra -> !esMismaMesaFinalDeMateria(mesa, otra));

        if (tieneConflictoReal) {
            throw new MesaExamenException("El docente " + nuevoDocente.getApellido() + ", " +
                    nuevoDocente.getNombre() + " ya está asignado a otra mesa en el mismo horario (" +
                    fechaMesa + " " + horaInicio + "–" + horaFin + ")");
        }

        Long materiaId = mesa.getMateriaCurso().getMateria().getId();
        Set<Long> docentesQueDALaMateria = materiaCursoRepo.findByMateriaId(materiaId)
                .stream()
                .filter(mc -> mc.getDocente() != null)
                .map(mc -> mc.getDocente().getId())
                .collect(Collectors.toSet());

        long docentesMateriaCount = asignaciones.stream()
                .mapToLong(a -> a.isEsDocenteMateria() ? 1 : 0)
                .sum();

        if (asignacionActual.isEsDocenteMateria() && docentesMateriaCount == 1) {
            if (!docentesQueDALaMateria.contains(nuevoDocenteId)) {
                throw new MesaExamenException("No se puede reemplazar el único docente que da la materia por uno que no la da");
            }
        }

        mesa.getDocentes().remove(asignacionActual);

        MesaExamenDocente nuevaAsignacionTmp = MesaExamenDocente.builder()
                .mesaExamen(mesa)
                .docente(nuevoDocente)
                .esDocenteMateria(docentesQueDALaMateria.contains(nuevoDocenteId))
                .build();

        try {
            validacionService.validarAgregarDocente(mesa, nuevaAsignacionTmp);
        } catch (IllegalArgumentException e) {
            mesa.getDocentes().add(asignacionActual);
            throw new MesaExamenException("No se puede asignar el docente " + nuevoDocente.getApellido() + ", " +
                    nuevoDocente.getNombre() + ": " + e.getMessage());
        }

        asignacionActual.setDocente(nuevoDocente);
        asignacionActual.setEsDocenteMateria(docentesQueDALaMateria.contains(nuevoDocenteId));
        mesaExamenDocenteRepo.save(asignacionActual);

        // sincronizar jurado con 1A, 1B, etc.
        sincronizarMesasFinalMateria(mesa);

        return listarDocentesAsignados(mesaExamenId);
    }

    private MesaExamenDocenteDto mapDocenteToDto(MesaExamenDocente asignacion) {
        String nombreMateria = null;
        if (asignacion.isEsDocenteMateria()
                && asignacion.getMesaExamen() != null
                && asignacion.getMesaExamen().getMateriaCurso() != null
                && asignacion.getMesaExamen().getMateriaCurso().getMateria() != null) {
            nombreMateria = asignacion.getMesaExamen()
                    .getMateriaCurso()
                    .getMateria()
                    .getNombre();
        }

        return MesaExamenDocenteDto.builder()
                .id(asignacion.getId())
                .docenteId(asignacion.getDocente().getId())
                .apellidoDocente(asignacion.getDocente().getApellido())
                .nombreDocente(asignacion.getDocente().getNombre())
                .nombreCompleto(asignacion.getDocente().getApellido() + ", " + asignacion.getDocente().getNombre())
                .nombreMateria(nombreMateria)          // solo si realmente da la materia
                .esDocenteMateria(asignacion.isEsDocenteMateria())
                .build();
    }

    // ================================================================
    // ALUMNOS QUE DEBEN MATERIA
    // ================================================================
    @Override
    public AlumnosDebenMateriaResponse listarAlumnosQueDebenMateria(Long cursoId, Long materiaId, Integer anioOpt) {
        int anio = (anioOpt != null) ? anioOpt : LocalDate.now().getYear();

        MateriaCurso mc = materiaCursoRepo.findByCurso_IdAndMateria_Id(cursoId, materiaId)
                .orElseThrow(() -> new MesaExamenException("No se encontró la materia para ese curso"));

        List<HistorialMateria> hms = historialMateriaRepository
                .findByMateriaCurso_IdAndEstado(mc.getId(), EstadoMateriaAlumno.DESAPROBADA);

        Map<Long, AlumnoDebeMateriaDto> mapaAlumnos = new LinkedHashMap<>();

        for (HistorialMateria hm : hms) {
            HistorialCurso hc = hm.getHistorialCurso();
            int anioCurso = hc.getCicloLectivo().getFechaDesde().getYear();

            if (anioCurso != anio) {
                continue;
            }

            Alumno a = hc.getAlumno();
            Curso c = hc.getCurso();

            if (a.getEstado() == EstadoAlumno.BORRADO
                    || a.getEstado() == EstadoAlumno.EXCLUIDO_POR_REPETICION) {
                continue;
            }

            if (mapaAlumnos.containsKey(a.getId())) {
                continue;
            }

            String cursoStr = (c != null)
                    ? c.getAnio() + "° " + (c.getDivision() != null ? c.getDivision().name() : "")
                    : "Sin curso";

            AlumnoDebeMateriaDto dto = AlumnoDebeMateriaDto.builder()
                    .alumnoId(a.getId())
                    .dni(a.getDni())
                    .apellido(a.getApellido())
                    .nombre(a.getNombre())
                    .curso(cursoStr)
                    .anioCiclo(anioCurso)
                    .estado(hm.getEstado())
                    .build();

            mapaAlumnos.put(a.getId(), dto);
        }

        List<AlumnoDebeMateriaDto> alumnos = new ArrayList<>(mapaAlumnos.values());

        return AlumnosDebenMateriaResponse.builder()
                .code(0)
                .mensaje("OK")
                .total(alumnos.size())
                .alumnos(alumnos)
                .build();
    }

    // ================================================================
    // CREACIÓN MASIVA DE MESAS
    // ================================================================
    @Override
    public MesaExamenListResponse crearMesasExamenMasivas(MesasExamenMasivasRequest req) {
        if (req.getTurnoId() == null) {
            throw new MesaExamenException("turnoId es obligatorio");
        }
        if (req.getCursoIds() == null || req.getCursoIds().isEmpty()) {
            throw new MesaExamenException("Debe enviar al menos un cursoId");
        }
        if (req.getTipoMesa() == null) {
            throw new MesaExamenException("tipoMesa es obligatorio (EXAMEN o COLOQUIO)");
        }

        TurnoExamen turno = turnoRepo.findById(req.getTurnoId())
                .orElseThrow(() -> new MesaExamenException("Turno no encontrado"));

        LocalDate fechaMesa = req.getFechaMesa(); // puede ser null

        if (fechaMesa != null) {
            if (fechaMesa.isBefore(turno.getFechaInicio()) || fechaMesa.isAfter(turno.getFechaFin())) {
                throw new MesaExamenException("La fecha de la mesa debe estar dentro del turno (" +
                        turno.getFechaInicio() + " a " + turno.getFechaFin() + ")");
            }
        }

        LocalDate base = (fechaMesa != null) ? fechaMesa : turno.getFechaInicio();
        int anioCursada = resolverAnioCursada(base);

        Set<Long> materiasFiltro = (req.getMateriaIds() != null && !req.getMateriaIds().isEmpty())
                ? new HashSet<>(req.getMateriaIds())
                : null;

        List<MesaExamen> mesasCreadas = new ArrayList<>();

        for (Long cursoId : req.getCursoIds()) {
            Curso curso = cursoRepository.findById(cursoId)
                    .orElseThrow(() -> new MesaExamenException("Curso no encontrado: " + cursoId));

            var reporteRinden = reporteRindenService.listarRindenPorCurso(cursoId, anioCursada);

            List<MateriaCurso> materiasCurso = materiaCursoRepo.findByCursoId(cursoId);

            if (materiasFiltro != null) {
                materiasCurso = materiasCurso.stream()
                        .filter(mc -> materiasFiltro.contains(mc.getMateria().getId()))
                        .toList();
            }

            for (MateriaCurso mc : materiasCurso) {
                Long materiaId = mc.getMateria().getId();

                var filasMateria = reporteRinden.getFilas().stream()
                        .filter(f -> f.getMateriaId().equals(materiaId))
                        .filter(f -> condicionAplicaATipoMesa(req.getTipoMesa(), f.getCondicion()))
                        .toList();

                if (filasMateria.isEmpty()) {
                    continue;
                }

                MesaExamen mesa = new MesaExamen();
                mesa.setMateriaCurso(mc);
                mesa.setTurno(turno);
                mesa.setTipoMesa(req.getTipoMesa());
                mesa.setEstado(EstadoMesaExamen.CREADA);

                if (fechaMesa != null) {
                    mesa.setFecha(fechaMesa);
                }
                // en masiva podrías agregar horaInicio/horaFin en el request

                Docente titular = mc.getDocente();
                if (titular != null) {
                    MesaExamenDocente med = MesaExamenDocente.builder()
                            .mesaExamen(mesa)
                            .docente(titular)
                            .esDocenteMateria(true)
                            .build();
                    mesa.getDocentes().add(med);
                }

                for (var fila : filasMateria) {
                    Alumno alumno = alumnoRepo.findById(fila.getAlumnoId())
                            .orElseThrow(() -> new MesaExamenException("Alumno no encontrado: " + fila.getAlumnoId()));

                    MesaExamenAlumno link = MesaExamenAlumno.builder()
                            .alumno(alumno)
                            .mesaExamen(mesa)
                            .estado(EstadoConvocado.CONVOCADO)
                            .condicionRinde(fila.getCondicion())
                            .notaFinal(null)
                            .turno(turno)
                            .build();

                    try {
                        validacionService.validarInscribirAlumno(mesa, link);
                    } catch (IllegalArgumentException e) {
                        throw new MesaExamenException("No se puede agregar el alumno " +
                                alumno.getApellido() + ", " + alumno.getNombre() + ": " + e.getMessage());
                    }

                    mesa.getAlumnos().add(link);
                }

                validacionService.validarConfiguracionMesa(mesa, false);

                mesaRepo.save(mesa);
                mesasCreadas.add(mesa);
            }
        }

        // sincronizar finales por materia+turno (1A y 1B juntas)
        for (MesaExamen mesa : mesasCreadas) {
            sincronizarMesasFinalMateria(mesa);
        }

        var dtos = mesasCreadas.stream()
                .map(m -> MesaExamenMapper.toDto(m, true))
                .toList();

        return MesaExamenListResponse.builder()
                .code(0)
                .mensaje("Se crearon " + dtos.size() + " mesas de " + req.getTipoMesa().name())
                .total(dtos.size())
                .mesas(dtos)
                .build();
    }

    // ================================================================
    // HELPERS
    // ================================================================
    private static void assertEditable(MesaExamen m) {
        if (m.getEstado() == EstadoMesaExamen.FINALIZADA) {
            throw new MesaExamenException("La mesa está finalizada y no puede modificarse.");
        }
    }

    private CondicionRinde obtenerCondicionDesdeHistorial(MesaExamen mesa,
                                                          Alumno alumno,
                                                          Long materiaId,
                                                          int anioMesa) {
        if (mesa.getTipoMesa() == TipoMesa.COLOQUIO) {
            throw new MesaExamenException(
                    "No se encontró información de rendimiento para el alumno " +
                            alumno.getApellido() + ", " + alumno.getNombre() +
                            " en el curso actual. Para coloquio solo se permiten alumnos del cursado vigente.");
        }

        List<HistorialCurso> historialCompleto =
                historialCursoRepository.findHistorialCompletoByAlumnoId(alumno.getId());

        boolean tieneMateriaPendiente = false;

        for (HistorialCurso hc : historialCompleto) {
            int anioCurso = hc.getCicloLectivo().getFechaDesde().getYear();
            if (anioCurso >= anioMesa) {
                continue;
            }

            List<HistorialMateria> hms = historialMateriaRepository.findAllByHistorialCursoId(hc.getId());

            for (HistorialMateria hm : hms) {
                if (hm.getMateriaCurso() != null
                        && hm.getMateriaCurso().getMateria().getId().equals(materiaId)
                        && hm.getEstado() == EstadoMateriaAlumno.DESAPROBADA) {
                    tieneMateriaPendiente = true;
                    break;
                }
            }

            if (tieneMateriaPendiente) {
                break;
            }
        }

        if (!tieneMateriaPendiente) {
            throw new MesaExamenException(
                    "El alumno " + alumno.getApellido() + ", " + alumno.getNombre() +
                            " no tiene esta materia pendiente según su historial académico.");
        }

        return CondicionRinde.EXAMEN;
    }

    private void actualizarMateriaPendienteComoAprobada(MesaExamen mesa, MesaExamenAlumno ma) {
        Alumno alumno = ma.getAlumno();
        Long alumnoId = alumno.getId();
        Long materiaId = mesa.getMateriaCurso().getMateria().getId();

        if (mesa.getFecha() == null) {
            return;
        }
        int anioMesa = mesa.getFecha().getYear();

        List<HistorialCurso> historialCompleto =
                historialCursoRepository.findHistorialCompletoByAlumnoId(alumnoId);

        HistorialMateria previaMasReciente = null;
        int anioPrevioMasReciente = Integer.MIN_VALUE;

        for (HistorialCurso hc : historialCompleto) {
            int anioCurso = hc.getCicloLectivo().getFechaDesde().getYear();

            if (anioCurso >= anioMesa) {
                continue;
            }

            List<HistorialMateria> hms = historialMateriaRepository.findAllByHistorialCursoId(hc.getId());
            for (HistorialMateria hm : hms) {
                if (hm.getMateriaCurso() != null
                        && hm.getMateriaCurso().getMateria().getId().equals(materiaId)
                        && hm.getEstado() == EstadoMateriaAlumno.DESAPROBADA) {

                    if (anioCurso > anioPrevioMasReciente) {
                        previaMasReciente = hm;
                        anioPrevioMasReciente = anioCurso;
                    }
                }
            }
        }

        if (previaMasReciente != null) {
            previaMasReciente.setEstado(EstadoMateriaAlumno.APROBADA);
            historialMateriaRepository.save(previaMasReciente);
            log.info("Alumno {}: materia previa {} del año {} marcada como APROBADA por mesa {}",
                    alumnoId, materiaId, anioPrevioMasReciente, mesa.getId());
        }
    }

    private boolean condicionAplicaATipoMesa(TipoMesa tipoMesa, CondicionRinde condicion) {
        if (condicion == null) return false;

        return switch (tipoMesa) {
            case EXAMEN ->
                    condicion == CondicionRinde.EXAMEN
                            || condicion == CondicionRinde.COLOQUIO;

            case COLOQUIO ->
                    condicion == CondicionRinde.COLOQUIO;
        };
    }

    /**
     * Enero, febrero y marzo se consideran parte del ciclo lectivo anterior.
     */
    private int resolverAnioCursada(LocalDate fechaBase) {
        if (fechaBase == null) {
            return LocalDate.now().getYear();
        }
        int year = fechaBase.getYear();
        int month = fechaBase.getMonthValue();
        return (month <= 3) ? year - 1 : year;
    }

    /**
     * Devuelve true si ambas mesas representan el "mismo final de materia":
     * - mismo tipo EXAMEN
     * - mismo turno
     * - misma materia
     * (el curso puede ser distinto: 1A, 1B, etc.)
     */
    private boolean esMismaMesaFinalDeMateria(MesaExamen base, MesaExamen otra) {
        if (base == null || otra == null) return false;
        if (base.getTipoMesa() != TipoMesa.EXAMEN || otra.getTipoMesa() != TipoMesa.EXAMEN) return false;
        if (base.getTurno() == null || otra.getTurno() == null) return false;
        if (base.getMateriaCurso() == null || otra.getMateriaCurso() == null) return false;
        if (base.getMateriaCurso().getMateria() == null || otra.getMateriaCurso().getMateria() == null) return false;

        boolean mismoTurno = Objects.equals(base.getTurno().getId(), otra.getTurno().getId());
        boolean mismaMateria = Objects.equals(
                base.getMateriaCurso().getMateria().getId(),
                otra.getMateriaCurso().getMateria().getId()
        );

        return mismoTurno && mismaMateria;
    }

    /**
     * Sincroniza fecha, horario, aula y DOCENTES de todas las mesas finales
     * de la misma materia+turno (1A, 1B, etc.) con la mesa base,
     * sin generar duplicados de (mesa,docente).
     */
    private void sincronizarMesasFinalMateria(MesaExamen base) {
        if (base == null) return;
        if (base.getTipoMesa() != TipoMesa.EXAMEN) return;
        if (base.getTurno() == null) return;
        if (base.getMateriaCurso() == null || base.getMateriaCurso().getMateria() == null) return;

        Long turnoId = base.getTurno().getId();
        Long materiaId = base.getMateriaCurso().getMateria().getId();

        List<MesaExamen> relacionadas =
                mesaRepo.findByTurno_IdAndTipoMesaAndMateriaCurso_Materia_Id(
                        turnoId, TipoMesa.EXAMEN, materiaId
                );

        // docentes de la mesa base
        Map<Long, Boolean> esDocenteMateriaMap = base.getDocentes().stream()
                .collect(Collectors.toMap(
                        med -> med.getDocente().getId(),
                        MesaExamenDocente::isEsDocenteMateria,
                        (a, b) -> a
                ));
        Set<Long> baseDocenteIds = esDocenteMateriaMap.keySet();

        for (MesaExamen otra : relacionadas) {
            if (Objects.equals(otra.getId(), base.getId())) continue;

            // sincronizar fecha, horario y aula
            otra.setFecha(base.getFecha());
            otra.setHoraInicio(base.getHoraInicio());
            otra.setHoraFin(base.getHoraFin());
            otra.setAula(base.getAula());

            // indexo docentes actuales de "otra" por docenteId
            Map<Long, MesaExamenDocente> actualesOtra = otra.getDocentes().stream()
                    .collect(Collectors.toMap(
                            med -> med.getDocente().getId(),
                            med -> med
                    ));

            // elimino los que no están en la base
            otra.getDocentes().removeIf(med -> !baseDocenteIds.contains(med.getDocente().getId()));

            // agrego/actualizo los que sí están en la base
            for (Long docenteId : baseDocenteIds) {
                MesaExamenDocente medOtra = actualesOtra.get(docenteId);
                if (medOtra != null) {
                    medOtra.setEsDocenteMateria(esDocenteMateriaMap.get(docenteId));
                } else {
                    // buscamos el Docente desde la mesa base (misma entidad gestor)
                    MesaExamenDocente medBase = base.getDocentes().stream()
                            .filter(m -> m.getDocente().getId().equals(docenteId))
                            .findFirst()
                            .orElseThrow();

                    MesaExamenDocente nuevo = MesaExamenDocente.builder()
                            .mesaExamen(otra)
                            .docente(medBase.getDocente())
                            .esDocenteMateria(esDocenteMateriaMap.get(docenteId))
                            .build();
                    otra.getDocentes().add(nuevo);
                }
            }

            mesaRepo.save(otra);
        }
    }
}
