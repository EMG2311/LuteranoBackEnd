package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.reporteDesempeno.ReporteDesempenoDocenteDto;
import com.grup14.luterano.dto.reporteDesempeno.ReporteDesempenoMateriaDto;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.response.reporteDesempeno.ReporteDesempenoResponse;
import com.grup14.luterano.service.NotaFinalService;
import com.grup14.luterano.service.ReporteDesempenoDocenteService;
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
public class ReporteDesempenoDocenteServiceImpl implements ReporteDesempenoDocenteService {

    private final MateriaCursoRepository materiaCursoRepository;
    private final HistorialCursoRepository historialCursoRepository;
    private final CicloLectivoRepository cicloLectivoRepository;
    private final NotaFinalService notaFinalService;

    @Override
    public ReporteDesempenoResponse generarReporteDesempeno(int cicloLectivoAnio) {
        log.info("Generando reporte de desempeño docente para año: {}", cicloLectivoAnio);
        
        // Obtener ciclo lectivo
        var cicloLectivo = cicloLectivoRepository.findByAnio(cicloLectivoAnio)
                .orElseThrow(() -> new RuntimeException("No existe ciclo lectivo para el año: " + cicloLectivoAnio));

        // Obtener todas las materias-curso del ciclo
        List<Object[]> materiasConDocente = materiaCursoRepository.findMateriasConDocentePorCiclo(cicloLectivoAnio);
        
        Map<Long, List<ReporteDesempenoDocenteDto>> resultadosPorMateria = new HashMap<>();
        
        for (Object[] row : materiasConDocente) {
            ReporteDesempenoDocenteDto resultado = procesarMateriaCurso(row, cicloLectivoAnio);
            
            Long materiaId = resultado.getMateriaId();
            resultadosPorMateria.computeIfAbsent(materiaId, k -> new ArrayList<>()).add(resultado);
        }
        
        // Construir resultado por materia
        List<ReporteDesempenoMateriaDto> reportesMaterias = new ArrayList<>();
        
        for (Map.Entry<Long, List<ReporteDesempenoDocenteDto>> entry : resultadosPorMateria.entrySet()) {
            ReporteDesempenoMateriaDto reporteMateria = construirReporteMateria(entry.getValue());
            reportesMaterias.add(reporteMateria);
        }
        
        // Ordenar por nombre de materia
        reportesMaterias.sort(Comparator.comparing(ReporteDesempenoMateriaDto::getNombreMateria));
        
        // Calcular estadísticas globales
        return ReporteDesempenoResponse.builder()
                .code(0)
                .mensaje("Reporte generado exitosamente")
                .cicloLectivoAnio(cicloLectivoAnio)
                .nombreCicloLectivo(cicloLectivo.getNombre())
                .totalMaterias(reportesMaterias.size())
                .totalDocentes(calcularTotalDocentes(reportesMaterias))
                .totalAlumnos(calcularTotalAlumnos(reportesMaterias))
                .totalCursos(calcularTotalCursos(reportesMaterias))
                .resultadosPorMateria(reportesMaterias)
                .resumenEjecutivo(generarResumenEjecutivo(reportesMaterias))
                .hallazgosImportantes(generarHallazgos(reportesMaterias))
                .recomendaciones(generarRecomendaciones(reportesMaterias))
                .build();
    }

    @Override
    public ReporteDesempenoResponse generarReportePorMateria(int cicloLectivoAnio, Long materiaId) {
        log.info("Generando reporte de desempeño para materia {} en año: {}", materiaId, cicloLectivoAnio);
        
        List<Object[]> materiasConDocente = materiaCursoRepository.findMateriasConDocentePorCicloYMateria(cicloLectivoAnio, materiaId);
        
        List<ReporteDesempenoDocenteDto> resultados = new ArrayList<>();
        for (Object[] row : materiasConDocente) {
            resultados.add(procesarMateriaCurso(row, cicloLectivoAnio));
        }
        
        if (resultados.isEmpty()) {
            return ReporteDesempenoResponse.builder()
                    .code(-1)
                    .mensaje("No se encontraron datos para la materia en el año especificado")
                    .build();
        }
        
        ReporteDesempenoMateriaDto reporteMateria = construirReporteMateria(resultados);
        
        var cicloLectivo = cicloLectivoRepository.findByAnio(cicloLectivoAnio)
                .orElseThrow(() -> new RuntimeException("No existe ciclo lectivo para el año: " + cicloLectivoAnio));
        
        return ReporteDesempenoResponse.builder()
                .code(0)
                .mensaje("Reporte por materia generado exitosamente")
                .cicloLectivoAnio(cicloLectivoAnio)
                .nombreCicloLectivo(cicloLectivo.getNombre())
                .totalMaterias(1)
                .totalDocentes(reporteMateria.getTotalDocentes())
                .totalAlumnos(reporteMateria.getTotalAlumnos())
                .totalCursos(reporteMateria.getTotalCursos())
                .resultadosPorMateria(List.of(reporteMateria))
                .resumenEjecutivo("Análisis específico de " + reporteMateria.getNombreMateria())
                .build();
    }

