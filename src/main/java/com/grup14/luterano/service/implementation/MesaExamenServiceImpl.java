// service/implementation/MesaExamenServiceImpl.java
package com.grup14.luterano.service.implementation;
import com.grup14.luterano.dto.MesaExamenAlumnoDto;
import com.grup14.luterano.dto.MesaExamenDto;
import com.grup14.luterano.dto.mesaExamenDocente.DocenteDisponibleDto;
import com.grup14.luterano.dto.mesaExamenDocente.MesaExamenDocenteDto;
import com.grup14.luterano.entities.*;
import com.grup14.luterano.entities.enums.EstadoConvocado;
import com.grup14.luterano.entities.enums.EstadoMesaExamen;
import com.grup14.luterano.exeptions.MesaExamenException;
import com.grup14.luterano.mappers.MesaExamenMapper;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.request.mesaExamen.AgregarConvocadosRequest;
import com.grup14.luterano.request.mesaExamen.MesaExamenCreateRequest;
import com.grup14.luterano.request.mesaExamen.MesaExamenUpdateRequest;
import com.grup14.luterano.request.mesaExamenDocente.AsignarDocentesRequest;
import com.grup14.luterano.response.mesaExamen.MesaExamenListResponse;
import com.grup14.luterano.response.mesaExamen.MesaExamenResponse;
import com.grup14.luterano.response.mesaExamenDocente.DocentesDisponiblesResponse;
import com.grup14.luterano.response.mesaExamenDocente.MesaExamenDocentesResponse;
import com.grup14.luterano.service.MesaExamenService;
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

    private final MesaExamenRepository mesaRepo;
    private final MesaExamenAlumnoRepository mesaAluRepo;
    private final MateriaCursoRepository materiaCursoRepo;
    private final TurnoExamenRepository turnoRepo;
    private final AlumnoRepository alumnoRepo;
    private final AulaRepository aulaRepo;
    private final DocenteRepository docenteRepo;
    private final MesaExamenDocenteRepository mesaExamenDocenteRepo;

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

        // Validar que sean exactamente 3 docentes
        if (request.getDocenteIds().size() != 3) {
            throw new MesaExamenException("Debe asignar exactamente 3 docentes");
        }

        // Validar que no haya docentes duplicados
        Set<Long> uniqueDocentes = new HashSet<>(request.getDocenteIds());
        if (uniqueDocentes.size() != 3) {
            throw new MesaExamenException("No se pueden asignar docentes duplicados");
        }

        // Validar que todos los docentes existan
        List<Docente> docentes = docenteRepo.findAllById(request.getDocenteIds());
        if (docentes.size() != 3) {
            throw new MesaExamenException("Uno o más docentes no existen");
        }

        // Verificar que al menos uno dé la materia
        Long materiaId = mesa.getMateriaCurso().getMateria().getId();
        Set<Long> docentesQueDALaMateria = materiaCursoRepo.findByMateriaId(materiaId)
                .stream()
                .filter(mc -> mc.getDocente() != null) // Solo los que tienen docente asignado
                .map(mc -> mc.getDocente().getId())
                .collect(Collectors.toSet());

        boolean tieneDocenteMateria = request.getDocenteIds().stream()
                .anyMatch(docentesQueDALaMateria::contains);

        if (!tieneDocenteMateria) {
            throw new MesaExamenException("Debe asignar al menos un docente que dé la materia: " + 
                    mesa.getMateriaCurso().getMateria().getNombre());
        }

        // Verificar conflictos horarios
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

        // Actualizar la asignación
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
                .esDocenteMateria(asignacion.isEsDocenteMateria())
                .build();
    }

    // ------ helpers -------
    private static void assertEditable(MesaExamen m) {
        if (m.getEstado() == EstadoMesaExamen.FINALIZADA)
            throw new MesaExamenException("La mesa está finalizada y no puede modificarse.");
    }
}