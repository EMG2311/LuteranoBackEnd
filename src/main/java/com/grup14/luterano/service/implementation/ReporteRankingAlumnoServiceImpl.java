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
import com.grup14.luterano.response.reporteRankingAlumno.RankingAlumnosCursoResponse;
import com.grup14.luterano.response.reporteRankingAlumno.RankingAlumnosColegioResponse;
import com.grup14.luterano.response.reporteRankingAlumno.RankingTodosCursosResponse;
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

    @Override
    @Transactional(readOnly = true)
    public RankingAlumnosCursoResponse rankingAlumnosPorCurso(Long cursoId, int anio) {
        if (cursoId == null) {
            throw new IllegalArgumentException("cursoId es requerido");
        }

        // Validar que el curso existe
        Curso curso = cursoRepo.findById(cursoId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado (id=" + cursoId + ")"));

        // Obtener ciclo lectivo
        LocalDate mid = LocalDate.of(anio, 7, 1);
        CicloLectivo ciclo = cicloLectivoRepo.findByFechaDesdeBeforeAndFechaHastaAfter(mid, mid)
                .orElseThrow(() -> new IllegalArgumentException("No hay ciclo lectivo que contenga el año " + anio));

        // Obtener ranking del curso
        List<HistorialCurso> historiales = historialCursoRepo.findRankingByCursoAndCiclo(cursoId, ciclo.getId());
        List<AlumnoRankingDto> ranking = calcularRankingConEmpates(historiales);

        String cursoNombre = curso.getAnio() + "° " + curso.getDivision() + " - " + curso.getNivel();

        return RankingAlumnosCursoResponse.builder()
                .ranking(ranking)
                .cursoId(cursoId)
                .cursoNombre(cursoNombre)
                .totalAlumnos(historiales.size())
                .code(0)
                .mensaje("OK")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RankingAlumnosColegioResponse rankingAlumnosColegio(int anio) {
        // Obtener ciclo lectivo
        LocalDate mid = LocalDate.of(anio, 7, 1);
        CicloLectivo ciclo = cicloLectivoRepo.findByFechaDesdeBeforeAndFechaHastaAfter(mid, mid)
                .orElseThrow(() -> new IllegalArgumentException("No hay ciclo lectivo que contenga el año " + anio));

        // Obtener ranking de todo el colegio
        List<HistorialCurso> historiales = historialCursoRepo.findRankingByCiclo(ciclo.getId());
        List<AlumnoRankingDto> ranking = calcularRankingConEmpates(historiales);

        return RankingAlumnosColegioResponse.builder()
                .ranking(ranking)
                .totalAlumnos(historiales.size())
                .code(0)
                .mensaje("OK")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RankingTodosCursosResponse rankingTodosCursos(int anio) {
        // Obtener ciclo lectivo
        LocalDate mid = LocalDate.of(anio, 7, 1);
        CicloLectivo ciclo = cicloLectivoRepo.findByFechaDesdeBeforeAndFechaHastaAfter(mid, mid)
                .orElseThrow(() -> new IllegalArgumentException("No hay ciclo lectivo que contenga el año " + anio));

        // Obtener todos los cursos activos
        List<Curso> cursos = historialCursoRepo.findCursosActivosByCiclo(ciclo.getId());
        
        List<CursoRankingDto> cursosRanking = cursos.stream()
                .map(curso -> {
                    // Obtener ranking del curso
                    List<HistorialCurso> historiales = historialCursoRepo.findRankingByCursoAndCiclo(curso.getId(), ciclo.getId());
                    List<AlumnoRankingDto> ranking = calcularRankingConEmpates(historiales);

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

    /**
     * Calcula el ranking con empates, devolviendo más de 3 si hay empates en el top 3
     */
    private List<AlumnoRankingDto> calcularRankingConEmpates(List<HistorialCurso> historiales) {
        if (historiales.isEmpty()) {
            return new ArrayList<>();
        }

        List<AlumnoRankingDto> ranking = new ArrayList<>();
        BigDecimal promedioAnterior = null;
        int posicionActual = 1;

        for (int i = 0; i < historiales.size(); i++) {
            HistorialCurso hc = historiales.get(i);
            BigDecimal promedioActual = hc.getPromedio();

            // Si el promedio cambió, actualizar la posición
            if (promedioAnterior != null && promedioActual.compareTo(promedioAnterior) != 0) {
                posicionActual = i + 1;
            }

            // Si estamos fuera del top 3 y no hay empate con el top 3, parar
            if (posicionActual > 3 && (ranking.isEmpty() || 
                ranking.get(ranking.size() - 1).getPosicion() < 3)) {
                break;
            }

            // Si estamos en posición > 3 pero hay empate con alguien del top 3, continuar
            boolean empateConTop3 = false;
            if (posicionActual > 3 && !ranking.isEmpty()) {
                empateConTop3 = ranking.stream()
                        .anyMatch(r -> r.getPosicion() <= 3 && 
                                r.getPromedio().compareTo(promedioActual) == 0);
            }

            if (posicionActual <= 3 || empateConTop3) {
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
                        .build();

                ranking.add(dto);
            }

            promedioAnterior = promedioActual;
        }

        return ranking;
    }
}