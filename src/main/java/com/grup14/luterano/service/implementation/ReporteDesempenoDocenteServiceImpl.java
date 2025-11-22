package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.reporteDesempeno.ReporteDesempenoDocenteDto;
import com.grup14.luterano.dto.reporteDesempeno.ReporteDesempenoMateriaDto;
import com.grup14.luterano.entities.*;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.response.reporteDesempeno.ReporteDesempenoResponse;
import com.grup14.luterano.service.NotaFinalService;
import com.grup14.luterano.service.ReporteDesempenoDocenteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteDesempenoDocenteServiceImpl implements ReporteDesempenoDocenteService {

    private final CalificacionRepository calificacionRepository;
    private final MateriaCursoRepository materiaCursoRepository;
    private final HistorialCursoRepository historialCursoRepository;
    private final CicloLectivoRepository cicloLectivoRepository;
    private final NotaFinalService notaFinalService; // ya no lo usamos acá, pero lo dejamos inyectado
    private final MesaExamenAlumnoRepository mesaExamenAlumnoRepo;

    @Override
    public ReporteDesempenoResponse generarReporteDesempeno(int cicloLectivoAnio) {
        log.info("Generando reporte de desempeño docente para año: {}", cicloLectivoAnio);

        // Obtener ciclo lectivo
        var cicloLectivo = cicloLectivoRepository.findByAnio(cicloLectivoAnio)
                .orElseThrow(() -> new RuntimeException("No existe ciclo lectivo para el año: " + cicloLectivoAnio));

        // Obtener todas las materias-curso con docente para ese ciclo
        List<Object[]> materiasConDocente = materiaCursoRepository.findMateriasConDocentePorCiclo(cicloLectivoAnio);

        Map<Long, List<ReporteDesempenoDocenteDto>> resultadosPorMateria = new HashMap<>();

        for (Object[] row : materiasConDocente) {
            ReporteDesempenoDocenteDto resultado = procesarMateriaCurso(row, cicloLectivo);

            Long materiaId = resultado.getMateriaId();
            resultadosPorMateria.computeIfAbsent(materiaId, k -> new ArrayList<>()).add(resultado);
        }

        // Construir resultado por materia
        List<ReporteDesempenoMateriaDto> reportesMaterias = new ArrayList<>();

        for (Map.Entry<Long, List<ReporteDesempenoDocenteDto>> entry : resultadosPorMateria.entrySet()) {
            ReporteDesempenoMateriaDto reporteMateria = construirReporteMateria(entry.getValue());
            if (reporteMateria != null) {
                reportesMaterias.add(reporteMateria);
            }
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

        var cicloLectivo = cicloLectivoRepository.findByAnio(cicloLectivoAnio)
                .orElseThrow(() -> new RuntimeException("No existe ciclo lectivo para el año: " + cicloLectivoAnio));

        List<Object[]> materiasConDocente = materiaCursoRepository
                .findMateriasConDocentePorCicloYMateria(cicloLectivoAnio, materiaId);

        List<ReporteDesempenoDocenteDto> resultados = new ArrayList<>();
        for (Object[] row : materiasConDocente) {
            resultados.add(procesarMateriaCurso(row, cicloLectivo));
        }

        if (resultados.isEmpty()) {
            return ReporteDesempenoResponse.builder()
                    .code(-1)
                    .mensaje("No se encontraron datos para la materia en el año especificado")
                    .build();
        }

        ReporteDesempenoMateriaDto reporteMateria = construirReporteMateria(resultados);

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

        var cicloLectivo = cicloLectivoRepository.findByAnio(cicloLectivoAnio)
                .orElseThrow(() -> new RuntimeException("No existe ciclo lectivo para el año: " + cicloLectivoAnio));

        List<Object[]> materiasConDocente = materiaCursoRepository
                .findMateriasConDocentePorCicloYDocente(cicloLectivoAnio, docenteId);

        Map<Long, List<ReporteDesempenoDocenteDto>> resultadosPorMateria = new HashMap<>();

        for (Object[] row : materiasConDocente) {
            ReporteDesempenoDocenteDto resultado = procesarMateriaCurso(row, cicloLectivo);

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
            ReporteDesempenoMateriaDto reporteMateria = construirReporteMateria(resultados);
            if (reporteMateria != null) {
                reportesMaterias.add(reporteMateria);
            }
        }

        String nombreDocente = reportesMaterias.get(0)
                .getResultadosPorDocente().get(0)
                .getNombreCompletoDocente();

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

    @Override
    public ReporteDesempenoResponse generarReportePorCurso(int cicloLectivoAnio, Long cursoId) {
        log.info("Generando reporte de desempeño para curso {} en año: {}", cursoId, cicloLectivoAnio);

        var cicloLectivo = cicloLectivoRepository.findByAnio(cicloLectivoAnio)
                .orElseThrow(() -> new RuntimeException("No existe ciclo lectivo para el año: " + cicloLectivoAnio));

        List<Object[]> materiasConDocente = materiaCursoRepository
                .findMateriasConDocentePorCicloYCurso(cicloLectivoAnio, cursoId);

        Map<Long, List<ReporteDesempenoDocenteDto>> resultadosPorMateria = new HashMap<>();

        for (Object[] row : materiasConDocente) {
            ReporteDesempenoDocenteDto resultado = procesarMateriaCurso(row, cicloLectivo);

            Long materiaId = resultado.getMateriaId();
            resultadosPorMateria.computeIfAbsent(materiaId, k -> new ArrayList<>()).add(resultado);
        }

        if (resultadosPorMateria.isEmpty()) {
            return ReporteDesempenoResponse.builder()
                    .code(-1)
                    .mensaje("No se encontraron datos para el curso en el año especificado")
                    .build();
        }

        List<ReporteDesempenoMateriaDto> reportesMaterias = new ArrayList<>();
        for (List<ReporteDesempenoDocenteDto> resultados : resultadosPorMateria.values()) {
            ReporteDesempenoMateriaDto reporteMateria = construirReporteMateria(resultados);
            if (reporteMateria != null) {
                reportesMaterias.add(reporteMateria);
            }
        }

        // Obtener información del curso para el resumen
        String infoCurso = String.format("%s %s %s",
                reportesMaterias.get(0).getResultadosPorDocente().get(0).getAnio(),
                reportesMaterias.get(0).getResultadosPorDocente().get(0).getNivel(),
                reportesMaterias.get(0).getResultadosPorDocente().get(0).getDivision());

        return ReporteDesempenoResponse.builder()
                .code(0)
                .mensaje("Reporte por curso generado exitosamente")
                .cicloLectivoAnio(cicloLectivoAnio)
                .nombreCicloLectivo(cicloLectivo.getNombre())
                .totalMaterias(reportesMaterias.size())
                .totalDocentes(calcularTotalDocentes(reportesMaterias))
                .totalAlumnos(calcularTotalAlumnos(reportesMaterias))
                .totalCursos(1) // Solo un curso
                .resultadosPorMateria(reportesMaterias)
                .resumenEjecutivo("Análisis específico del curso " + infoCurso)
                .hallazgosImportantes(generarHallazgos(reportesMaterias))
                .recomendaciones(generarRecomendaciones(reportesMaterias))
                .build();
    }

    /**
     * Procesa una fila de "materia-curso-docente" y calcula el desempeño
     * usando la misma lógica de aprobado/desaprobado que ReporteNotasServiceImpl.
     */
    private ReporteDesempenoDocenteDto procesarMateriaCurso(Object[] row, CicloLectivo cicloLectivo) {
        // Extraer datos de la query (ajustar según la query real)
        Long materiaCursoId   = (Long) row[0];
        Long materiaId        = (Long) row[1];
        String nombreMateria  = (String) row[2];
        Long docenteId        = (Long) row[3];
        String apellidoDocente= (String) row[4];
        String nombreDocente  = (String) row[5];
        Long cursoId          = (Long) row[6];
        Integer anio          = (Integer) row[7];
        String nivel          = (String) row[8];
        String division       = (String) row[9];

        int cicloAnio = cicloLectivo.getAnio();

        // Alumnos que cursaron ese curso en ese ciclo
        List<Long> alumnosIds = historialCursoRepository
                .findAlumnosIdsPorCursoYCiclo(cursoId, cicloAnio);

        int totalAlumnos = alumnosIds.size();
        int aprobados = 0;
        int desaprobados = 0;

        BigDecimal sumaNotas = BigDecimal.ZERO;
        int totalConNota = 0;

        BigDecimal notaMinima = BigDecimal.valueOf(10);
        BigDecimal notaMaxima = BigDecimal.ZERO;

        // Rango para calificaciones (año calendario)
        LocalDate desdeCalif = LocalDate.of(cicloAnio, 1, 1);
        LocalDate hastaCalif = LocalDate.of(cicloAnio, 12, 31);

        // Rango para mesas (según fechas del ciclo)
        LocalDate desdeMesas = cicloLectivo.getFechaDesde();
        LocalDate hastaMesas = cicloLectivo.getFechaHasta();

        // Todas las calificaciones del año para esos alumnos
        List<Calificacion> califs = alumnosIds.isEmpty()
                ? List.of()
                : calificacionRepository.findByAlumnosAndAnio(alumnosIds, desdeCalif, hastaCalif);

        // Todas las mesas del ciclo para esos alumnos
        List<MesaExamenAlumno> mesasAlumnos = alumnosIds.isEmpty()
                ? List.of()
                : mesaExamenAlumnoRepo.findByAlumno_IdInAndMesaExamen_FechaBetween(
                alumnosIds, desdeMesas, hastaMesas
        );

        for (Long alumnoId : alumnosIds) {
            // Calificaciones de este alumno en esta materia
            List<Calificacion> califsAlumnoMateria = califs.stream()
                    .filter(c -> c.getHistorialMateria().getHistorialCurso().getAlumno().getId().equals(alumnoId))
                    .filter(c -> c.getHistorialMateria().getMateriaCurso().getMateria().getId().equals(materiaId))
                    .toList();

            Integer[] e1Notas = new Integer[]{null, null, null, null};
            Integer[] e2Notas = new Integer[]{null, null, null, null};

            for (Calificacion c : califsAlumnoMateria) {
                Integer n = c.getNumeroNota();
                if (n == null || n < 1 || n > 4) continue;
                int idx = n - 1;

                if (c.getEtapa() == 1) e1Notas[idx] = c.getNota();
                else if (c.getEtapa() == 2) e2Notas[idx] = c.getNota();
            }

            Double e1 = promedio(e1Notas);
            Double e2 = promedio(e2Notas);
            Double pg = promedioGeneral(e1, e2);

            // Mesas de este alumno en esta materia (en el ciclo)
            List<MesaExamenAlumno> mesasAlumnoMateria = mesasAlumnos.stream()
                    .filter(mea -> mea.getAlumno().getId().equals(alumnoId) &&
                            mea.getMesaExamen().getMateriaCurso().getMateria().getId().equals(materiaId))
                    .sorted(Comparator.comparing(
                            mea -> mea.getMesaExamen().getFecha(),
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();

            Integer notaMasAlta = null;
            for (MesaExamenAlumno mesa : mesasAlumnoMateria) {
                Integer notaMesa = mesa.getNotaFinal();
                if (notaMesa == null) continue;
                if (notaMasAlta == null || notaMesa > notaMasAlta) {
                    notaMasAlta = notaMesa;
                }
            }

            boolean apr1 = e1 != null && e1 >= 6.0;
            boolean apr2 = e2 != null && e2 >= 6.0;
            boolean aprobado;
            Double pfa;

            if (notaMasAlta != null) {
                pfa = notaMasAlta.doubleValue();
                aprobado = notaMasAlta >= 6;
            } else if (apr1 && apr2) {
                pfa = pg;
                aprobado = true;
            } else {
                pfa = pg;
                aprobado = false;
            }

            if (aprobado) aprobados++;
            else desaprobados++;

            if (pfa != null) {
                BigDecimal notaBD = BigDecimal.valueOf(pfa);
                sumaNotas = sumaNotas.add(notaBD);
                totalConNota++;

                if (notaBD.compareTo(notaMinima) < 0) notaMinima = notaBD;
                if (notaBD.compareTo(notaMaxima) > 0) notaMaxima = notaBD;
            }
        }

        BigDecimal porcentajeAprobacion = totalAlumnos > 0
                ? BigDecimal.valueOf(aprobados * 100.0 / totalAlumnos)
                .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal porcentajeReprobacion = totalAlumnos > 0
                ? BigDecimal.valueOf(desaprobados * 100.0 / totalAlumnos)
                .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal promedioGeneral = totalConNota > 0
                ? sumaNotas.divide(BigDecimal.valueOf(totalConNota), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Ajustar nota mínima si nunca se actualizó
        if (notaMinima.equals(BigDecimal.valueOf(10)) || totalConNota == 0) {
            notaMinima = BigDecimal.ZERO;
        }

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
                .notaMinima(notaMinima)
                .notaMaxima(notaMaxima)
                .cicloLectivoAnio(cicloAnio)
                .estadoAnalisis(estadoAnalisis)
                .build();
    }

    private ReporteDesempenoMateriaDto construirReporteMateria(List<ReporteDesempenoDocenteDto> resultados) {
        if (resultados.isEmpty()) return null;

        ReporteDesempenoDocenteDto primero = resultados.get(0);

        int totalAlumnos = resultados.stream().mapToInt(ReporteDesempenoDocenteDto::getTotalAlumnos).sum();
        int totalAprobados = resultados.stream().mapToInt(ReporteDesempenoDocenteDto::getAlumnosAprobados).sum();

        BigDecimal promedioAprobacion = totalAlumnos > 0
                ? BigDecimal.valueOf(totalAprobados * 100.0 / totalAlumnos)
                .setScale(2, RoundingMode.HALF_UP)
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

    @Override
    public ReporteDesempenoResponse generarReporteNotasIndividuales(int cicloLectivoAnio) {
        log.info("Generando reporte de notas individuales para año: {}", cicloLectivoAnio);

        // TODO: Implementar análisis de las 4 notas individuales por etapa
        return ReporteDesempenoResponse.builder()
                .code(0)
                .mensaje("Funcionalidad en desarrollo - análisis de notas individuales")
                .cicloLectivoAnio(cicloLectivoAnio)
                .resultadosPorMateria(new ArrayList<>())
                .hallazgosImportantes(List.of("Funcionalidad en desarrollo"))
                .recomendaciones(List.of("Se implementará próximamente"))
                .build();
    }

    // Helpers de promedio (mismos conceptos que en ReporteNotasServiceImpl)
    private static Double promedio(Integer[] notas) {
        int suma = 0, n = 0;
        for (Integer v : notas) {
            if (v != null) {
                suma += v;
                n++;
            }
        }
        if (n == 0) return null;
        return redondear1((double) suma / n);
    }

    private static Double promedioGeneral(Double e1, Double e2) {
        if (e1 == null && e2 == null) return null;
        if (e1 == null) return e2;
        if (e2 == null) return e1;
        return redondear1((e1 + e2) / 2.0);
    }

    private static Double redondear1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
