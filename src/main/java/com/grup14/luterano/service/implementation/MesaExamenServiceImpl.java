// service/implementation/MesaExamenServiceImpl.java
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
    @Override
    public MesaExamenResponse crear(MesaExamenCreateRequest req) {
        if (req.getMateriaCursoId() == null || req.getFecha() == null || req.getTurnoId() == null)
            return MesaExamenResponse.builder().code(-1).mensaje("materiaCursoId, turnoId y fecha son obligatorios").build();

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
        m.setTipoMesa(req.getTipoMesa()); // Establecer tipo de mesa
        
        if (req.getAulaId() != null) {
            Aula aula = aulaRepo.findById(req.getAulaId())
                    .orElseThrow(() -> new MesaExamenException("Aula no encontrada"));
            m.setAula(aula);
        }
        m.setEstado(EstadoMesaExamen.CREADA);
        
        // Validar configuración antes de guardar
        validacionService.validarConfiguracionMesa(m);
        
        mesaRepo.save(m);

        log.info("Mesa creada id={} mc={} turno={} fecha={}", m.getId(), mc.getId(), turno.getId(), m.getFecha());
        return MesaExamenResponse.builder().code(0).mensaje("Mesa creada").mesa(MesaExamenMapper.toDto(m, true)).build();
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
        MesaExamen m = mesaRepo.findByIdWithAlumnos(id)
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

        if (req.getAlumnoIds() == null || req.getAlumnoIds().isEmpty()) {
            throw new MesaExamenException("Debe enviar alumnoIds");
        }

        int anioMesa = (m.getFecha() != null ? m.getFecha().getYear() : LocalDate.now().getYear());
        Long materiaId = m.getMateriaCurso().getMateria().getId();

        // Seguimos usando el reporte (comportamiento original)
        var reporteRinden = reporteRindenService
                .listarRindenPorCurso(m.getMateriaCurso().getCurso().getId(), anioMesa);

        for (Long aId : req.getAlumnoIds()) {
            if (mesaAluRepo.existsByMesaExamen_IdAndAlumno_Id(mesaId, aId)) {
                continue;
            }

            Alumno a = alumnoRepo.findById(aId)
                    .orElseThrow(() -> new MesaExamenException("Alumno no encontrado: " + aId));

            // 1) Intentar obtener la condición desde el reporte (como antes)
            var filaAlumnoOpt = reporteRinden.getFilas().stream()
                    .filter(f -> f.getAlumnoId().equals(aId)
                            && f.getMateriaId().equals(materiaId))
                    .findFirst();

            CondicionRinde condicionRinde;

            if (filaAlumnoOpt.isPresent()) {
                condicionRinde = filaAlumnoOpt.get().getCondicion();
            } else {
                // 2) Fallback: usar historial académico para ver si debe la materia
                condicionRinde = obtenerCondicionDesdeHistorial(m, a, materiaId, anioMesa);
            }

            MesaExamenAlumno link = MesaExamenAlumno.builder()
                    .alumno(a)
                    .mesaExamen(m)
                    .estado(EstadoConvocado.CONVOCADO)
                    .condicionRinde(condicionRinde)
                    .notaFinal(null)
                    .turno(m.getTurno())
                    .build();

            // Validar que el alumno puede inscribirse según el tipo de mesa
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

        // Buscar y remover el alumno de la colección
        boolean removed = m.getAlumnos().removeIf(mesaAlumno ->
                mesaAlumno.getAlumno().getId().equals(alumnoId));

        if (!removed) {
            throw new MesaExamenException("El alumno no está convocado a esta mesa");
        }

        log.info("Después de quitar: Mesa {} tiene {} alumnos convocados", mesaId, m.getAlumnos().size());

        // Guardar la mesa actualizada
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
        if (notasPorAlumnoId == null || notasPorAlumnoId.isEmpty())
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

                    // 👇 Nuevo comportamiento:
                    // Si aprobó la mesa, ver si tenía la materia pendiente (DESAPROBADA) en años anteriores
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
            return MesaExamenResponse.builder().code(0).mensaje("La mesa ya estaba finalizada").mesa(MesaExamenMapper.toDto(m, true)).build();
        }
        m.setEstado(EstadoMesaExamen.FINALIZADA);
        mesaRepo.save(m);
        return MesaExamenResponse.builder().code(0).mensaje("Mesa finalizada").mesa(MesaExamenMapper.toDto(m, true)).build();
    }

    // ------ GESTIÓN DE DOCENTES -------

    @Override
    public DocentesDisponiblesResponse listarDocentesDisponibles(Long mesaExamenId) {
        MesaExamen mesa = mesaRepo.findById(mesaExamenId)
                .orElseThrow(() -> new MesaExamenException("Mesa de examen no encontrada con ID: " + mesaExamenId));

        Long materiaId = mesa.getMateriaCurso().getMateria().getId();
        String nombreMateria = mesa.getMateriaCurso().getMateria().getNombre();
        LocalDate fechaMesa = mesa.getFecha();

        // Obtener todos los docentes
        List<Docente> todosDocentes = docenteRepo.findAll();

        // Obtener docentes que dan la materia
        Set<Long> docentesQueDALaMateria = materiaCursoRepo.findByMateriaId(materiaId)
                .stream()
                .filter(mc -> mc.getDocente() != null) // Solo los que tienen docente asignado
                .map(mc -> mc.getDocente().getId())
                .collect(Collectors.toSet());

        // Obtener docentes con conflictos horarios
        List<Long> docenteIds = todosDocentes.stream().map(Docente::getId).collect(Collectors.toList());
        List<Long> docentesConConflicto = mesaExamenDocenteRepo.findDocentesConflictoEnFecha(fechaMesa, docenteIds);
        Set<Long> docentesConflictSet = new HashSet<>(docentesConConflicto);

        // Mapear a DTOs y ordenar: primero los que dan la materia, después el resto
        List<DocenteDisponibleDto> docentesDto = todosDocentes.stream()
                .map(docente -> {
                    boolean tieneConflicto = docentesConflictSet.contains(docente.getId());
                    String detalleConflicto = null;

                    if (tieneConflicto) {
                        List<MesaExamen> mesasConflicto = mesaRepo.findMesasConflictoParaDocente(
                                docente.getId(), fechaMesa, mesaExamenId);
                        if (!mesasConflicto.isEmpty()) {
                            MesaExamen primeraConflicto = mesasConflicto.get(0);
                            detalleConflicto = String.format("Ya asignado a %s - %s %s",
                                    primeraConflicto.getMateriaCurso().getMateria().getNombre(),
                                    primeraConflicto.getMateriaCurso().getCurso().getAnio(),
                                    primeraConflicto.getMateriaCurso().getCurso().getDivision());
                        }
                    }

                    return DocenteDisponibleDto.builder()
                            .docenteId(docente.getId())
                            .apellido(docente.getApellido())
                            .nombre(docente.getNombre())
                            .nombreCompleto(docente.getApellido() + ", " + docente.getNombre())
                            .daLaMateria(docentesQueDALaMateria.contains(docente.getId()))
                            .nombreMateria(docentesQueDALaMateria.contains(docente.getId()) ? nombreMateria : null)
                            .tieneConflictoHorario(tieneConflicto)
                            .detalleConflicto(detalleConflicto)
                            .build();
                })
                .sorted(Comparator.comparing((DocenteDisponibleDto d) -> d.isTieneConflictoHorario()) // sin conflicto primero
                        .thenComparing(d -> !d.isDaLaMateria()) // luego los que dan la materia
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

        // Validar que todos los docentes existan
        List<Docente> docentes = docenteRepo.findAllById(request.getDocenteIds());
        if (docentes.size() != request.getDocenteIds().size()) {
            throw new MesaExamenException("Uno o más docentes no existen");
        }

        // Validar que no haya docentes duplicados
        Set<Long> uniqueDocentes = new HashSet<>(request.getDocenteIds());
        if (uniqueDocentes.size() != request.getDocenteIds().size()) {
            throw new MesaExamenException("No se pueden asignar docentes duplicados");
        }

        // Verificar conflictos horarios para todos los docentes
        LocalDate fechaMesa = mesa.getFecha();
        List<Long> docentesConConflicto = mesaExamenDocenteRepo.findDocentesConflictoEnFecha(fechaMesa, request.getDocenteIds());
        if (!docentesConConflicto.isEmpty()) {
            List<String> nombresConflicto = new ArrayList<>();
            for (Long docenteId : docentesConConflicto) {
                Docente docente = docentes.stream()
                        .filter(d -> d.getId().equals(docenteId))
                        .findFirst()
                        .orElse(null);
                if (docente != null) {
                    nombresConflicto.add(docente.getApellido() + ", " + docente.getNombre());
                }
            }
            throw new MesaExamenException("Los siguientes docentes ya están asignados a otra mesa en la fecha " +
                    fechaMesa + ": " + String.join(", ", nombresConflicto));
        }

        // Verificar que al menos uno dé la materia
        Long materiaId = mesa.getMateriaCurso().getMateria().getId();
        Set<Long> docentesQueDALaMateria = materiaCursoRepo.findByMateriaId(materiaId)
                .stream()
                .filter(mc -> mc.getDocente() != null) // Solo los que tienen docente asignado
                .map(mc -> mc.getDocente().getId())
                .collect(Collectors.toSet());

        // Validar asignación según tipo de mesa (cantidad y requisitos específicos)
        try {
            validacionService.validarAsignacionDocentes(mesa, request.getDocenteIds(), docentesQueDALaMateria);
        } catch (IllegalArgumentException e) {
            throw new MesaExamenException(e.getMessage());
        }

        // Eliminar asignaciones previas
        mesaExamenDocenteRepo.deleteByMesaExamen_Id(mesaExamenId);

        // Crear nuevas asignaciones
        List<MesaExamenDocente> nuevasAsignaciones = new ArrayList<>();
        for (Long docenteId : request.getDocenteIds()) {
            Docente docente = docentes.stream()
                    .filter(d -> d.getId().equals(docenteId))
                    .findFirst()
                    .orElseThrow();

            MesaExamenDocente asignacion = MesaExamenDocente.builder()
                    .mesaExamen(mesa)
                    .docente(docente)
                    .esDocenteMateria(docentesQueDALaMateria.contains(docenteId))
                    .build();

            nuevasAsignaciones.add(asignacion);
        }

        mesaExamenDocenteRepo.saveAll(nuevasAsignaciones);

        return listarDocentesAsignados(mesaExamenId);
    }

    @Override
    public MesaExamenDocentesResponse listarDocentesAsignados(Long mesaExamenId) {
        MesaExamen mesa = mesaRepo.findById(mesaExamenId)
                .orElseThrow(() -> new MesaExamenException("Mesa de examen no encontrada con ID: " + mesaExamenId));

        List<MesaExamenDocente> asignaciones = mesaExamenDocenteRepo.findByMesaExamen_Id(mesaExamenId);

        List<MesaExamenDocenteDto> docentesDto = asignaciones.stream()
                .map(this::mapDocenteToDto)
                .sorted(Comparator.comparing((MesaExamenDocenteDto d) -> !d.isEsDocenteMateria()) // docentes de la materia primero
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

        // Buscar la asignación actual
        List<MesaExamenDocente> asignaciones = mesaExamenDocenteRepo.findByMesaExamen_Id(mesaExamenId);
        MesaExamenDocente asignacionActual = asignaciones.stream()
                .filter(a -> a.getDocente().getId().equals(docenteActualId))
                .findFirst()
                .orElseThrow(() -> new MesaExamenException("El docente no está asignado a esta mesa de examen"));

        // Validar que el nuevo docente existe
        Docente nuevoDocente = docenteRepo.findById(nuevoDocenteId)
                .orElseThrow(() -> new MesaExamenException("Docente no encontrado con ID: " + nuevoDocenteId));

        // Validar que el nuevo docente no esté ya asignado
        boolean yaAsignado = asignaciones.stream()
                .anyMatch(a -> a.getDocente().getId().equals(nuevoDocenteId));
        if (yaAsignado) {
            throw new MesaExamenException("El docente ya está asignado a esta mesa de examen");
        }

        // Verificar conflictos horarios del nuevo docente
        LocalDate fechaMesa = mesa.getFecha();
        boolean tieneConflicto = mesaExamenDocenteRepo.existeDocenteEnOtraMesaEnFecha(
                nuevoDocenteId, fechaMesa, mesaExamenId);
        if (tieneConflicto) {
            throw new MesaExamenException("El docente " + nuevoDocente.getApellido() + ", " +
                    nuevoDocente.getNombre() + " ya está asignado a otra mesa en la fecha " + fechaMesa);
        }

        // Verificar que sigue habiendo al menos un docente de la materia
        Long materiaId = mesa.getMateriaCurso().getMateria().getId();
        Set<Long> docentesQueDALaMateria = materiaCursoRepo.findByMateriaId(materiaId)
                .stream()
                .filter(mc -> mc.getDocente() != null) // Solo los que tienen docente asignado
                .map(mc -> mc.getDocente().getId())
                .collect(Collectors.toSet());

        // Si estamos removiendo el único docente de la materia, validar que el nuevo también dé la materia
        long docentesMateriaCount = asignaciones.stream()
                .mapToLong(a -> a.isEsDocenteMateria() ? 1 : 0)
                .sum();

        if (asignacionActual.isEsDocenteMateria() && docentesMateriaCount == 1) {
            if (!docentesQueDALaMateria.contains(nuevoDocenteId)) {
                throw new MesaExamenException("No se puede reemplazar el único docente que da la materia por uno que no la da");
            }
        }

        // Crear una asignación temporal para validar con el servicio de validación
        // Primero removemos la asignación actual de la mesa para la validación
        mesa.getDocentes().remove(asignacionActual);
        
        MesaExamenDocente nuevaAsignacion = MesaExamenDocente.builder()
                .mesaExamen(mesa)
                .docente(nuevoDocente)
                .esDocenteMateria(docentesQueDALaMateria.contains(nuevoDocenteId))
                .build();

        // Validar que se puede agregar el nuevo docente según las reglas de negocio
        try {
            validacionService.validarAgregarDocente(mesa, nuevaAsignacion);
        } catch (IllegalArgumentException e) {
            // Restaurar la asignación actual antes de lanzar la excepción
            mesa.getDocentes().add(asignacionActual);
            throw new MesaExamenException("No se puede asignar el docente " + nuevoDocente.getApellido() + ", " + nuevoDocente.getNombre() + ": " + e.getMessage());
        }

        // Si llegamos aquí, la validación pasó. Actualizar la asignación
        asignacionActual.setDocente(nuevoDocente);
        asignacionActual.setEsDocenteMateria(docentesQueDALaMateria.contains(nuevoDocenteId));
        mesaExamenDocenteRepo.save(asignacionActual);

        return listarDocentesAsignados(mesaExamenId);
    }

    private MesaExamenDocenteDto mapDocenteToDto(MesaExamenDocente asignacion) {
        return MesaExamenDocenteDto.builder()
                .id(asignacion.getId())
                .docenteId(asignacion.getDocente().getId())
                .apellidoDocente(asignacion.getDocente().getApellido())
                .nombreDocente(asignacion.getDocente().getNombre())
                .nombreCompleto(asignacion.getDocente().getApellido() + ", " + asignacion.getDocente().getNombre())
                .nombreMateria(asignacion.getMesaExamen().getMateriaCurso().getMateria().getNombre())
                .esDocenteMateria(asignacion.isEsDocenteMateria())
                .build();
    }

    @Override
    public AlumnosDebenMateriaResponse listarAlumnosQueDebenMateria(Long cursoId, Long materiaId, Integer anioOpt) {
        int anio = (anioOpt != null) ? anioOpt : LocalDate.now().getYear();

        // 1) Buscar la MateriaCurso específica (curso + materia)
        MateriaCurso mc = materiaCursoRepo.findByCurso_IdAndMateria_Id(cursoId, materiaId)
                .orElseThrow(() -> new MesaExamenException("No se encontró la materia para ese curso"));

        // 2) Buscar todos los HistorialMateria con esa MateriaCurso y estado DESAPROBADA
        List<HistorialMateria> hms = historialMateriaRepository
                .findByMateriaCurso_IdAndEstado(mc.getId(), EstadoMateriaAlumno.DESAPROBADA);

        // 3) Filtrar por año del CicloLectivo (fechaDesde.getYear() == anio)
        Map<Long, AlumnoDebeMateriaDto> mapaAlumnos = new LinkedHashMap<>();

        for (HistorialMateria hm : hms) {
            HistorialCurso hc = hm.getHistorialCurso();
            int anioCurso = hc.getCicloLectivo().getFechaDesde().getYear();

            if (anioCurso != anio) {
                continue;
            }

            Alumno a = hc.getAlumno();
            Curso c = hc.getCurso();

            // 👉 Filtrar alumnos borrados o excluidos
            if (a.getEstado() == EstadoAlumno.BORRADO
                    || a.getEstado() == EstadoAlumno.EXCLUIDO_POR_REPETICION) {
                continue;
            }

            // Evitar duplicados por alumno
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
                    .estado(hm.getEstado()) // DESAPROBADA
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

        LocalDate fechaMesa = req.getFechaMesa();

        // Validar fecha dentro del turno
        if (fechaMesa.isBefore(turno.getFechaInicio()) || fechaMesa.isAfter(turno.getFechaFin())) {
            throw new MesaExamenException("La fecha de la mesa debe estar dentro del turno (" +
                    turno.getFechaInicio() + " a " + turno.getFechaFin() + ")");
        }

        int anioMesa = fechaMesa.getYear();
        Set<Long> materiasFiltro = (req.getMateriaIds() != null && !req.getMateriaIds().isEmpty())
                ? new HashSet<>(req.getMateriaIds())
                : null;

        List<MesaExamen> mesasCreadas = new ArrayList<>();

        for (Long cursoId : req.getCursoIds()) {
            Curso curso = cursoRepository.findById(cursoId)
                    .orElseThrow(() -> new MesaExamenException("Curso no encontrado: " + cursoId));

            // Traer reporte de rinden para este curso y año de la mesa
            var reporteRinden = reporteRindenService.listarRindenPorCurso(cursoId, anioMesa);

            // Todas las MateriaCurso del curso
            List<MateriaCurso> materiasCurso = materiaCursoRepo.findByCursoId(cursoId);

            // Filtrar materias si se pasó lista
            if (materiasFiltro != null) {
                materiasCurso = materiasCurso.stream()
                        .filter(mc -> materiasFiltro.contains(mc.getMateria().getId()))
                        .toList();
            }

            for (MateriaCurso mc : materiasCurso) {
                Long materiaId = mc.getMateria().getId();

                // Filtrar filas del reporte para esa materia y esa condicion (EXAMEN o COLOQUIO)
                var filasMateria = reporteRinden.getFilas().stream()
                        .filter(f -> f.getMateriaId().equals(materiaId))
                        .filter(f -> condicionAplicaATipoMesa(req.getTipoMesa(), f.getCondicion()))
                        .toList();

                // 👇 Si no hay NADIE con esa condición, NO creamos la mesa
                if (filasMateria.isEmpty()) {
                    continue;
                }

                // Crear mesa
                MesaExamen mesa = new MesaExamen();
                mesa.setMateriaCurso(mc);
                mesa.setTurno(turno);
                mesa.setFecha(fechaMesa);
                mesa.setTipoMesa(req.getTipoMesa());
                mesa.setEstado(EstadoMesaExamen.CREADA);

                // Asignar docente titular si existe
                Docente titular = mc.getDocente();
                if (titular != null) {
                    MesaExamenDocente med = MesaExamenDocente.builder()
                            .mesaExamen(mesa)
                            .docente(titular)
                            .esDocenteMateria(true)
                            .build();
                    mesa.getDocentes().add(med);
                }

                // Convocar alumnos según condición del reporte
                for (var fila : filasMateria) {
                    Alumno alumno = alumnoRepo.findById(fila.getAlumnoId())
                            .orElseThrow(() -> new MesaExamenException("Alumno no encontrado: " + fila.getAlumnoId()));

                    MesaExamenAlumno link = MesaExamenAlumno.builder()
                            .alumno(alumno)
                            .mesaExamen(mesa)
                            .estado(EstadoConvocado.CONVOCADO)
                            .condicionRinde(fila.getCondicion()) // EXAMEN o COLOQUIO según corresponda
                            .notaFinal(null)
                            .turno(turno)
                            .build();

                    // Validar que pueda inscribirse según las reglas de la mesa
                    try {
                        validacionService.validarInscribirAlumno(mesa, link);
                    } catch (IllegalArgumentException e) {
                        throw new MesaExamenException("No se puede agregar el alumno " +
                                alumno.getApellido() + ", " + alumno.getNombre() + ": " + e.getMessage());
                    }

                    mesa.getAlumnos().add(link);
                }

                // Validar configuración completa (docentes + alumnos)
                validacionService.validarConfiguracionMesa(mesa);

                mesaRepo.save(mesa);
                mesasCreadas.add(mesa);
            }
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

    // ------ helpers -------
    private static void assertEditable(MesaExamen m) {
        if (m.getEstado() == EstadoMesaExamen.FINALIZADA)
            throw new MesaExamenException("La mesa está finalizada y no puede modificarse.");
    }
    private CondicionRinde obtenerCondicionDesdeHistorial(MesaExamen mesa,
                                                          Alumno alumno,
                                                          Long materiaId,
                                                          int anioMesa) {
        // Para coloquio queremos seguir siendo estrictos:
        if (mesa.getTipoMesa() == TipoMesa.COLOQUIO) {
            throw new MesaExamenException(
                    "No se encontró información de rendimiento para el alumno " +
                            alumno.getApellido() + ", " + alumno.getNombre() +
                            " en el curso actual. Para coloquio solo se permiten alumnos del cursado vigente.");
        }

        // Buscamos en TODO el historial del alumno si tiene esa materia desaprobada
        List<HistorialCurso> historialCompleto =
                historialCursoRepository.findHistorialCompletoByAlumnoId(alumno.getId());

        boolean tieneMateriaPendiente = false;

        for (HistorialCurso hc : historialCompleto) {
            // Solo consideramos cursos de años ANTERIORES al año de la mesa
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
            return; // sin fecha no podemos decidir anio; mejor no tocar nada
        }
        int anioMesa = mesa.getFecha().getYear();

        // Historial completo del alumno
        List<HistorialCurso> historialCompleto =
                historialCursoRepository.findHistorialCompletoByAlumnoId(alumnoId);

        HistorialMateria previaMasReciente = null;
        int anioPrevioMasReciente = Integer.MIN_VALUE;

        for (HistorialCurso hc : historialCompleto) {
            int anioCurso = hc.getCicloLectivo().getFechaDesde().getYear();

            // Solo previas: años ANTERIORES al año de la mesa
            if (anioCurso >= anioMesa) {
                continue;
            }

            List<HistorialMateria> hms = historialMateriaRepository.findAllByHistorialCursoId(hc.getId());
            for (HistorialMateria hm : hms) {
                if (hm.getMateriaCurso() != null
                        && hm.getMateriaCurso().getMateria().getId().equals(materiaId)
                        && hm.getEstado() == EstadoMateriaAlumno.DESAPROBADA) {

                    // Nos quedamos con la previa DESAPROBADA más reciente
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

}