    @Override
    public ReporteDesempenoResponse generarReportePorDocente(int cicloLectivoAnio, Long docenteId) {
        log.info("Generando reporte de desempeño para docente {} en año: {}", docenteId, cicloLectivoAnio);
        
        List<Object[]> materiasConDocente = materiaCursoRepository.findMateriasConDocentePorCicloYDocente(cicloLectivoAnio, docenteId);
        
        Map<Long, List<ReporteDesempenoDocenteDto>> resultadosPorMateria = new HashMap<>();
        
        for (Object[] row : materiasConDocente) {
            ReporteDesempenoDocenteDto resultado = procesarMateriaCurso(row, cicloLectivoAnio);
            
            Long materiaId = resultado.getMateriaId();
            resultadosPorMateria.computeIfAbsent(materiaId, k -> new ArrayList<>()).add(resultado);
        }
        
        if (resultadosPorMateria.isEmpty()) {
            return ReporteDesempenoResponse.builder()
                    .code(-1)
                    .mensaje("No se encontraron datos para el docente en el año especificado")
                    .build();
        }
        
        List<ReporteDesempenoMateriaDto> reportesMaterias = new ArrayList<>();
        for (List<ReporteDesempenoDocenteDto> resultados : resultadosPorMateria.values()) {
            reportesMaterias.add(construirReporteMateria(resultados));
        }
        
        var cicloLectivo = cicloLectivoRepository.findByAnio(cicloLectivoAnio)
                .orElseThrow(() -> new RuntimeException("No existe ciclo lectivo para el año: " + cicloLectivoAnio));
        
        String nombreDocente = reportesMaterias.get(0).getResultadosPorDocente().get(0).getNombreCompletoDocente();
        
        return ReporteDesempenoResponse.builder()
                .code(0)
                .mensaje("Reporte por docente generado exitosamente")
                .cicloLectivoAnio(cicloLectivoAnio)
                .nombreCicloLectivo(cicloLectivo.getNombre())
                .totalMaterias(reportesMaterias.size())
                .totalDocentes(1)
                .totalAlumnos(calcularTotalAlumnos(reportesMaterias))
                .totalCursos(calcularTotalCursos(reportesMaterias))
                .resultadosPorMateria(reportesMaterias)
                .resumenEjecutivo("Análisis específico del docente " + nombreDocente)
                .build();
    }

