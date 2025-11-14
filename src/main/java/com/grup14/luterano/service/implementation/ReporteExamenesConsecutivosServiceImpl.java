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

            // Detectar todas las secuencias consecutivas desaprobadas
            List<List<Calificacion>> secuenciasConsecutivas = detectarSecuenciasConsecutivas(calificaciones);
            
            // Crear casos individuales para cada secuencia
            List<ReporteExamenesConsecutivosDto> casosDelAlumnoMateria = new ArrayList<>();
            for (List<Calificacion> secuencia : secuenciasConsecutivas) {
                if (secuencia.size() >= 2) {
                    ReporteExamenesConsecutivosDto caso = construirCasoSecuencia(secuencia);
                    if (caso != null) {
                        casosDelAlumnoMateria.add(caso);
                    }
                }
            }
            
            // Aplicar scoring inteligente por materia a todos los casos
            if (!casosDelAlumnoMateria.isEmpty()) {
                String estadoRiesgoMateria = determinarEstadoRiesgoPorMateria(casosDelAlumnoMateria);
                
                // Asignar el mismo nivel de riesgo a todos los casos de esta materia
                for (ReporteExamenesConsecutivosDto caso : casosDelAlumnoMateria) {
                    caso.setEstadoRiesgo(estadoRiesgoMateria);
                    casosDetectados.add(caso);
                }
            }
        }

        return casosDetectados;
    }

    private List<List<Calificacion>> detectarSecuenciasConsecutivas(List<Calificacion> calificaciones) {
        List<List<Calificacion>> secuencias = new ArrayList<>();
        List<Calificacion> secuenciaActual = new ArrayList<>();

        for (int i = 0; i < calificaciones.size(); i++) {
            Calificacion actual = calificaciones.get(i);
            
            if (esDesaprobada(actual.getNota())) {
                // Si es la primera calificación desaprobada o es consecutiva a la anterior
                if (secuenciaActual.isEmpty() || 
                    sonConsecutivas(secuenciaActual.get(secuenciaActual.size() - 1), actual)) {
                    secuenciaActual.add(actual);
                } else {
                    // No es consecutiva, terminar secuencia actual y empezar nueva
                    if (secuenciaActual.size() >= 2) {
                        secuencias.add(new ArrayList<>(secuenciaActual));
                    }
                    secuenciaActual.clear();
                    secuenciaActual.add(actual);
                }
            } else {
                // Calificación aprobada, terminar secuencia actual
                if (secuenciaActual.size() >= 2) {
                    secuencias.add(new ArrayList<>(secuenciaActual));
                }
                secuenciaActual.clear();
            }
        }

        // Agregar la última secuencia si existe
        if (secuenciaActual.size() >= 2) {
            secuencias.add(secuenciaActual);
        }

        return secuencias;
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

    private ReporteExamenesConsecutivosDto construirCasoSecuencia(List<Calificacion> secuencia) {
        try {
            if (secuencia.isEmpty()) return null;

            Calificacion primera = secuencia.get(0);
            Calificacion ultima = secuencia.get(secuencia.size() - 1);
            
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

            // Crear descripción detallada de la secuencia
            String descripcion;
            int cantidadConsecutivas = secuencia.size();
            
            if (cantidadConsecutivas == 2) {
                descripcion = String.format("Etapa %d: Examen %d (nota: %d) y Examen %d (nota: %d)",
                    primera.getEtapa(),
                    primera.getNumeroNota(), primera.getNota(),
                    ultima.getNumeroNota(), ultima.getNota());
            } else {
                // Para secuencias de 3+, mostrar todos los exámenes
                StringBuilder desc = new StringBuilder();
                desc.append(String.format("Etapa %d: ", primera.getEtapa()));
                
                for (int i = 0; i < secuencia.size(); i++) {
                    Calificacion cal = secuencia.get(i);
                    if (i > 0) desc.append(", ");
                    desc.append(String.format("Examen %d (nota: %d)", cal.getNumeroNota(), cal.getNota()));
                }
                desc.append(String.format(" - %d consecutivos", cantidadConsecutivas));
                descripcion = desc.toString();
            }

            String estadoRiesgo = "MEDIO"; // Valor temporal, será reemplazado por el scoring de materia
            
            // Crear secuencia de notas para debugging/análisis
            String notasSecuencia = secuencia.stream()
                    .map(cal -> String.valueOf(cal.getNota()))
                    .collect(java.util.stream.Collectors.joining(","));

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
                        .segundaNota(ultima.getNota())
                        .etapaSegundaNota(ultima.getEtapa())
                        .numeroSegundaNota(ultima.getNumeroNota())
                        .descripcionConsecutivo(descripcion)
                        .estadoRiesgo(estadoRiesgo)
                        .cantidadConsecutivas(cantidadConsecutivas)
                        .notasSecuencia(notasSecuencia)
                        .docenteId(docenteId)
                        .docenteNombre(docenteNombre)
                        .docenteApellido(docenteApellido)
                        .docenteNombreCompleto(docenteNombreCompleto)
                        .build();

        } catch (Exception e) {
            log.error("Error construyendo caso de secuencia", e);
            return null;
        }
    }

    private String determinarEstadoRiesgoPorCantidad(int cantidadConsecutivas) {
        if (cantidadConsecutivas >= 5) {
            return "EMERGENCIA";     // 5 o más exámenes consecutivos
        } else if (cantidadConsecutivas >= 4) {
            return "CRÍTICO";        // 4 exámenes consecutivos
        } else if (cantidadConsecutivas >= 3) {
            return "ALTO";           // 3 exámenes consecutivos
        } else {
            return "MEDIO";          // 2 exámenes consecutivos
        }
    }

    private String determinarEstadoRiesgoPorMateria(List<ReporteExamenesConsecutivosDto> casosDeMateria) {
        if (casosDeMateria.isEmpty()) {
            return "MEDIO";
        }

        int totalSecuencias = casosDeMateria.size();
        int maxConsecutivas = casosDeMateria.stream()
            .mapToInt(ReporteExamenesConsecutivosDto::getCantidadConsecutivas)
            .max()
            .orElse(0);
        double promedioNotas = calcularPromedioNotasMateria(casosDeMateria);
        boolean hayPatronPersistente = verificarPatronPersistente(casosDeMateria);
        
        int score = 0;
        
        // Factor 1: Secuencia más larga en la materia
        if (maxConsecutivas >= 5) {
            score += 50;
        } else if (maxConsecutivas >= 4) {
            score += 40;
        } else if (maxConsecutivas >= 3) {
            score += 25;
        } else {
            score += 15;
        }
        
        // Factor 2: Múltiples secuencias EN LA MISMA MATERIA (persistencia)
        if (totalSecuencias >= 3) {
            score += 30;      // 3+ secuencias = patrón sistemático
        } else if (totalSecuencias >= 2) {
            score += 20;      // 2 secuencias = persistencia
        }
        
        // Factor 3: Gravedad promedio de notas EN LA MATERIA
        if (promedioNotas <= 2.5) {
            score += 20;      // Notas muy bajas
        } else if (promedioNotas <= 3.5) {
            score += 15;
        } else if (promedioNotas <= 4.5) {
            score += 10;
        } else {
            score += 5;
        }
        
        // Factor 4: Patrón persistente entre etapas
        if (hayPatronPersistente) {
            score += 15;      // Misma dificultad en diferentes etapas
        }
        
        // Clasificación final POR MATERIA
        if (score >= 80) {
            return "EMERGENCIA";       // Falla sistemática en la materia
        } else if (score >= 60) {
            return "CRÍTICO";          // Dificultad grave en la materia  
        } else if (score >= 40) {
            return "ALTO";             // Problema persistente en la materia
        } else {
            return "MEDIO";            // Dificultad puntual en la materia
        }
    }

    private double calcularPromedioNotasMateria(List<ReporteExamenesConsecutivosDto> casos) {
        List<Integer> todasLasNotas = new ArrayList<>();
        
        for (ReporteExamenesConsecutivosDto caso : casos) {
            if (caso.getNotasSecuencia() != null && !caso.getNotasSecuencia().trim().isEmpty()) {
                // Parsear notas de la secuencia: "3,4,2,5" -> [3,4,2,5]
                String[] notasStr = caso.getNotasSecuencia().split(",");
                for (String nota : notasStr) {
                    try {
                        todasLasNotas.add(Integer.parseInt(nota.trim()));
                    } catch (NumberFormatException e) {
                        // Ignorar notas mal formateadas
                    }
                }
            } else {
                // Fallback para compatibilidad
                todasLasNotas.add(caso.getPrimeraNota());
                todasLasNotas.add(caso.getSegundaNota());
            }
        }
        
        return todasLasNotas.stream()
            .mapToDouble(Integer::doubleValue)
            .average()
            .orElse(5.0);
    }

    private boolean verificarPatronPersistente(List<ReporteExamenesConsecutivosDto> casos) {
        // Verifica si hay secuencias en diferentes etapas de la misma materia
        Set<Integer> etapasAfectadas = new HashSet<>();
        for (ReporteExamenesConsecutivosDto caso : casos) {
            etapasAfectadas.add(caso.getEtapaPrimeraNota());
            etapasAfectadas.add(caso.getEtapaSegundaNota());
        }
        return etapasAfectadas.size() >= 2; // Dificultades en múltiples etapas
    }

    private ReporteExamenesConsecutivosResponse construirRespuestaVacia(Integer cicloLectivoAnio, String nombreCiclo) {
        return ReporteExamenesConsecutivosResponse.builder()
                .cicloLectivoAnio(cicloLectivoAnio)
                .nombreCicloLectivo(nombreCiclo)
                .totalAlumnosEnRiesgo(0)
                .totalMateriasAfectadas(0)
                .totalCursosAfectados(0)
                .casosDetectados(new ArrayList<>())
                .casosEmergencia(0)
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
        int casosEmergencia = (int) casosDetectados.stream().filter(c -> "EMERGENCIA".equals(c.getEstadoRiesgo())).count();
        int casosCriticos = (int) casosDetectados.stream().filter(c -> "CRÍTICO".equals(c.getEstadoRiesgo())).count();
        int casosAltos = (int) casosDetectados.stream().filter(c -> "ALTO".equals(c.getEstadoRiesgo())).count();
        int casosMedios = (int) casosDetectados.stream().filter(c -> "MEDIO".equals(c.getEstadoRiesgo())).count();

        // Resumen por materia
        var resumenPorMateria = construirResumenPorMateria(casosDetectados);

        // Recomendaciones
        var recomendaciones = generarRecomendaciones(casosDetectados, casosEmergencia, casosCriticos, casosAltos, casosMedios);

        return ReporteExamenesConsecutivosResponse.builder()
                .cicloLectivoAnio(cicloLectivoAnio)
                .nombreCicloLectivo(nombreCiclo)
                .totalAlumnosEnRiesgo(totalAlumnos)
                .totalMateriasAfectadas(totalMaterias)
                .totalCursosAfectados(totalCursos)
                .casosDetectados(casosDetectados)
                .casosEmergencia(casosEmergencia)
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
                                               int casosEmergencia, int casosCriticos, int casosAltos, int casosMedios) {
        List<String> recomendaciones = new ArrayList<>();

        if (casosDetectados.isEmpty()) {
            recomendaciones.add("Excelente: No se detectaron casos de exámenes consecutivos desaprobados");
            return recomendaciones;
        }

        if (casosEmergencia > 0) {
            recomendaciones.add(String.format("🚨 EMERGENCIA: %d casos con 4+ exámenes consecutivos desaprobados - INTERVENCIÓN INMEDIATA", casosEmergencia));
            recomendaciones.add("Reunión urgente con padres, gabinete psicopedagógico y docentes");
            recomendaciones.add("Considerar cambio de estrategia metodológica o apoyo extracurricular");
        }

        if (casosCriticos > 0) {
            recomendaciones.add(String.format("⚠️ CRÍTICO: %d casos con 3 exámenes consecutivos - Plan de apoyo intensivo", casosCriticos));
            recomendaciones.add("Implementar seguimiento semanal personalizado");
        }

        if (casosAltos > 0) {
            recomendaciones.add(String.format("🔴 ALTO RIESGO: %d casos con 2 exámenes consecutivos - Seguimiento cercano", casosAltos));
            recomendaciones.add("Refuerzo académico y comunicación con padres");
        }

        if (casosMedios > 0) {
            recomendaciones.add(String.format("🟡 PREVENCIÓN: %d casos requieren monitoreo regular", casosMedios));
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