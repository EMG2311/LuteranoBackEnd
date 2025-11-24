package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.*;
import com.grup14.luterano.entities.enums.CondicionRinde;
import com.grup14.luterano.entities.enums.EstadoMateriaAlumno;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.service.ElegibilidadMesaExamenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElegibilidadMesaExamenServiceImpl implements ElegibilidadMesaExamenService {

    private final CicloLectivoRepository cicloLectivoRepository;
    private final HistorialCursoRepository historialCursoRepository;
    private final HistorialMateriaRepository historialMateriaRepository;
    private final CalificacionRepository calificacionRepository;
    private final MesaExamenAlumnoRepository mesaExamenAlumnoRepo;

    @Override
    public Optional<CondicionRinde> determinarCondicionRinde(
            Alumno alumno,
            MateriaCurso materiaCurso,
            LocalDate fechaMesa
    ) {
        if (alumno == null || materiaCurso == null || fechaMesa == null) {
            return Optional.empty();
        }

        Long alumnoId = alumno.getId();
        Long materiaId = materiaCurso.getMateria().getId();
        Long cursoId = materiaCurso.getCurso().getId();

        int anioCursada = resolverAnioCursada(fechaMesa);

        // Buscar ciclo lectivo correspondiente al año de cursada
        LocalDate pivote = LocalDate.of(anioCursada, 7, 1);
        Optional<CicloLectivo> cicloOpt = cicloLectivoRepository
                .findByFechaDesdeBeforeAndFechaHastaAfter(pivote, pivote);

        if (cicloOpt.isEmpty()) {
            log.warn("No se encontró ciclo lectivo para el año {} (alumno={}, materia={})",
                    anioCursada, alumnoId, materiaId);
            // en este caso, solo podemos mirar previas históricas
            return determinarCondicionDesdePrevias(alumnoId, materiaId, anioCursada);
        }

        CicloLectivo ciclo = cicloOpt.get();

        // 1) Notas de cursado (E1 / E2) del año lectivo
        Double e1 = null;
        Double e2 = null;

        LocalDate desde = LocalDate.of(anioCursada, 1, 1);
        LocalDate hasta = LocalDate.of(anioCursada, 12, 31);

        List<Long> alumnosIds = List.of(alumnoId);
        List<Calificacion> califs = calificacionRepository
                .findByAlumnosCursoCicloAndAnio(alumnosIds, cursoId, ciclo.getId(), desde, hasta);

        int sum1 = 0, sum2 = 0, cnt1 = 0, cnt2 = 0;

        for (Calificacion c : califs) {
            HistorialMateria hm = c.getHistorialMateria();
            if (hm == null || hm.getMateriaCurso() == null) continue;
            if (!Objects.equals(hm.getMateriaCurso().getMateria().getId(), materiaId)) continue;

            Integer nota = c.getNota();
            if (nota == null) continue;

            if (c.getEtapa() == 1) {
                sum1 += nota;
                cnt1++;
            } else if (c.getEtapa() == 2) {
                sum2 += nota;
                cnt2++;
            }
        }

        if (cnt1 > 0) {
            e1 = round1((double) sum1 / cnt1);
        }
        if (cnt2 > 0) {
            e2 = round1((double) sum2 / cnt2);
        }

        boolean apr1 = e1 != null && e1 >= 6.0;
        boolean apr2 = e2 != null && e2 >= 6.0;
        boolean tieneCursado = (e1 != null) || (e2 != null);

        // 2) ¿Ya aprobó por mesa en este ciclo?
        List<MesaExamenAlumno> mesas = mesaExamenAlumnoRepo
                .findByAlumno_IdAndMesaExamen_FechaBetween(
                        alumnoId,
                        ciclo.getFechaDesde(),
                        ciclo.getFechaHasta()
                );

        MesaExamenAlumno mesaMasReciente = mesas.stream()
                .filter(mea -> mea.getMesaExamen() != null
                        && mea.getMesaExamen().getMateriaCurso() != null
                        && mea.getMesaExamen().getMateriaCurso().getMateria() != null
                        && Objects.equals(
                        mea.getMesaExamen().getMateriaCurso().getMateria().getId(),
                        materiaId
                ))
                .filter(mea -> mea.getNotaFinal() != null)
                .max(Comparator.comparing(mea -> mea.getMesaExamen().getFecha(),
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);

        if (mesaMasReciente != null && mesaMasReciente.getNotaFinal() != null
                && mesaMasReciente.getNotaFinal() >= 6) {
            // Ya aprobó por mesa → no rinde
            return Optional.empty();
        }

        // 3) Si promocionó por cursado (ambas etapas >= 6) → no rinde
        if (apr1 && apr2) {
            return Optional.empty();
        }

        // 4) Si tiene cursado, definimos condición por e1/e2
        if (tieneCursado) {
            CondicionRinde cond = (apr1 ^ apr2)
                    ? CondicionRinde.COLOQUIO
                    : CondicionRinde.EXAMEN;
            return Optional.of(cond);
        }

        // 5) Si no tiene cursado este año, mirar previas de años anteriores
        return determinarCondicionDesdePrevias(alumnoId, materiaId, anioCursada);
    }

    /**
     * Busca en TODO el historial del alumno si tiene la materia DESAPROBADA
     * en años anteriores al año de la mesa.
     * Si la tiene, devuelve EXAMEN (previa). Si no, empty.
     */
    private Optional<CondicionRinde> determinarCondicionDesdePrevias(
            Long alumnoId,
            Long materiaId,
            int anioMesa
    ) {
        List<HistorialCurso> historialCompleto =
                historialCursoRepository.findHistorialCompletoByAlumnoId(alumnoId);

        boolean tieneMateriaPendiente = false;

        for (HistorialCurso hc : historialCompleto) {
            if (hc.getCicloLectivo() == null || hc.getCicloLectivo().getFechaDesde() == null) continue;

            int anioCurso = hc.getCicloLectivo().getFechaDesde().getYear();
            // Solo cursos de años ANTERIORES al año de la mesa
            if (anioCurso >= anioMesa) {
                continue;
            }

            List<HistorialMateria> hms = historialMateriaRepository.findAllByHistorialCursoId(hc.getId());
            for (HistorialMateria hm : hms) {
                if (hm.getMateriaCurso() == null
                        || hm.getMateriaCurso().getMateria() == null) continue;

                if (Objects.equals(hm.getMateriaCurso().getMateria().getId(), materiaId)
                        && hm.getEstado() == EstadoMateriaAlumno.DESAPROBADA) {
                    tieneMateriaPendiente = true;
                    break;
                }
            }
            if (tieneMateriaPendiente) break;
        }

        if (!tieneMateriaPendiente) {
            return Optional.empty();
        }
        // Previas rinden EXAMEN
        return Optional.of(CondicionRinde.EXAMEN);
    }

    /**
     * Enero, febrero y marzo cuentan como parte del ciclo lectivo anterior.
     */
    private int resolverAnioCursada(LocalDate fechaBase) {
        int year = fechaBase.getYear();
        int month = fechaBase.getMonthValue();
        return (month <= 3) ? year - 1 : year;
    }

    private static Double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