    private ReporteDesempenoDocenteDto procesarMateriaCurso(Object[] row, int cicloLectivoAnio) {
        // Extraer datos de la query (ajustar según la query real)
        Long materiaCursoId = (Long) row[0];
        Long materiaId = (Long) row[1];
        String nombreMateria = (String) row[2];
        Long docenteId = (Long) row[3];
        String apellidoDocente = (String) row[4];
        String nombreDocente = (String) row[5];
        Long cursoId = (Long) row[6];
        Integer anio = (Integer) row[7];
        String nivel = (String) row[8];
        String division = (String) row[9];
        
        // Obtener alumnos que cursaron esa materia en ese año
        List<Long> alumnosIds = historialCursoRepository.findAlumnosIdsPorCursoYCiclo(cursoId, cicloLectivoAnio);
        
        int totalAlumnos = alumnosIds.size();
        int aprobados = 0;
        int desaprobados = 0;
        
        BigDecimal sumaNotas = BigDecimal.ZERO;
        BigDecimal notaMinima = BigDecimal.valueOf(10);
        BigDecimal notaMaxima = BigDecimal.ZERO;
        
        for (Long alumnoId : alumnosIds) {
            Integer notaFinal = notaFinalService.calcularNotaFinal(alumnoId, materiaId, cicloLectivoAnio);
            
            if (notaFinal != null) {
                BigDecimal nota = BigDecimal.valueOf(notaFinal);
                sumaNotas = sumaNotas.add(nota);
                
                if (notaFinal >= 6) {
                    aprobados++;
                } else {
                    desaprobados++;
                }
                
                if (nota.compareTo(notaMinima) < 0) notaMinima = nota;
                if (nota.compareTo(notaMaxima) > 0) notaMaxima = nota;
            }
        }
        
        BigDecimal porcentajeAprobacion = totalAlumnos > 0 
            ? BigDecimal.valueOf(aprobados * 100.0 / totalAlumnos).setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
            
        BigDecimal porcentajeReprobacion = totalAlumnos > 0
            ? BigDecimal.valueOf(desaprobados * 100.0 / totalAlumnos).setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
            
        BigDecimal promedioGeneral = (aprobados + desaprobados) > 0
            ? sumaNotas.divide(BigDecimal.valueOf(aprobados + desaprobados), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        
        String estadoAnalisis = determinarEstadoAnalisis(porcentajeAprobacion);
        
        return ReporteDesempenoDocenteDto.builder()
                .docenteId(docenteId)
                .apellidoDocente(apellidoDocente)
                .nombreDocente(nombreDocente)
                .nombreCompletoDocente(apellidoDocente + ", " + nombreDocente)
                .materiaId(materiaId)
                .nombreMateria(nombreMateria)
                .cursoId(cursoId)
                .anio(anio)
                .nivel(nivel)
                .division(division)
                .cursoCompleto(anio + "° " + division + " (" + nivel + ")")
                .totalAlumnos(totalAlumnos)
                .alumnosAprobados(aprobados)
                .alumnosDesaprobados(desaprobados)
                .porcentajeAprobacion(porcentajeAprobacion)
                .porcentajeReprobacion(porcentajeReprobacion)
                .promedioGeneral(promedioGeneral)
                .notaMinima(notaMinima.equals(BigDecimal.valueOf(10)) ? BigDecimal.ZERO : notaMinima)
                .notaMaxima(notaMaxima)
                .cicloLectivoAnio(cicloLectivoAnio)
                .estadoAnalisis(estadoAnalisis)
                .build();
    }
    
    private ReporteDesempenoMateriaDto construirReporteMateria(List<ReporteDesempenoDocenteDto> resultados) {
        if (resultados.isEmpty()) return null;
        
        ReporteDesempenoDocenteDto primero = resultados.get(0);
        
        int totalAlumnos = resultados.stream().mapToInt(ReporteDesempenoDocenteDto::getTotalAlumnos).sum();
        int totalAprobados = resultados.stream().mapToInt(ReporteDesempenoDocenteDto::getAlumnosAprobados).sum();
        
        BigDecimal promedioAprobacion = totalAlumnos > 0
            ? BigDecimal.valueOf(totalAprobados * 100.0 / totalAlumnos).setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
            
        BigDecimal promedioReprobacion = BigDecimal.valueOf(100).subtract(promedioAprobacion);
        
        BigDecimal promedioGeneral = resultados.stream()
            .map(ReporteDesempenoDocenteDto::getPromedioGeneral)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(resultados.size()), 2, RoundingMode.HALF_UP);
        
        // Encontrar mejor y peor docente
        ReporteDesempenoDocenteDto mejorDocente = resultados.stream()
            .max(Comparator.comparing(ReporteDesempenoDocenteDto::getPorcentajeAprobacion))
            .orElse(null);
            
        ReporteDesempenoDocenteDto peorDocente = resultados.stream()
            .min(Comparator.comparing(ReporteDesempenoDocenteDto::getPorcentajeAprobacion))
            .orElse(null);
        
        BigDecimal rangoAprobacion = mejorDocente != null && peorDocente != null
            ? mejorDocente.getPorcentajeAprobacion().subtract(peorDocente.getPorcentajeAprobacion())
            : BigDecimal.ZERO;
        
        return ReporteDesempenoMateriaDto.builder()
                .materiaId(primero.getMateriaId())
                .nombreMateria(primero.getNombreMateria())
                .totalDocentes(resultados.size())
                .totalAlumnos(totalAlumnos)
                .totalCursos((int) resultados.stream().map(ReporteDesempenoDocenteDto::getCursoId).distinct().count())
                .promedioAprobacionMateria(promedioAprobacion)
                .promedioReprobacionMateria(promedioReprobacion)
                .promedioGeneralMateria(promedioGeneral)
                .resultadosPorDocente(resultados.stream()
                    .sorted(Comparator.comparing(ReporteDesempenoDocenteDto::getPorcentajeAprobacion).reversed())
                    .collect(Collectors.toList()))
                .mejorDocente(mejorDocente)
                .peorDocente(peorDocente)
                .rangoAprobacion(rangoAprobacion)
                .build();
    }
    
