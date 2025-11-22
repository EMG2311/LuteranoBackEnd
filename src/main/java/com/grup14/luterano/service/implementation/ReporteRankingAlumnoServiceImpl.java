package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.reporteRankingAlumno.AlumnoRankingDto;
import com.grup14.luterano.dto.reporteRankingAlumno.CursoRankingDto;
import com.grup14.luterano.entities.CicloLectivo;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.HistorialCurso;
import com.grup14.luterano.mappers.CursoMapper;
import com.grup14.luterano.repository.CicloLectivoRepository;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.repository.HistorialCursoRepository;
import com.grup14.luterano.repository.MateriaCursoRepository;
import com.grup14.luterano.response.reporteRankingAlumno.RankingAlumnosColegioResponse;
import com.grup14.luterano.response.reporteRankingAlumno.RankingAlumnosCursoResponse;
import com.grup14.luterano.response.reporteRankingAlumno.RankingTodosCursosResponse;
import com.grup14.luterano.service.NotaFinalService;
import com.grup14.luterano.service.ReporteRankingAlumnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteRankingAlumnoServiceImpl implements ReporteRankingAlumnoService {

    private final HistorialCursoRepository historialCursoRepo;
    private final CicloLectivoRepository cicloLectivoRepo;
    private final CursoRepository cursoRepo;
    private final NotaFinalService notaFinalService;
    private final MateriaCursoRepository materiaCursoRepository;

    // =========================
    // RANKING POR CURSO
    // =========================

    @Override
    @Transactional(readOnly = true)
    public RankingAlumnosCursoResponse rankingAlumnosPorCurso(Long cursoId, int anio, int top) {
        if (cursoId == null) {
            throw new IllegalArgumentException("cursoId es requerido");
        }

        final int topLimit = (top <= 0) ? 1 : top;

        // Validar que el curso existe
        Curso curso = cursoRepo.findById(cursoId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado (id=" + cursoId + ")"));

        // Obtener ciclo lectivo
        LocalDate mid = LocalDate.of(anio, 7, 1);
        CicloLectivo ciclo = cicloLectivoRepo.findByFechaDesdeBeforeAndFechaHastaAfter(mid, mid)
                .orElseThrow(() -> new IllegalArgumentException("No hay ciclo lectivo que contenga el año " + anio));

        // Obtener historiales activos - NO requiere promedio pre-calculado
        List<HistorialCurso> historiales =
                historialCursoRepo.findHistorialesActivosParaRankingExcluyendoInactivos(cursoId, ciclo.getId());

        int cicloAnio = ciclo.getFechaDesde().getYear();

        // Calcular promedios dinámicamente
        for (HistorialCurso hc : historiales) {
            BigDecimal promedio = calcularPromedioParaRanking(
                    hc.getAlumno().getId(), ciclo.getId(), cicloAnio);
            hc.setPromedio(promedio);
        }

        // Filtrar solo los que tienen promedio y ordenar
        historiales = historiales.stream()
                .filter(hc -> hc.getPromedio() != null)
                .sorted((h1, h2) -> {
                    int cmp = h2.getPromedio().compareTo(h1.getPromedio());
                    if (cmp != 0) return cmp;
                    int cmpAp = h1.getAlumno().getApellido()
                            .compareToIgnoreCase(h2.getAlumno().getApellido());
                    if (cmpAp != 0) return cmpAp;
                    return h1.getAlumno().getNombre()
                            .compareToIgnoreCase(h2.getAlumno().getNombre());
                })
                .collect(Collectors.toList());

        List<AlumnoRankingDto> ranking = calcularRankingConEmpates(historiales, topLimit);

        String cursoNombre = curso.getAnio() + "° " + curso.getDivision().toString()
                + " - " + curso.getNivel().toString();

        return RankingAlumnosCursoResponse.builder()
                .ranking(ranking)
                .cursoId(cursoId)
                .cursoNombre(cursoNombre)
                .totalAlumnos(historiales.size())
                .code(0)
                .mensaje("OK")
                .build();
    }

    // =========================
    // RANKING COLEGIO (NO DINÁMICO)
    // =========================

    @Override
    @Transactional(readOnly = true)
    public RankingAlumnosColegioResponse rankingAlumnosColegio(int anio, int top) {
        final int topLimit = (top <= 0) ? 1 : top;

        // Obtener ciclo lectivo
        LocalDate mid = LocalDate.of(anio, 7, 1);
        CicloLectivo ciclo = cicloLectivoRepo.findByFechaDesdeBeforeAndFechaHastaAfter(mid, mid)
                .orElseThrow(() -> new IllegalArgumentException("No hay ciclo lectivo que contenga el año " + anio));

        // Obtener ranking de todo el colegio usando solo promedios ya calculados
        List<HistorialCurso> historiales =
                historialCursoRepo.findRankingByCicloExcluyendoInactivos(ciclo.getId());

        // Filtrar nulls de promedio por seguridad
        historiales = historiales.stream()
                .filter(hc -> hc.getPromedio() != null)
                .sorted((h1, h2) -> {
                    int cmp = h2.getPromedio().compareTo(h1.getPromedio());
                    if (cmp != 0) return cmp;
                    int cmpAp = h1.getAlumno().getApellido()
                            .compareToIgnoreCase(h2.getAlumno().getApellido());
                    if (cmpAp != 0) return cmpAp;
                    return h1.getAlumno().getNombre()
                            .compareToIgnoreCase(h2.getAlumno().getNombre());
                })
                .collect(Collectors.toList());

        List<AlumnoRankingDto> ranking = calcularRankingConEmpates(historiales, topLimit);

        return RankingAlumnosColegioResponse.builder()
                .ranking(ranking)
                .totalAlumnos(historiales.size())
                .code(0)
                .mensaje("OK")
                .build();
    }

    // =========================
    // RANKING TODOS LOS CURSOS
    // =========================

    @Override
    @Transactional(readOnly = true)
    public RankingTodosCursosResponse rankingTodosCursos(int anio, int top) {
        final int topLimit = (top <= 0) ? 1 : top;

        // Obtener ciclo lectivo
        LocalDate mid = LocalDate.of(anio, 7, 1);
        CicloLectivo ciclo = cicloLectivoRepo.findByFechaDesdeBeforeAndFechaHastaAfter(mid, mid)
                .orElseThrow(() -> new IllegalArgumentException("No hay ciclo lectivo que contenga el año " + anio));

        // Obtener todos los cursos activos
        List<Curso> cursos = historialCursoRepo.findCursosActivosByCiclo(ciclo.getId());

        int cicloAnio = ciclo.getFechaDesde().getYear();

        List<CursoRankingDto> cursosRanking = cursos.stream()
                .map(curso -> {
                    // Obtener historiales activos - NO requiere promedio pre-calculado
                    List<HistorialCurso> historiales =
                            historialCursoRepo.findHistorialesActivosParaRankingExcluyendoInactivos(
                                    curso.getId(), ciclo.getId());

                    // Calcular promedios dinámicamente
                    for (HistorialCurso hc : historiales) {
                        BigDecimal promedio = calcularPromedioParaRanking(
                                hc.getAlumno().getId(), ciclo.getId(), cicloAnio);
                        hc.setPromedio(promedio);
                    }

                    // Filtrar solo los que tienen promedio y ordenar
                    historiales = historiales.stream()
                            .filter(hc -> hc.getPromedio() != null)
                            .sorted((h1, h2) -> {
                                int cmp = h2.getPromedio().compareTo(h1.getPromedio());
                                if (cmp != 0) return cmp;
                                int cmpAp = h1.getAlumno().getApellido()
                                        .compareToIgnoreCase(h2.getAlumno().getApellido());
                                if (cmpAp != 0) return cmpAp;
                                return h1.getAlumno().getNombre()
                                        .compareToIgnoreCase(h2.getAlumno().getNombre());
                            })
                            .collect(Collectors.toList());

                    List<AlumnoRankingDto> ranking = calcularRankingConEmpates(historiales, topLimit);

                    return CursoRankingDto.builder()
                            .curso(CursoMapper.toDto(curso))
                            .topAlumnos(ranking)
                            .totalAlumnos(historiales.size())
                            .build();
                })
                .collect(Collectors.toList());

        return RankingTodosCursosResponse.builder()
                .cursosRanking(cursosRanking)
                .totalCursos(cursos.size())
                .code(0)
                .mensaje("OK")
                .build();
    }

    // =========================
    // CÁLCULO DE PROMEDIO DINÁMICO
    // =========================

    /**
     * Calcula el promedio de un alumno para el ranking en un ciclo lectivo específico.
     * Obtiene todas las materias del curso actual del alumno y calcula el promedio de las notas finales.
     */
    private BigDecimal calcularPromedioParaRanking(Long alumnoId, Long cicloLectivoId, int anio) {
        // Obtener el historial curso del alumno en este ciclo
        List<HistorialCurso> historiales =
                historialCursoRepo.findByAlumno_IdAndCicloLectivo_Id(alumnoId, cicloLectivoId);

        if (historiales.isEmpty()) {
            return null;
        }

        // Obtener todas las materias del curso (por alumno y ciclo)
        List<Long> materiasIds =
                materiaCursoRepository.findMateriasIdsPorAlumnoCiclo(alumnoId, cicloLectivoId);

        if (materiasIds.isEmpty()) {
            return null;
        }

        // Calcular promedio de notas finales
        List<Integer> notasFinales = new ArrayList<>();
        for (Long materiaId : materiasIds) {
            Integer notaFinal = notaFinalService.calcularNotaFinal(alumnoId, materiaId, anio);
            if (notaFinal != null) {
                notasFinales.add(notaFinal);
            }
        }

        if (notasFinales.isEmpty()) {
            return null;
        }

        double promedio = notasFinales.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        return BigDecimal.valueOf(promedio);
    }

    // =========================
    // CÁLCULO DE RANKING CON EMPATES
    // =========================

    /**
     * Calcula el ranking con empates, devolviendo más de N si hay empates
     * en la última posición incluida.
     */
    private List<AlumnoRankingDto> calcularRankingConEmpates(List<HistorialCurso> historiales, int topN) {
        List<AlumnoRankingDto> ranking = new ArrayList<>();

        if (historiales == null || historiales.isEmpty()) {
            return ranking;
        }

        final int topLimit = (topN <= 0) ? 1 : topN;

        BigDecimal promedioAnterior = null;
        int posicionActual = 1;

        for (int i = 0; i < historiales.size(); i++) {
            HistorialCurso hc = historiales.get(i);
            BigDecimal promedioActual = hc.getPromedio();

            if (promedioActual == null) {
                continue;
            }

            // Si el promedio cambió, la posición pasa a ser (i + 1)
            if (promedioAnterior != null && promedioActual.compareTo(promedioAnterior) != 0) {
                posicionActual = i + 1;
            }

            // Si estamos fuera del top N, solo seguimos si hay empate con alguien del top N
            if (posicionActual > topLimit) {
                boolean empateConTopN = ranking.stream()
                        .anyMatch(r -> r.getPosicion() <= topLimit &&
                                r.getPromedio().compareTo(promedioActual) == 0);
                if (!empateConTopN) {
                    break;
                }
            }

            var alumno = hc.getAlumno();
            String nombreCompleto = alumno.getApellido() + ", " + alumno.getNombre();

            AlumnoRankingDto dto = AlumnoRankingDto.builder()
                    .alumnoId(alumno.getId())
                    .dni(alumno.getDni())
                    .apellido(alumno.getApellido())
                    .nombre(alumno.getNombre())
                    .nombreCompleto(nombreCompleto)
                    .promedio(promedioActual)
                    .posicion(posicionActual)
                    .curso(CursoMapper.toDto(hc.getCurso()))
                    .build();

            ranking.add(dto);
            promedioAnterior = promedioActual;
        }

        return ranking;
    }
}
