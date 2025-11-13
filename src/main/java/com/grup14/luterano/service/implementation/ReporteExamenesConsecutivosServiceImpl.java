package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.reporteExamenesConsecutivos.ReporteExamenesConsecutivosDto;
import com.grup14.luterano.entities.Calificacion;
import com.grup14.luterano.entities.CicloLectivo;
import com.grup14.luterano.repository.CalificacionRepository;
import com.grup14.luterano.repository.CicloLectivoRepository;
import com.grup14.luterano.response.reporteExamenesConsecutivos.ReporteExamenesConsecutivosResponse;
import com.grup14.luterano.response.reporteExamenesConsecutivos.ResumenPorMateriaDto;
import com.grup14.luterano.service.ReporteExamenesConsecutivosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteExamenesConsecutivosServiceImpl implements ReporteExamenesConsecutivosService {

    private final CalificacionRepository calificacionRepository;
    private final CicloLectivoRepository cicloLectivoRepository;

    @Override
    public ReporteExamenesConsecutivosResponse generarReporte(Integer cicloLectivoAnio) {
        return generarReporteInterno(cicloLectivoAnio, null, null);
    }

    @Override
    public ReporteExamenesConsecutivosResponse generarReportePorMateria(Integer cicloLectivoAnio, Long materiaId) {
        return generarReporteInterno(cicloLectivoAnio, materiaId, null);
    }

    @Override
    public ReporteExamenesConsecutivosResponse generarReportePorCurso(Integer cicloLectivoAnio, Long cursoId) {
        return generarReporteInterno(cicloLectivoAnio, null, cursoId);
    }

    private ReporteExamenesConsecutivosResponse generarReporteInterno(Integer cicloLectivoAnio, Long materiaId, Long cursoId) {
        try {
            log.info("Generando reporte de exámenes consecutivos para año: {}, materia: {}, curso: {}", 
                    cicloLectivoAnio, materiaId, cursoId);

            // Buscar ciclo lectivo
            Optional<CicloLectivo> cicloOpt = cicloLectivoRepository.findByAnio(cicloLectivoAnio);
            if (cicloOpt.isEmpty()) {
                return ReporteExamenesConsecutivosResponse.builder()
                        .code(-1)
                        .mensaje("No se encontró el ciclo lectivo para el año " + cicloLectivoAnio)
                        .build();
            }

            CicloLectivo ciclo = cicloOpt.get();

            // Obtener todas las calificaciones del año según el filtro
            List<Calificacion> calificaciones;
            if (materiaId != null) {
                calificaciones = calificacionRepository.findCalificacionesParaAnalisisConsecutivoPorMateria(cicloLectivoAnio, materiaId);
            } else if (cursoId != null) {
                calificaciones = calificacionRepository.findCalificacionesParaAnalisisConsecutivoPorCurso(cicloLectivoAnio, cursoId);
            } else {
                calificaciones = calificacionRepository.findCalificacionesParaAnalisisConsecutivo(cicloLectivoAnio);
            }

            if (calificaciones.isEmpty()) {
                return construirRespuestaVacia(cicloLectivoAnio, ciclo.getNombre());
            }

            // Agrupar calificaciones por alumno y materia (ya están filtradas por las queries)
            Map<String, List<Calificacion>> calificacionesPorAlumnoMateria = agruparCalificaciones(calificaciones);

            // Analizar casos consecutivos
            List<ReporteExamenesConsecutivosDto> casosDetectados = analizarCasosConsecutivos(calificacionesPorAlumnoMateria);

            // Construir respuesta completa
            return construirRespuestaCompleta(cicloLectivoAnio, ciclo.getNombre(), casosDetectados);

        } catch (Exception e) {
            log.error("Error generando reporte de exámenes consecutivos", e);
            return ReporteExamenesConsecutivosResponse.builder()
                    .code(-1)
                    .mensaje("Error al generar reporte: " + e.getMessage())
                    .build();
        }
    }

    private Map<String, List<Calificacion>> agruparCalificaciones(List<Calificacion> calificaciones) {
        return calificaciones.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getHistorialMateria().getHistorialCurso().getAlumno().getId() + 
                             "_" + c.getHistorialMateria().getMateriaCurso().getMateria().getId(),
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(Calificacion::getEtapa)
                                                .thenComparing(Calificacion::getNumeroNota))
                                        .collect(Collectors.toList())
                        )
                ));
    }

    private List<ReporteExamenesConsecutivosDto> analizarCasosConsecutivos(Map<String, List<Calificacion>> calificacionesPorAlumnoMateria) {
        List<ReporteExamenesConsecutivosDto> casosDetectados = new ArrayList<>();

        for (Map.Entry<String, List<Calificacion>> entry : calificacionesPorAlumnoMateria.entrySet()) {
            List<Calificacion> calificaciones = entry.getValue();
            
            if (calificaciones.size() < 2) {
                continue; // Necesitamos al menos 2 calificaciones
            }

            // Analizar secuencias consecutivas desaprobadas
            for (int i = 0; i < calificaciones.size() - 1; i++) {
                Calificacion actual = calificaciones.get(i);
                Calificacion siguiente = calificaciones.get(i + 1);

                if (sonConsecutivas(actual, siguiente) && 
                    esDesaprobada(actual.getNota()) && 
                    esDesaprobada(siguiente.getNota())) {
                    
                    ReporteExamenesConsecutivosDto caso = construirCasoDetectado(actual, siguiente);
                    if (caso != null) {
                        casosDetectados.add(caso);
                    }
                }
            }
        }

        return casosDetectados;
    }

    private boolean sonConsecutivas(Calificacion actual, Calificacion siguiente) {
        // Caso 1: Misma etapa, números consecutivos
        if (actual.getEtapa() == siguiente.getEtapa()) {
            return siguiente.getNumeroNota() == actual.getNumeroNota() + 1;
        }
        
        // Caso 2: Última nota de etapa 1 y primera nota de etapa 2
        if (actual.getEtapa() == 1 && siguiente.getEtapa() == 2) {
            return actual.getNumeroNota() == 4 && siguiente.getNumeroNota() == 1;
        }
        
        return false;
    }

    private boolean esDesaprobada(Integer nota) {
        return nota != null && nota < 6; // Nota menor a 6 es desaprobada (6 se aprueba)
    }

    private ReporteExamenesConsecutivosDto construirCasoDetectado(Calificacion primera, Calificacion segunda) {
        try {
            var historialMateria = primera.getHistorialMateria();
            var alumno = historialMateria.getHistorialCurso().getAlumno();
            var materia = historialMateria.getMateriaCurso().getMateria();
        var curso = historialMateria.getMateriaCurso().getCurso();

        // Obtener información del docente asignado a la materia-curso (si existe)
        var materiaCurso = historialMateria.getMateriaCurso();
        var docente = materiaCurso.getDocente(); // puede ser null

        String docenteNombreCompleto = null;
        Long docenteId = null;
        String docenteNombre = null;
        String docenteApellido = null;

        if (docente != null) {
        docenteId = docente.getId();
        docenteNombre = docente.getNombre();
        docenteApellido = docente.getApellido();
        docenteNombreCompleto = docente.getApellido() + ", " + docente.getNombre();
        }

        String descripcion = String.format("%dº nota Etapa %d y %dº nota Etapa %d",
            primera.getNumeroNota(), primera.getEtapa(),
            segunda.getNumeroNota(), segunda.getEtapa());

        String estadoRiesgo = determinarEstadoRiesgo(primera.getNota(), segunda.getNota());

        return ReporteExamenesConsecutivosDto.builder()
                    .alumnoId(alumno.getId())
                    .alumnoNombre(alumno.getNombre())
                    .alumnoApellido(alumno.getApellido())
                    .nombreCompleto(alumno.getApellido() + ", " + alumno.getNombre())
                    .materiaId(materia.getId())
                    .materiaNombre(materia.getNombre())
                    .cursoId(curso.getId())
                    .cursoNombre(curso.getAnio() + "° " + curso.getNivel().toString() + " " + curso.getDivision().toString())
                    .anio(curso.getAnio())
                    .division(curso.getDivision().toString())
                    .primeraNota(primera.getNota())
                    .etapaPrimeraNota(primera.getEtapa())
                    .numeroPrimeraNota(primera.getNumeroNota())
                    .segundaNota(segunda.getNota())
                    .etapaSegundaNota(segunda.getEtapa())
                    .numeroSegundaNota(segunda.getNumeroNota())
                    .descripcionConsecutivo(descripcion)
                    .estadoRiesgo(estadoRiesgo)
                    .docenteId(docenteId)
                    .docenteNombre(docenteNombre)
                    .docenteApellido(docenteApellido)
                    .docenteNombreCompleto(docenteNombreCompleto)
                    .build();

        } catch (Exception e) {
            log.error("Error construyendo caso detectado", e);
            return null;
        }
    }

    private String determinarEstadoRiesgo(Integer nota1, Integer nota2) {
        double promedio = (nota1 + nota2) / 2.0;
        
        if (promedio <= 4.0) {
            return "CRÍTICO";
        } else if (promedio <= 5.0) {
            return "ALTO";
        } else {
            return "MEDIO";
        }
    }

    private ReporteExamenesConsecutivosResponse construirRespuestaVacia(Integer cicloLectivoAnio, String nombreCiclo) {
        return ReporteExamenesConsecutivosResponse.builder()
                .cicloLectivoAnio(cicloLectivoAnio)
                .nombreCicloLectivo(nombreCiclo)
                .totalAlumnosEnRiesgo(0)
                .totalMateriasAfectadas(0)
                .totalCursosAfectados(0)
                .casosDetectados(new ArrayList<>())
                .casosCriticos(0)
                .casosAltos(0)
                .casosMedios(0)
                .resumenPorMateria(new ArrayList<>())
                .recomendaciones(List.of("No se detectaron casos de exámenes consecutivos desaprobados"))
                .code(0)
                .mensaje("Reporte generado exitosamente - Sin casos detectados")
                .build();
    }

    private ReporteExamenesConsecutivosResponse construirRespuestaCompleta(Integer cicloLectivoAnio, String nombreCiclo, 
                                                                          List<ReporteExamenesConsecutivosDto> casosDetectados) {
        
        // Estadísticas generales
        int totalAlumnos = (int) casosDetectados.stream().mapToLong(ReporteExamenesConsecutivosDto::getAlumnoId).distinct().count();
        int totalMaterias = (int) casosDetectados.stream().mapToLong(ReporteExamenesConsecutivosDto::getMateriaId).distinct().count();
        int totalCursos = (int) casosDetectados.stream().mapToLong(ReporteExamenesConsecutivosDto::getCursoId).distinct().count();

        // Estadísticas por nivel de riesgo
        int casosCriticos = (int) casosDetectados.stream().filter(c -> "CRÍTICO".equals(c.getEstadoRiesgo())).count();
        int casosAltos = (int) casosDetectados.stream().filter(c -> "ALTO".equals(c.getEstadoRiesgo())).count();
        int casosMedios = (int) casosDetectados.stream().filter(c -> "MEDIO".equals(c.getEstadoRiesgo())).count();

        // Resumen por materia
        var resumenPorMateria = construirResumenPorMateria(casosDetectados);

        // Recomendaciones
        var recomendaciones = generarRecomendaciones(casosDetectados, casosCriticos, casosAltos, casosMedios);

        return ReporteExamenesConsecutivosResponse.builder()
                .cicloLectivoAnio(cicloLectivoAnio)
                .nombreCicloLectivo(nombreCiclo)
                .totalAlumnosEnRiesgo(totalAlumnos)
                .totalMateriasAfectadas(totalMaterias)
                .totalCursosAfectados(totalCursos)
                .casosDetectados(casosDetectados)
                .casosCriticos(casosCriticos)
                .casosAltos(casosAltos)
                .casosMedios(casosMedios)
                .resumenPorMateria(resumenPorMateria)
                .recomendaciones(recomendaciones)
                .code(0)
                .mensaje("Reporte generado exitosamente")
                .build();
    }

    private List<ResumenPorMateriaDto> construirResumenPorMateria(List<ReporteExamenesConsecutivosDto> casosDetectados) {
        Map<Long, List<ReporteExamenesConsecutivosDto>> casosPorMateria = casosDetectados.stream()
                .collect(Collectors.groupingBy(ReporteExamenesConsecutivosDto::getMateriaId));

        return casosPorMateria.entrySet().stream()
                .map(entry -> {
                    List<ReporteExamenesConsecutivosDto> casos = entry.getValue();
                    String materiaNombre = casos.get(0).getMateriaNombre();
                    
                    int totalCasos = casos.size();
                    int criticos = (int) casos.stream().filter(c -> "CRÍTICO".equals(c.getEstadoRiesgo())).count();
                    int altos = (int) casos.stream().filter(c -> "ALTO".equals(c.getEstadoRiesgo())).count();
                    int medios = (int) casos.stream().filter(c -> "MEDIO".equals(c.getEstadoRiesgo())).count();

                    return ResumenPorMateriaDto.builder()
                            .materiaId(entry.getKey())
                            .materiaNombre(materiaNombre)
                            .totalCasos(totalCasos)
                            .casosCriticos(criticos)
                            .casosAltos(altos)
                            .casosMedios(medios)
                            .porcentajeAfectacion(null) // Se puede calcular posteriormente si se tiene el total de alumnos por materia
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<String> generarRecomendaciones(List<ReporteExamenesConsecutivosDto> casosDetectados, 
                                               int casosCriticos, int casosAltos, int casosMedios) {
        List<String> recomendaciones = new ArrayList<>();

        if (casosDetectados.isEmpty()) {
            recomendaciones.add("Excelente: No se detectaron casos de exámenes consecutivos desaprobados");
            return recomendaciones;
        }

        if (casosCriticos > 0) {
            recomendaciones.add(String.format("URGENTE: %d casos críticos requieren intervención inmediata (promedio ≤ 4)", casosCriticos));
            recomendaciones.add("Implementar plan de apoyo académico intensivo para casos críticos");
        }

        if (casosAltos > 0) {
            recomendaciones.add(String.format("ATENCIÓN: %d casos de alto riesgo necesitan seguimiento cercano", casosAltos));
        }

        if (casosMedios > 0) {
            recomendaciones.add(String.format("PREVENCIÓN: %d casos de riesgo medio requieren monitoreo", casosMedios));
        }

        // Recomendaciones específicas por materia más afectada
        Map<String, Long> materiasFrecuencia = casosDetectados.stream()
                .collect(Collectors.groupingBy(ReporteExamenesConsecutivosDto::getMateriaNombre, Collectors.counting()));
        
        materiasFrecuencia.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(entry -> recomendaciones.add(
                        String.format("La materia '%s' presenta la mayor cantidad de casos (%d) - Revisar metodología de evaluación", 
                                entry.getKey(), entry.getValue())
                ));

        recomendaciones.add("Considerar reuniones con padres/tutores de alumnos afectados");
        recomendaciones.add("Evaluar estrategias de recuperación antes del cierre de etapa");

        return recomendaciones;
    }
}