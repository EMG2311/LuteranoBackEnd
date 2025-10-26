package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.ReporteLibreDto;
import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.CicloLectivo;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.HistorialCurso;
import com.grup14.luterano.exeptions.ReporteLibreException;
import com.grup14.luterano.repository.AsistenciaAlumnoRepository;
import com.grup14.luterano.repository.CicloLectivoRepository;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.repository.HistorialCursoRepository;
import com.grup14.luterano.response.ReporteLibresResponse;
import com.grup14.luterano.service.ReporteLibreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteLibreServiceImpl implements ReporteLibreService {
    private final CursoRepository cursoRepository;
    private final CicloLectivoRepository cicloLectivoRepository;
    private final HistorialCursoRepository historialCursoRepository;
    private final AsistenciaAlumnoRepository asistenciaAlumnoRepository;

    @Transactional(readOnly = true)
    @Override
    public ReporteLibresResponse listarLibres(Integer anio, Long cursoId) {
        if (anio == null) throw new ReporteLibreException("anio es requerido");

        // ciclo lectivo del año (busco con pivote 1/7)
        LocalDate pivote = LocalDate.of(anio, 7, 1);
        CicloLectivo ciclo = cicloLectivoRepository
                .findByFechaDesdeBeforeAndFechaHastaAfter(pivote, pivote)
                .orElseThrow(() -> new ReporteLibreException("No hay ciclo lectivo para el año " + anio));

        // historiales abiertos: por curso o todos
        List<HistorialCurso> hcs = (cursoId != null)
                ? historialCursoRepository.findAbiertosByCursoAndCiclo(cursoId, ciclo.getId())
                : historialCursoRepository.findAbiertosByCiclo(ciclo.getId());

        if (hcs.isEmpty()) {
            return ReporteLibresResponse.builder()
                    .anio(anio)
                    .cursoId(cursoId)
                    .filas(List.of())
                    .totalLibres(0)
                    .code(0).mensaje("OK, No hay historial de Curso abierto")
                    .build();
        }

        // índice por alumno
        Map<Long, HistorialCurso> idxHcByAlumno = hcs.stream()
                .collect(Collectors.toMap(h -> h.getAlumno().getId(), h -> h));

        List<Long> alumnoIds = hcs.stream().map(h -> h.getAlumno().getId()).toList();

        // rango del año calendario consultado
        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);

        // suma ponderada por alumno
        List<Object[]> rows = asistenciaAlumnoRepository
                .sumarInasistenciasPorAlumnoEntreFechas(desde, hasta, alumnoIds);

        Map<Long, Double> totales = new HashMap<>();
        for (Object[] r : rows) {
            Long aId = (Long) r[0];
            Double total = (Double) r[1];
            totales.put(aId, total == null ? 0.0 : total);
        }


        List<ReporteLibreDto> filas = new ArrayList<>();
        for (Long aId : alumnoIds) {
            double total = totales.getOrDefault(aId, 0.0);
            if (total > 25.0) {
                HistorialCurso hc = idxHcByAlumno.get(aId);
                Alumno a = hc.getAlumno();
                Curso c = hc.getCurso();

                filas.add(ReporteLibreDto.builder()
                        .alumnoId(a.getId())
                        .dni(a.getDni())
                        .apellido(a.getApellido())
                        .nombre(a.getNombre())
                        .cursoId(c.getId())
                        .anio(c.getAnio())
                        .nivel(c.getNivel())
                        .division(c.getDivision())
                        .cursoEtiqueta(etiquetaCurso(c))
                        .motivo("Inasistencias > 25")
                        .inasistenciasAcum(Math.round(total * 100.0) / 100.0)
                        .build());
            }
        }


        Collator coll = Collator.getInstance(new Locale("es","AR"));
        coll.setStrength(Collator.PRIMARY);
        filas.sort(Comparator
                .comparing(ReporteLibreDto::getAnio)
                .thenComparing(ReporteLibreDto::getNivel, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(ReporteLibreDto::getDivision, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(ReporteLibreDto::getApellido, coll)
                .thenComparing(ReporteLibreDto::getNombre, coll));

        return ReporteLibresResponse.builder()
                .anio(anio)
                .cursoId(cursoId)
                .filas(filas)
                .totalLibres(filas.size())
                .code(0).mensaje("OK")
                .build();
    }

    private static String etiquetaCurso(Curso c) {
        return c.getAnio() + "° " + (c.getNivel() != null ? c.getNivel().name() : "");
    }
}