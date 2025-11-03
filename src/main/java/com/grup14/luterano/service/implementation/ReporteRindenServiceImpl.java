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

    @Transactional(readOnly = true)
    public ReporteRindenResponse listarRindenPorCurso(Long cursoId, int anio) {
        if (cursoId == null) throw new ReporteRindeException("cursoId es requerido");

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ReporteRindeException("Curso no encontrado (id=" + cursoId + ")"));

        LocalDate pivote = LocalDate.of(anio, 7, 1);
        CicloLectivo ciclo = cicloLectivoRepository
                .findByFechaDesdeBeforeAndFechaHastaAfter(pivote, pivote)
                .orElseThrow(() -> new ReporteRindeException("No hay ciclo lectivo para el año " + anio));

        List<HistorialCurso> hcs = historialCursoRepository.findAbiertosByCursoAndCiclo(cursoId, ciclo.getId());
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

                boolean apr1 = e1 != null && e1 >= 6.0;
                boolean apr2 = e2 != null && e2 >= 6.0;

                if (apr1 && apr2) continue;

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
                        .co(null).ex(null).pf(null)
                        .condicion(cond)
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
}
