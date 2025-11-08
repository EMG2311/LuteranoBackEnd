package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.reporteHistorialAlumno.ReporteHistorialAlumnoDto;
import com.grup14.luterano.entities.*;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.response.reporteHistorialAlumno.ReporteHistorialAlumnoResponse;
import com.grup14.luterano.service.ReporteHistorialAlumnoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteHistorialAlumnoServiceImpl implements ReporteHistorialAlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final HistorialCursoRepository historialCursoRepository;
    private final CalificacionRepository calificacionRepository;

    @Override
    @Transactional(readOnly = true)
    public ReporteHistorialAlumnoResponse generarHistorialCompleto(Long alumnoId) {
        try {
            // Verificar que existe el alumno
            Alumno alumno = alumnoRepository.findById(alumnoId)
                    .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

            // Obtener todo el historial curso del alumno
            List<HistorialCurso> historialCursos = historialCursoRepository.findHistorialCompletoByAlumnoId(alumnoId);

            if (historialCursos.isEmpty()) {
                return construirRespuestaVacia(alumno, "El alumno no tiene historial académico registrado");
            }

            // Obtener todas las calificaciones del alumno
            List<Calificacion> todasLasCalificaciones = calificacionRepository.findHistorialCompletoByAlumnoId(alumnoId);

            // Procesar datos
            ReporteHistorialAlumnoDto reporte = procesarHistorialCompleto(alumno, historialCursos, todasLasCalificaciones);

            return ReporteHistorialAlumnoResponse.builder()
                    .code(1)
                    .mensaje("Historial académico generado exitosamente")
                    .historial(reporte)
                    .build();

        } catch (Exception e) {
            log.error("Error generando historial completo para alumno {}: {}", alumnoId, e.getMessage());
            return ReporteHistorialAlumnoResponse.builder()
                    .code(-1)
                    .mensaje("Error generando historial: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteHistorialAlumnoResponse generarHistorialPorCiclo(Long alumnoId, Long cicloLectivoId) {
        try {
            // Verificar que existe el alumno
            Alumno alumno = alumnoRepository.findById(alumnoId)
                    .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

            // Obtener calificaciones del ciclo específico
            List<Calificacion> calificaciones = calificacionRepository.findHistorialByAlumnoAndCiclo(alumnoId, cicloLectivoId);

            if (calificaciones.isEmpty()) {
                return construirRespuestaVacia(alumno, "El alumno no tiene registros en el ciclo lectivo especificado");
            }

            // Obtener historial curso del ciclo
            Optional<HistorialCurso> historialOpt = alumno.getHistorialCursos().stream()
                    .filter(hc -> hc.getCicloLectivo().getId().equals(cicloLectivoId))
                    .findFirst();

            if (historialOpt.isEmpty()) {
                return construirRespuestaVacia(alumno, "El alumno no cursó en el ciclo lectivo especificado");
            }

            List<HistorialCurso> historialCiclo = List.of(historialOpt.get());
            ReporteHistorialAlumnoDto reporte = procesarHistorialCompleto(alumno, historialCiclo, calificaciones);

            return ReporteHistorialAlumnoResponse.builder()
                    .code(1)
                    .mensaje("Historial del ciclo generado exitosamente")
                    .historial(reporte)
                    .build();

        } catch (Exception e) {
            log.error("Error generando historial por ciclo para alumno {} y ciclo {}: {}", alumnoId, cicloLectivoId, e.getMessage());
            return ReporteHistorialAlumnoResponse.builder()
                    .code(-1)
                    .mensaje("Error generando historial: " + e.getMessage())
                    .build();
        }
    }

    private ReporteHistorialAlumnoDto procesarHistorialCompleto(Alumno alumno, 
                                                               List<HistorialCurso> historialCursos, 
                                                               List<Calificacion> calificaciones) {
        
        // Agrupar calificaciones por ciclo lectivo
        Map<Long, List<Calificacion>> calificacionesPorCiclo = calificaciones.stream()
                .collect(Collectors.groupingBy(c -> c.getHistorialMateria().getHistorialCurso().getCicloLectivo().getId()));

        // Procesar cada ciclo
        List<ReporteHistorialAlumnoDto.HistorialCicloDto> historialCiclos = new ArrayList<>();
        
        for (HistorialCurso hc : historialCursos) {
            Long cicloId = hc.getCicloLectivo().getId();
            List<Calificacion> calificacionesCiclo = calificacionesPorCiclo.getOrDefault(cicloId, new ArrayList<>());
            
            ReporteHistorialAlumnoDto.HistorialCicloDto cicloDto = procesarCiclo(hc, calificacionesCiclo);
            historialCiclos.add(cicloDto);
        }

        // Calcular resumen estadístico
        ReporteHistorialAlumnoDto.ResumenHistorialDto resumen = calcularResumenHistorial(alumno, historialCiclos);

        return ReporteHistorialAlumnoDto.builder()
                .alumnoId(alumno.getId())
                .dni(alumno.getDni())
                .apellido(alumno.getApellido())
                .nombre(alumno.getNombre())
                .genero(alumno.getGenero() != null ? alumno.getGenero().name() : null)
                .estadoActual(alumno.getEstado() != null ? alumno.getEstado().name() : null)
                .historialPorCiclos(historialCiclos)
                .resumen(resumen)
                .build();
    }

    private ReporteHistorialAlumnoDto.HistorialCicloDto procesarCiclo(HistorialCurso historialCurso, 
                                                                     List<Calificacion> calificaciones) {
        
        CicloLectivo ciclo = historialCurso.getCicloLectivo();
        Curso curso = historialCurso.getCurso();
        
        // Agrupar calificaciones por materia
        Map<Long, List<Calificacion>> calificacionesPorMateria = calificaciones.stream()
                .collect(Collectors.groupingBy(c -> c.getHistorialMateria().getMateriaCurso().getMateria().getId()));

        List<ReporteHistorialAlumnoDto.MateriaNotasDto> materias = new ArrayList<>();
        
        for (Map.Entry<Long, List<Calificacion>> entry : calificacionesPorMateria.entrySet()) {
            List<Calificacion> calificacionesMateria = entry.getValue();
            if (!calificacionesMateria.isEmpty()) {
                ReporteHistorialAlumnoDto.MateriaNotasDto materiaDto = procesarMateria(calificacionesMateria);
                materias.add(materiaDto);
            }
        }

        // Calcular estadísticas del ciclo
        double promedioGeneral = materias.stream()
                .filter(m -> m.getPromedioGeneral() != null)
                .mapToDouble(ReporteHistorialAlumnoDto.MateriaNotasDto::getPromedioGeneral)
                .average()
                .orElse(0.0);

        int materiasAprobadas = (int) materias.stream()
                .filter(m -> "APROBADA".equals(m.getEstadoMateria()))
                .count();

        int materiasDesaprobadas = (int) materias.stream()
                .filter(m -> "DESAPROBADA".equals(m.getEstadoMateria()))
                .count();

        // Determinar estado del ciclo
        String estadoCiclo = historialCurso.getFechaHasta() != null ? "CERRADO" : "ACTIVO";

        return ReporteHistorialAlumnoDto.HistorialCicloDto.builder()
                .cicloAnio(ciclo.getFechaDesde().getYear())
                .cicloNombre(ciclo.getNombre())
                .cursoAnio(curso.getAnio())
                .cursoDivision(curso.getDivision() != null ? curso.getDivision().name() : null)
                .cursoNivel(curso.getNivel() != null ? curso.getNivel().name() : null)
                .estadoCiclo(estadoCiclo)
                .materias(materias)
                .promedioGeneral(Math.round(promedioGeneral * 100.0) / 100.0)
                .materiasAprobadas(materiasAprobadas)
                .materiasDesaprobadas(materiasDesaprobadas)
                .materiasTotal(materias.size())
                .build();
    }

    private ReporteHistorialAlumnoDto.MateriaNotasDto procesarMateria(List<Calificacion> calificaciones) {
        if (calificaciones.isEmpty()) return null;

        Calificacion primera = calificaciones.get(0);
        Materia materia = primera.getHistorialMateria().getMateriaCurso().getMateria();

        // Agrupar por etapa
        Map<Integer, List<Calificacion>> calificacionesPorEtapa = calificaciones.stream()
                .collect(Collectors.groupingBy(Calificacion::getEtapa));

        // Calcular promedios por etapa
        Double promedioEtapa1 = calcularPromedioEtapa(calificacionesPorEtapa.get(1));
        Double promedioEtapa2 = calcularPromedioEtapa(calificacionesPorEtapa.get(2));
        
        // Calcular promedio general
        Double promedioGeneral = null;
        if (promedioEtapa1 != null && promedioEtapa2 != null) {
            promedioGeneral = (promedioEtapa1 + promedioEtapa2) / 2.0;
        } else if (promedioEtapa1 != null) {
            promedioGeneral = promedioEtapa1;
        } else if (promedioEtapa2 != null) {
            promedioGeneral = promedioEtapa2;
        }

        // Determinar estado de la materia
        String estadoMateria = determinarEstadoMateria(promedioEtapa1, promedioEtapa2);

        // Crear DTOs de calificaciones
        List<ReporteHistorialAlumnoDto.CalificacionDto> calificacionesDtos = calificaciones.stream()
                .map(this::convertirCalificacion)
                .sorted(Comparator.comparing(ReporteHistorialAlumnoDto.CalificacionDto::getEtapa)
                        .thenComparing(ReporteHistorialAlumnoDto.CalificacionDto::getNumeroNota))
                .collect(Collectors.toList());

        return ReporteHistorialAlumnoDto.MateriaNotasDto.builder()
                .materiaId(materia.getId())
                .materiaNombre(materia.getNombre())
                .calificaciones(calificacionesDtos)
                .promedioEtapa1(promedioEtapa1 != null ? Math.round(promedioEtapa1 * 100.0) / 100.0 : null)
                .promedioEtapa2(promedioEtapa2 != null ? Math.round(promedioEtapa2 * 100.0) / 100.0 : null)
                .promedioGeneral(promedioGeneral != null ? Math.round(promedioGeneral * 100.0) / 100.0 : null)
                .notaFinal(calcularNotaFinal(promedioEtapa1, promedioEtapa2))
                .estadoMateria(estadoMateria)
                .build();
    }

    private Double calcularPromedioEtapa(List<Calificacion> calificacionesEtapa) {
        if (calificacionesEtapa == null || calificacionesEtapa.isEmpty()) return null;
        
        return calificacionesEtapa.stream()
                .filter(c -> c.getNota() != null)
                .mapToInt(Calificacion::getNota)
                .average()
                .orElse(0.0);
    }

    private String determinarEstadoMateria(Double promedioEtapa1, Double promedioEtapa2) {
        // Si ambas etapas están aprobadas (>=6), la materia está aprobada
        if (promedioEtapa1 != null && promedioEtapa1 >= 6.0 && 
            promedioEtapa2 != null && promedioEtapa2 >= 6.0) {
            return "APROBADA";
        }
        
        // Si alguna etapa está desaprobada y es el final del año, está desaprobada
        if ((promedioEtapa1 != null && promedioEtapa1 < 6.0) || 
            (promedioEtapa2 != null && promedioEtapa2 < 6.0)) {
            return "DESAPROBADA";
        }
        
        // Si no tiene notas suficientes o está en progreso
        return "EN_CURSO";
    }

    private Integer calcularNotaFinal(Double promedioEtapa1, Double promedioEtapa2) {
        if (promedioEtapa1 != null && promedioEtapa1 >= 6.0 && 
            promedioEtapa2 != null && promedioEtapa2 >= 6.0) {
            return (int) Math.round((promedioEtapa1 + promedioEtapa2) / 2.0);
        }
        return null;
    }

    private ReporteHistorialAlumnoDto.CalificacionDto convertirCalificacion(Calificacion calificacion) {
        return ReporteHistorialAlumnoDto.CalificacionDto.builder()
                .etapa(calificacion.getEtapa())
                .numeroNota(calificacion.getNumeroNota())
                .nota(calificacion.getNota())
                .fecha(calificacion.getFecha() != null ? 
                       calificacion.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null)
                .build();
    }

    private ReporteHistorialAlumnoDto.ResumenHistorialDto calcularResumenHistorial(Alumno alumno, 
                                                                                  List<ReporteHistorialAlumnoDto.HistorialCicloDto> ciclos) {
        
        int totalCiclos = ciclos.size();
        int totalAprobadas = ciclos.stream().mapToInt(ReporteHistorialAlumnoDto.HistorialCicloDto::getMateriasAprobadas).sum();
        int totalDesaprobadas = ciclos.stream().mapToInt(ReporteHistorialAlumnoDto.HistorialCicloDto::getMateriasDesaprobadas).sum();
        
        double promedioHistorico = ciclos.stream()
                .filter(c -> c.getPromedioGeneral() != null && c.getPromedioGeneral() > 0)
                .mapToDouble(ReporteHistorialAlumnoDto.HistorialCicloDto::getPromedioGeneral)
                .average()
                .orElse(0.0);

        // Analizar tendencia académica
        String tendencia = analizarTendencia(ciclos);

        // Generar logros y áreas de mejora
        List<String> logros = generarLogrosDestacados(ciclos, promedioHistorico);
        List<String> areasAMejorar = generarAreasAMejorar(ciclos);

        return ReporteHistorialAlumnoDto.ResumenHistorialDto.builder()
                .totalCiclosLectivos(totalCiclos)
                .totalMateriasAprobadas(totalAprobadas)
                .totalMateriasDesaprobadas(totalDesaprobadas)
                .promedioGeneralHistorico(Math.round(promedioHistorico * 100.0) / 100.0)
                .cantidadRepeticiones(alumno.getCantidadRepeticiones() != null ? alumno.getCantidadRepeticiones() : 0)
                .tendenciaAcademica(tendencia)
                .logrosDestacados(logros)
                .areasAMejorar(areasAMejorar)
                .build();
    }

    private String analizarTendencia(List<ReporteHistorialAlumnoDto.HistorialCicloDto> ciclos) {
        if (ciclos.size() < 2) return "ESTABLE";
        
        // Comparar los últimos 2 ciclos
        var ultimoCiclo = ciclos.get(ciclos.size() - 1);
        var penultimoCiclo = ciclos.get(ciclos.size() - 2);
        
        if (ultimoCiclo.getPromedioGeneral() == null || penultimoCiclo.getPromedioGeneral() == null) {
            return "ESTABLE";
        }
        
        double diferencia = ultimoCiclo.getPromedioGeneral() - penultimoCiclo.getPromedioGeneral();
        
        if (diferencia > 0.5) return "MEJORANDO";
        if (diferencia < -0.5) return "EMPEORANDO";
        return "ESTABLE";
    }

    private List<String> generarLogrosDestacados(List<ReporteHistorialAlumnoDto.HistorialCicloDto> ciclos, double promedioHistorico) {
        List<String> logros = new ArrayList<>();
        
        if (promedioHistorico >= 8.0) {
            logros.add("Excelente rendimiento académico general (promedio " + String.format("%.2f", promedioHistorico) + ")");
        }
        
        // Buscar ciclos sin materias desaprobadas
        long ciclosSinDesaprobadas = ciclos.stream()
                .filter(c -> c.getMateriasDesaprobadas() == 0)
                .count();
        
        if (ciclosSinDesaprobadas > 0) {
            logros.add(ciclosSinDesaprobadas + " ciclo(s) sin materias desaprobadas");
        }
        
        return logros;
    }

    private List<String> generarAreasAMejorar(List<ReporteHistorialAlumnoDto.HistorialCicloDto> ciclos) {
        List<String> areas = new ArrayList<>();
        
        // Buscar materias frecuentemente desaprobadas
        Map<String, Integer> materiasDesaprobadas = new HashMap<>();
        
        for (var ciclo : ciclos) {
            for (var materia : ciclo.getMaterias()) {
                if ("DESAPROBADA".equals(materia.getEstadoMateria())) {
                    materiasDesaprobadas.merge(materia.getMateriaNombre(), 1, Integer::sum);
                }
            }
        }
        
        materiasDesaprobadas.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .forEach(entry -> areas.add("Reforzar " + entry.getKey() + " (desaprobada " + entry.getValue() + " veces)"));
        
        return areas;
    }

    private ReporteHistorialAlumnoResponse construirRespuestaVacia(Alumno alumno, String mensaje) {
        ReporteHistorialAlumnoDto reporteVacio = ReporteHistorialAlumnoDto.builder()
                .alumnoId(alumno.getId())
                .dni(alumno.getDni())
                .apellido(alumno.getApellido())
                .nombre(alumno.getNombre())
                .genero(alumno.getGenero() != null ? alumno.getGenero().name() : null)
                .estadoActual(alumno.getEstado() != null ? alumno.getEstado().name() : null)
                .historialPorCiclos(new ArrayList<>())
                .resumen(ReporteHistorialAlumnoDto.ResumenHistorialDto.builder()
                        .totalCiclosLectivos(0)
                        .totalMateriasAprobadas(0)
                        .totalMateriasDesaprobadas(0)
                        .promedioGeneralHistorico(0.0)
                        .cantidadRepeticiones(alumno.getCantidadRepeticiones() != null ? alumno.getCantidadRepeticiones() : 0)
                        .tendenciaAcademica("SIN_DATOS")
                        .logrosDestacados(new ArrayList<>())
                        .areasAMejorar(new ArrayList<>())
                        .build())
                .build();

        return ReporteHistorialAlumnoResponse.builder()
                .code(0)
                .mensaje(mensaje)
                .historial(reporteVacio)
                .build();
    }
}