    private String determinarEstadoAnalisis(BigDecimal porcentajeAprobacion) {
        if (porcentajeAprobacion.compareTo(BigDecimal.valueOf(90)) >= 0) return "EXCELENTE";
        if (porcentajeAprobacion.compareTo(BigDecimal.valueOf(75)) >= 0) return "BUENO";
        if (porcentajeAprobacion.compareTo(BigDecimal.valueOf(60)) >= 0) return "REGULAR";
        return "PREOCUPANTE";
    }
    
    private Integer calcularTotalDocentes(List<ReporteDesempenoMateriaDto> reportes) {
        return reportes.stream()
            .mapToInt(ReporteDesempenoMateriaDto::getTotalDocentes)
            .sum();
    }
    
    private Integer calcularTotalAlumnos(List<ReporteDesempenoMateriaDto> reportes) {
        return reportes.stream()
            .mapToInt(ReporteDesempenoMateriaDto::getTotalAlumnos)
            .sum();
    }
    
    private Integer calcularTotalCursos(List<ReporteDesempenoMateriaDto> reportes) {
        return reportes.stream()
            .mapToInt(ReporteDesempenoMateriaDto::getTotalCursos)
            .sum();
    }
    
    private String generarResumenEjecutivo(List<ReporteDesempenoMateriaDto> reportes) {
        if (reportes.isEmpty()) return "Sin datos para analizar";
        
        BigDecimal promedioGeneral = reportes.stream()
            .map(ReporteDesempenoMateriaDto::getPromedioAprobacionMateria)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(reportes.size()), 2, RoundingMode.HALF_UP);
            
        return String.format("Promedio institucional de aprobación: %.2f%%. " +
            "Se analizaron %d materias con %d docentes en total.",
            promedioGeneral, reportes.size(), calcularTotalDocentes(reportes));
    }
    
    private List<String> generarHallazgos(List<ReporteDesempenoMateriaDto> reportes) {
        List<String> hallazgos = new ArrayList<>();
        
        // Materia con mayor variación
        ReporteDesempenoMateriaDto mayorVariacion = reportes.stream()
            .max(Comparator.comparing(ReporteDesempenoMateriaDto::getRangoAprobacion))
            .orElse(null);
            
        if (mayorVariacion != null && mayorVariacion.getRangoAprobacion().compareTo(BigDecimal.valueOf(30)) > 0) {
            hallazgos.add(String.format("La materia %s presenta la mayor variación entre docentes (%.2f%% de diferencia)",
                mayorVariacion.getNombreMateria(), mayorVariacion.getRangoAprobacion()));
        }
        
        // Materias con bajo rendimiento
        List<ReporteDesempenoMateriaDto> bajoRendimiento = reportes.stream()
            .filter(r -> r.getPromedioAprobacionMateria().compareTo(BigDecimal.valueOf(60)) < 0)
            .collect(Collectors.toList());
            
        if (!bajoRendimiento.isEmpty()) {
            hallazgos.add(String.format("%d materia(s) tienen tasa de aprobación menor al 60%%",
                bajoRendimiento.size()));
        }
        
        return hallazgos;
    }
    
    private List<String> generarRecomendaciones(List<ReporteDesempenoMateriaDto> reportes) {
        List<String> recomendaciones = new ArrayList<>();
        
        recomendaciones.add("Realizar reuniones de intercambio de metodologías entre docentes de la misma materia");
        recomendaciones.add("Implementar capacitaciones para docentes con tasas de aprobación menores al 70%");
        recomendaciones.add("Analizar factores externos que puedan estar afectando el rendimiento estudiantil");
        
        return recomendaciones;
    }
}