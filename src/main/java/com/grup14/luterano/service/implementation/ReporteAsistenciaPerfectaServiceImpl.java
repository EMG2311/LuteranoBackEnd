package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.CursoDto;
import com.grup14.luterano.dto.reporteAsistenciaPerfecta.AlumnoLigeroDto;
import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.HistorialCurso;
import com.grup14.luterano.mappers.CursoMapper;
import com.grup14.luterano.repository.AsistenciaAlumnoRepository;
import com.grup14.luterano.repository.CicloLectivoRepository;
import com.grup14.luterano.repository.HistorialCursoRepository;
import com.grup14.luterano.repository.CursoRepository;
import com.grup14.luterano.response.reporteAsistenciaPerfecta.AsistenciaPerfectaCursoRow;
import com.grup14.luterano.response.reporteAsistenciaPerfecta.AsistenciaPerfectaResponse;
import com.grup14.luterano.service.ReporteAsistenciaPerfectaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteAsistenciaPerfectaServiceImpl implements ReporteAsistenciaPerfectaService {

    private final CicloLectivoRepository cicloRepo;
    private final HistorialCursoRepository historialCursoRepo;
    private final AsistenciaAlumnoRepository asistenciaRepo;
    private final CursoRepository cursoRepo;

    @Override
    @Transactional(readOnly = true)
    public AsistenciaPerfectaResponse listarPorAnio(int anio) {
        LocalDate mid = LocalDate.of(anio, 7, 1);
        var ciclo = cicloRepo.findByFechaDesdeBeforeAndFechaHastaAfter(mid, mid)
                .orElseThrow(() -> new IllegalArgumentException("No hay ciclo lectivo que contenga el año " + anio));

        // historiales abiertos del ciclo (con alumno y curso)
        List<HistorialCurso> hcs = historialCursoRepo.findAbiertosByCiclo(ciclo.getId());

        // Agrupar alumnos por curso
        Map<Long, CursoDto> cursoById = new LinkedHashMap<>();
        Map<Long, List<Alumno>> alumnosPorCurso = new LinkedHashMap<>();

        for (HistorialCurso hc : hcs) {
            Long cId = hc.getCurso().getId();
            cursoById.putIfAbsent(cId, CursoMapper.toDto(hc.getCurso()));
            alumnosPorCurso.computeIfAbsent(cId, k -> new ArrayList<>()).add(hc.getAlumno());
        }

        // Incluir TODOS los cursos del sistema, aunque no tengan historiales/alumnos (lista vacía)
        cursoRepo.findAll().forEach(c -> {
            Long cId = c.getId();
            cursoById.putIfAbsent(cId, CursoMapper.toDto(c));
            alumnosPorCurso.putIfAbsent(cId, new ArrayList<>());
        });

        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);

        // Calcular suma ponderada por alumno
    List<Long> allAlumnoIds = hcs.stream().map(h -> h.getAlumno().getId()).distinct().toList();
        Map<Long, Double> ponderadoPorAlumno = new HashMap<>();
        if (!allAlumnoIds.isEmpty()) {
            for (Object[] row : asistenciaRepo.sumarInasistenciasPorAlumnoEntreFechas(desde, hasta, allAlumnoIds)) {
                if (row == null || row.length < 2) continue;
                Long aId = (Long) row[0];
                Double sum = row[1] instanceof Number n ? n.doubleValue() : 0.0;
                ponderadoPorAlumno.put(aId, sum);
            }
        }

        // Armar filas por curso
        int totalPerfectos = 0;
        List<AsistenciaPerfectaCursoRow> filas = new ArrayList<>();
        for (var entry : alumnosPorCurso.entrySet()) {
            Long cId = entry.getKey();
            List<Alumno> alumnos = entry.getValue();
            CursoDto cursoDto = cursoById.get(cId);

            List<AlumnoLigeroDto> perfectos = alumnos.stream()
                    .filter(a -> ponderadoPorAlumno.getOrDefault(a.getId(), 0.0) == 0.0)
                    .map(a -> AlumnoLigeroDto.builder()
                            .alumnoId(a.getId())
                            .dni(a.getDni())
                            .apellido(a.getApellido())
                            .nombre(a.getNombre())
                            .build())
                    .sorted(Comparator.comparing(AlumnoLigeroDto::getApellido, colEsAr())
                            .thenComparing(AlumnoLigeroDto::getNombre, colEsAr()))
                    .collect(Collectors.toList());

            totalPerfectos += perfectos.size();
            filas.add(AsistenciaPerfectaCursoRow.builder()
                    .curso(cursoDto)
                    .alumnos(perfectos)
                    .totalPerfectos(perfectos.size())
                    .build());
        }

        // Ordenar cursos por año/nivel/división si está disponible en el DTO
        filas.sort(Comparator
                .comparing((AsistenciaPerfectaCursoRow r) -> r.getCurso().getAnio(), Comparator.nullsLast(Integer::compareTo))
                .thenComparing(r -> r.getCurso().getNivel(), Comparator.nullsLast(Enum::compareTo))
                .thenComparing(r -> r.getCurso().getDivision(), Comparator.nullsLast(Enum::compareTo))
        );

        return AsistenciaPerfectaResponse.builder()
                .anio(anio)
                .cursos(filas)
                .totalAlumnosPerfectos(totalPerfectos)
                .code(0)
                .mensaje("OK")
                .build();
    }

    private java.text.Collator colEsAr() {
        var c = java.text.Collator.getInstance(new java.util.Locale.Builder().setLanguage("es").setRegion("AR").build());
        c.setStrength(java.text.Collator.PRIMARY);
        return c;
    }
}
