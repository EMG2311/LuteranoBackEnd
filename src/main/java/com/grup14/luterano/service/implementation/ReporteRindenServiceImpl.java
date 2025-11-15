package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.ReporteRindeDto;
import com.grup14.luterano.entities.*;
import com.grup14.luterano.entities.enums.CondicionRinde;
import com.grup14.luterano.exeptions.ReporteRindeException;
import com.grup14.luterano.mappers.CursoMapper;
import com.grup14.luterano.repository.CalificacionRepository;
import com.grup14.luterano.repository.CicloLectivoRepository;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.repository.HistorialCursoRepository;
import com.grup14.luterano.repository.MateriaCursoRepository;
import com.grup14.luterano.repository.MesaExamenAlumnoRepository;
import com.grup14.luterano.response.reporteRinden.ReporteRindenResponse;
import com.grup14.luterano.service.ReporteRindenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReporteRindenServiceImpl implements ReporteRindenService {

    private final CursoRepository cursoRepository;
    private final CicloLectivoRepository cicloLectivoRepository;
    private final HistorialCursoRepository historialCursoRepository;
    private final CalificacionRepository calificacionRepository;
    private final MesaExamenAlumnoRepository mesaExamenAlumnoRepo;
    private final MateriaCursoRepository materiaCursoRepo;

    @Transactional(readOnly = true)
    public ReporteRindenResponse listarRindenPorCurso(Long cursoId, int anio) {
        return listarRindenPorCurso(cursoId, anio, false);
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteRindenResponse listarRindenPorCurso(Long cursoId, int anio, boolean incluirPrevias) {
        if (cursoId == null) throw new ReporteRindeException("cursoId es requerido");

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ReporteRindeException("Curso no encontrado (id=" + cursoId + ")"));

        LocalDate pivote = LocalDate.of(anio, 7, 1);
        CicloLectivo ciclo = cicloLectivoRepository
                .findByFechaDesdeBeforeAndFechaHastaAfter(pivote, pivote)
                .orElseThrow(() -> new ReporteRindeException("No hay ciclo lectivo para el año " + anio));

        List<HistorialCurso> hcs = historialCursoRepository.findAbiertosByCursoAndCicloExcluyendoInactivos(cursoId, ciclo.getId());
        List<Alumno> alumnos = hcs.stream().map(HistorialCurso::getAlumno).toList();
        if (alumnos.isEmpty()) {
            return ReporteRindenResponse.builder()
                    .curso(CursoMapper.toDto(curso))
                    .anio(anio)
                    .filas(List.of())
                    .total(0).totalColoquio(0).totalExamen(0)
                    .code(0).mensaje("OK")
                    .build();
        }

        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);
        List<Long> alumnoIds = alumnos.stream().map(Alumno::getId).toList();
        List<Calificacion> califs = calificacionRepository
                .findByAlumnosCursoCicloAndAnio(alumnoIds, cursoId, ciclo.getId(), desde, hasta);

        class Agg {
            int sum1 = 0, cnt1 = 0, sum2 = 0, cnt2 = 0;
            String materiaNombre;
        }
        Map<Long, Map<Long, Agg>> map = new LinkedHashMap<>();

        for (Calificacion c : califs) {
            var hm = c.getHistorialMateria();
            var hc = hm.getHistorialCurso();
            Long aId = hc.getAlumno().getId();
            Long mId = hm.getMateriaCurso().getMateria().getId();
            String mNombre = hm.getMateriaCurso().getMateria().getNombre();

            var porMateria = map.computeIfAbsent(aId, k -> new LinkedHashMap<>());
            var agg = porMateria.computeIfAbsent(mId, k -> new Agg());
            agg.materiaNombre = mNombre;

            Integer nota = c.getNota();
            if (nota == null) continue;
            if (c.getEtapa() == 1) {
                agg.sum1 += nota;
                agg.cnt1++;
            } else if (c.getEtapa() == 2) {
                agg.sum2 += nota;
                agg.cnt2++;
            }
        }

        Map<Long, Alumno> idxAlumno = alumnos.stream()
                .collect(Collectors.toMap(Alumno::getId, a -> a));

        List<ReporteRindeDto> filas = new ArrayList<>();
        for (var eAlu : map.entrySet()) {
            Long alumnoId = eAlu.getKey();
            Alumno a = idxAlumno.get(alumnoId);
            if (a == null) continue;

            for (var eMat : eAlu.getValue().entrySet()) {
                Long materiaId = eMat.getKey();
                Agg agg = eMat.getValue();

                Double e1 = (agg.cnt1 == 0) ? null : round1((double) agg.sum1 / agg.cnt1);
                Double e2 = (agg.cnt2 == 0) ? null : round1((double) agg.sum2 / agg.cnt2);
                Double pg = pg(e1, e2);

                // Buscar nota de mesa de examen para este alumno y materia en el año                
                List<MesaExamenAlumno> mesas = mesaExamenAlumnoRepo
                    .findByAlumno_IdAndMesaExamen_FechaBetween(alumnoId, ciclo.getFechaDesde(), ciclo.getFechaHasta());
                
                // Filtrar por materia y obtener la más reciente
                MesaExamenAlumno mesaMasReciente = mesas.stream()
                    .filter(mea -> mea.getMesaExamen().getMateriaCurso().getMateria().getId().equals(materiaId))
                    .filter(mea -> mea.getNotaFinal() != null)
                    .max((mesa1, mesa2) -> {
                        LocalDate fecha1 = mesa1.getMesaExamen().getFecha();
                        LocalDate fecha2 = mesa2.getMesaExamen().getFecha();
                        if (fecha1 == null && fecha2 == null) return 0;
                        if (fecha1 == null) return -1;
                        if (fecha2 == null) return 1;
                        return fecha1.compareTo(fecha2);
                    })
                    .orElse(null);

                // Determinar notas de coloquio y examen
                Integer notaCo = null;
                Integer notaEx = null;
                Double notaPf = null;

                if (mesaMasReciente != null) {
                    boolean apr1 = e1 != null && e1 >= 6.0;
                    boolean apr2 = e2 != null && e2 >= 6.0;
                    
                    // Si aprobó solo una etapa → COLOQUIO
                    // Si desaprobó ambas etapas → EXAMEN FINAL
                    if (apr1 ^ apr2) {  // XOR - solo una etapa aprobada
                        notaCo = mesaMasReciente.getNotaFinal();
                        notaEx = null;
                    } else {  // ambas desaprobadas
                        notaCo = null;
                        notaEx = mesaMasReciente.getNotaFinal();
                    }
                    notaPf = mesaMasReciente.getNotaFinal().doubleValue();
                } else if (pg != null) {
                    // Si no hay mesa de examen, PF = PG redondeado
                    notaPf = (double) Math.round(pg);
                }

                boolean apr1 = e1 != null && e1 >= 6.0;
                boolean apr2 = e2 != null && e2 >= 6.0;

                // Si promocionó (ambas etapas >= 6), no rinde
                if (apr1 && apr2) continue;

                // Si ya aprobó en mesa (nota >= 6), tampoco debe aparecer como elegible
                if (mesaMasReciente != null && mesaMasReciente.getNotaFinal() != null && 
                    mesaMasReciente.getNotaFinal() >= 6) {
                    continue; // Ya aprobó la materia por mesa, no necesita rendir más
                }

                CondicionRinde cond = (apr1 ^ apr2)
                        ? CondicionRinde.COLOQUIO
                        : CondicionRinde.EXAMEN;

                filas.add(ReporteRindeDto.builder()
                        .alumnoId(a.getId())
                        .dni(a.getDni())
                        .apellido(a.getApellido())
                        .nombre(a.getNombre())
                        .materiaId(materiaId)
                        .materiaNombre(agg.materiaNombre)
                        .e1(e1).e2(e2).pg(pg)
                        .co(notaCo).ex(notaEx).pf(notaPf)
                        .condicion(cond)
                        .estadoAcademico("DEBE_RENDIR") // Los que aparecen aquí siempre deben rendir
                        .build());
            }
        }

        // Orden visual: Apellido, Nombre, Materia
        var coll = java.text.Collator.getInstance(new java.util.Locale("es", "AR"));
        coll.setStrength(java.text.Collator.PRIMARY);
        filas.sort(Comparator
                .comparing(ReporteRindeDto::getApellido, coll)
                .thenComparing(ReporteRindeDto::getNombre, coll)
                .thenComparing(ReporteRindeDto::getMateriaNombre, coll));

        // Si se solicita incluir previas, agregar alumnos de otros cursos que deben materias de este curso
        if (incluirPrevias) {
            List<ReporteRindeDto> filasPrevias = obtenerAlumnosConPrevias(cursoId, ciclo, alumnoIds);
            filas.addAll(filasPrevias);
            
            // Reordenar con las previas incluidas
            filas.sort(Comparator
                    .comparing(ReporteRindeDto::getApellido, coll)
                    .thenComparing(ReporteRindeDto::getNombre, coll)
                    .thenComparing(ReporteRindeDto::getMateriaNombre, coll));
        }

        int totalColoquio = (int) filas.stream().filter(f -> f.getCondicion() == CondicionRinde.COLOQUIO).count();
        int totalExamen = (int) filas.stream().filter(f -> f.getCondicion() == CondicionRinde.EXAMEN).count();

        return ReporteRindenResponse.builder()
                .curso(CursoMapper.toDto(curso))
                .anio(anio)
                .filas(filas)
                .total(filas.size())
                .totalColoquio(totalColoquio)
                .totalExamen(totalExamen)
                .code(0)
                .mensaje("OK")
                .build();
    }

    @Override
    public ReporteRindenResponse listarTodosLosAlumnosPorCurso(Long cursoId, int anio) {
        if (cursoId == null) throw new ReporteRindeException("cursoId es requerido");
        
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ReporteRindeException("Curso no encontrado (id=" + cursoId + ")"));

        // Usar lógica similar pero incluir TODOS los alumnos
        LocalDate pivote = LocalDate.of(anio, 7, 1);
        CicloLectivo ciclo = cicloLectivoRepository
                .findByFechaDesdeBeforeAndFechaHastaAfter(pivote, pivote)
                .orElseThrow(() -> new ReporteRindeException("No hay ciclo lectivo para el año " + anio));

        // Obtener TODOS los alumnos del curso (incluyendo promovidos)
        List<HistorialCurso> historiales = historialCursoRepository.findAbiertosByCursoAndCicloExcluyendoInactivos(cursoId, ciclo.getId());
        List<Alumno> alumnos = historiales.stream().map(HistorialCurso::getAlumno).toList();
        if (alumnos.isEmpty()) {
            return ReporteRindenResponse.builder()
                    .curso(CursoMapper.toDto(curso))
                    .anio(anio)
                    .filas(List.of())
                    .total(0)
                    .totalColoquio(0)
                    .totalExamen(0)
                    .code(0)
                    .mensaje("No hay alumnos registrados en este curso para el año " + anio)
                    .build();
        }

        List<ReporteRindeDto> filas = new ArrayList<>();

        // Usar la misma lógica que el método original pero sin filtros de promoción
        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);
        List<Long> alumnoIds = alumnos.stream().map(Alumno::getId).toList();
        List<Calificacion> califs = calificacionRepository
                .findByAlumnosCursoCicloAndAnio(alumnoIds, cursoId, ciclo.getId(), desde, hasta);

        class Agg {
            int sum1 = 0, cnt1 = 0, sum2 = 0, cnt2 = 0;
            String materiaNombre;
        }
        Map<Long, Map<Long, Agg>> map = new LinkedHashMap<>();

        for (Calificacion c : califs) {
            var hm = c.getHistorialMateria();
            var hc = hm.getHistorialCurso();
            Long aId = hc.getAlumno().getId();
            Long mId = hm.getMateriaCurso().getMateria().getId();
            String mNombre = hm.getMateriaCurso().getMateria().getNombre();

            var porMateria = map.computeIfAbsent(aId, k -> new LinkedHashMap<>());
            var agg = porMateria.computeIfAbsent(mId, k -> new Agg());
            agg.materiaNombre = mNombre;

            Integer nota = c.getNota();
            if (nota == null) continue;
            if (c.getEtapa() == 1) {
                agg.sum1 += nota;
                agg.cnt1++;
            } else if (c.getEtapa() == 2) {
                agg.sum2 += nota;
                agg.cnt2++;
            }
        }

        Map<Long, Alumno> idxAlumno = alumnos.stream()
                .collect(Collectors.toMap(Alumno::getId, a -> a));

        for (var eAlu : map.entrySet()) {
            Long alumnoId = eAlu.getKey();
            Alumno a = idxAlumno.get(alumnoId);
            if (a == null) continue;

            for (var eMat : eAlu.getValue().entrySet()) {
                Long materiaId = eMat.getKey();
                Agg agg = eMat.getValue();

                Double e1 = (agg.cnt1 == 0) ? null : round1((double) agg.sum1 / agg.cnt1);
                Double e2 = (agg.cnt2 == 0) ? null : round1((double) agg.sum2 / agg.cnt2);
                Double pg = pg(e1, e2);

                // Buscar nota de mesa de examen para este alumno y materia en el año                
                List<MesaExamenAlumno> mesas = mesaExamenAlumnoRepo
                    .findByAlumno_IdAndMesaExamen_FechaBetween(alumnoId, ciclo.getFechaDesde(), ciclo.getFechaHasta());
                
                // Filtrar por materia y obtener la más reciente
                MesaExamenAlumno mesaMasReciente = mesas.stream()
                    .filter(mea -> mea.getMesaExamen().getMateriaCurso().getMateria().getId().equals(materiaId))
                    .max((ma1, ma2) -> {
                        LocalDate fecha1 = ma1.getMesaExamen().getFecha();
                        LocalDate fecha2 = ma2.getMesaExamen().getFecha();
                        if (fecha1 == null) return -1;
                        if (fecha2 == null) return 1;
                        return fecha1.compareTo(fecha2);
                    })
                    .orElse(null);

                // Determinar notas de coloquio y examen
                Integer notaCo = null;
                Integer notaEx = null;
                Double notaPf = null;

                boolean apr1 = e1 != null && e1 >= 6.0;
                boolean apr2 = e2 != null && e2 >= 6.0;

                String estadoAcademico;
                CondicionRinde condicion = null;

                if (apr1 && apr2) {
                    // PROMOCIONADO - ambas etapas >= 6
                    estadoAcademico = "PROMOCIONADO";
                    notaPf = pg;
                } else if (mesaMasReciente != null && mesaMasReciente.getNotaFinal() != null && 
                          mesaMasReciente.getNotaFinal() >= 6) {
                    // APROBADO POR MESA
                    estadoAcademico = "APROBADO_MESA";
                    notaPf = mesaMasReciente.getNotaFinal().doubleValue();
                    
                    // Determinar si fue coloquio o examen final
                    if (apr1 ^ apr2) {
                        notaCo = mesaMasReciente.getNotaFinal();
                        condicion = CondicionRinde.COLOQUIO;
                    } else {
                        notaEx = mesaMasReciente.getNotaFinal();
                        condicion = CondicionRinde.EXAMEN;
                    }
                } else {
                    // DEBE RENDIR
                    estadoAcademico = "DEBE_RENDIR";
                    condicion = (apr1 ^ apr2) ? CondicionRinde.COLOQUIO : CondicionRinde.EXAMEN;
                    
                    if (mesaMasReciente != null) {
                        if (condicion == CondicionRinde.COLOQUIO) {
                            notaCo = mesaMasReciente.getNotaFinal();
                        } else {
                            notaEx = mesaMasReciente.getNotaFinal();
                        }
                        notaPf = mesaMasReciente.getNotaFinal() != null ? mesaMasReciente.getNotaFinal().doubleValue() : null;
                    } else if (pg != null) {
                        notaPf = (double) Math.round(pg);
                    }
                }

                filas.add(ReporteRindeDto.builder()
                        .alumnoId(a.getId())
                        .dni(a.getDni())
                        .apellido(a.getApellido())
                        .nombre(a.getNombre())
                        .materiaId(materiaId)
                        .materiaNombre(agg.materiaNombre)
                        .e1(e1).e2(e2).pg(pg)
                        .co(notaCo).ex(notaEx).pf(notaPf)
                        .condicion(condicion)
                        .estadoAcademico(estadoAcademico)
                        .build());
            }
        }

        // Ordenar por apellido, nombre, materia
        var coll = java.text.Collator.getInstance(new java.util.Locale("es", "AR"));
        coll.setStrength(java.text.Collator.PRIMARY);
        filas.sort(Comparator
                .comparing(ReporteRindeDto::getApellido, coll)
                .thenComparing(ReporteRindeDto::getNombre, coll)
                .thenComparing(ReporteRindeDto::getMateriaNombre, coll));

        int totalColoquio = (int) filas.stream().filter(f -> f.getCondicion() == CondicionRinde.COLOQUIO).count();
        int totalExamen = (int) filas.stream().filter(f -> f.getCondicion() == CondicionRinde.EXAMEN).count();
        int totalAprobados = (int) filas.stream().filter(f -> 
            "PROMOCIONADO".equals(f.getEstadoAcademico()) || "APROBADO_MESA".equals(f.getEstadoAcademico())
        ).count();

        return ReporteRindenResponse.builder()
                .curso(CursoMapper.toDto(curso))
                .anio(anio)
                .filas(filas)
                .total(filas.size())
                .totalColoquio(totalColoquio)
                .totalExamen(totalExamen)
                .code(0)
                .mensaje("OK - Total aprobados: " + totalAprobados)
                .build();
    }

    // helpers
    private static Double pg(Double e1, Double e2) {
        if (e1 == null && e2 == null) return null;
        if (e1 == null) return e2;
        if (e2 == null) return e1;
        return round1((e1 + e2) / 2.0);
    }

    private static Double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
    
    /**
     * Obtiene alumnos de otros cursos que tienen materias desaprobadas (previas) del curso consultado
     */
    private List<ReporteRindeDto> obtenerAlumnosConPrevias(Long cursoId, CicloLectivo cicloActual, List<Long> alumnosDelCursoActual) {
        List<ReporteRindeDto> resultado = new ArrayList<>();
        
        // Obtener todas las materias del curso consultado
        List<MateriaCurso> materiasCurso = materiaCursoRepo.findByCursoId(cursoId);
        if (materiasCurso.isEmpty()) {
            return resultado;
        }
        
        Set<Long> materiasIds = materiasCurso.stream()
                .map(mc -> mc.getMateria().getId())
                .collect(Collectors.toSet());
        
        // Buscar alumnos activos que NO estén en el curso actual
        List<HistorialCurso> todosHistoriales = historialCursoRepository
                .findAbiertosByCicloExcluyendoInactivos(cicloActual.getId());
        
        List<Alumno> alumnosOtrosCursos = todosHistoriales.stream()
                .map(HistorialCurso::getAlumno)
                .filter(a -> !alumnosDelCursoActual.contains(a.getId()))
                .distinct()
                .toList();
        
        if (alumnosOtrosCursos.isEmpty()) {
            return resultado;
        }
        
        // Para cada alumno de otros cursos, buscar si tiene previas de las materias de este curso
        for (Alumno alumno : alumnosOtrosCursos) {
            // Buscar historiales de años anteriores
            List<HistorialCurso> historialesAnteriores = historialCursoRepository
                    .findByAlumno_IdOrderByCicloLectivo_FechaDesdeDesc(alumno.getId());
            
            for (HistorialCurso hc : historialesAnteriores) {
                // Solo buscar en ciclos anteriores al actual
                if (hc.getCicloLectivo().getFechaDesde().isAfter(cicloActual.getFechaDesde()) ||
                    hc.getCicloLectivo().getFechaDesde().isEqual(cicloActual.getFechaDesde())) {
                    continue;
                }
                
                // Buscar materias desaprobadas del historial
                for (HistorialMateria hm : hc.getMateriasHistorial()) {
                    Long materiaId = hm.getMateriaCurso().getMateria().getId();
                    
                    // Verificar si esta materia pertenece al curso consultado y está desaprobada
                    if (materiasIds.contains(materiaId) && 
                        hm.getEstado() == com.grup14.luterano.entities.enums.EstadoMateriaAlumno.DESAPROBADA) {
                        
                        // Verificar si ya aprobó la materia en una mesa posterior
                        List<MesaExamenAlumno> mesasPosteriores = mesaExamenAlumnoRepo
                                .findByAlumno_IdAndMesaExamen_MateriaCurso_Materia_Id(alumno.getId(), materiaId);
                        
                        boolean yaAprobo = mesasPosteriores.stream()
                                .anyMatch(mea -> mea.getNotaFinal() != null && mea.getNotaFinal() >= 6);
                        
                        if (!yaAprobo) {
                            // Buscar la última nota de mesa para esta materia
                            MesaExamenAlumno ultimaMesa = mesasPosteriores.stream()
                                    .filter(mea -> mea.getNotaFinal() != null)
                                    .max(Comparator.comparing(mea -> mea.getMesaExamen().getFecha()))
                                    .orElse(null);
                            
                            Integer notaEx = ultimaMesa != null ? ultimaMesa.getNotaFinal() : null;
                            
                            // Agregar como fila de previa (siempre rinde EXAMEN porque es previa)
                            resultado.add(ReporteRindeDto.builder()
                                    .alumnoId(alumno.getId())
                                    .dni(alumno.getDni())
                                    .apellido(alumno.getApellido())
                                    .nombre(alumno.getNombre())
                                    .materiaId(materiaId)
                                    .materiaNombre(hm.getMateriaCurso().getMateria().getNombre())
                                    .e1(null)  // No tiene etapas del año actual
                                    .e2(null)
                                    .pg(null)
                                    .co(null)  // Previas siempre rinden examen
                                    .ex(notaEx)
                                    .pf(notaEx != null ? notaEx.doubleValue() : null)
                                    .condicion(CondicionRinde.EXAMEN)  // Previas siempre rinden EXAMEN
                                    .estadoAcademico("PREVIA")  // Indicador de que es una previa
                                    .build());
                        }
                    }
                }
            }
        }
        
        return resultado;
    }
}
