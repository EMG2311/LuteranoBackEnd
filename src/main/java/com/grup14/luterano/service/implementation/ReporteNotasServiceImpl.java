package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.reporteNotas.CalificacionesAlumnoResumenDto;
import com.grup14.luterano.dto.reporteNotas.CalificacionesMateriaResumenDto;
import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.Calificacion;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.HistorialCurso;
import com.grup14.luterano.entities.MesaExamenAlumno;
import com.grup14.luterano.exeptions.ReporteNotasException;
import com.grup14.luterano.mappers.CursoMapper;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.response.reporteNotas.CalificacionesAlumnoAnioResponse;
import com.grup14.luterano.response.reporteNotas.CalificacionesCursoAnioResponse;
import com.grup14.luterano.service.ReporteNotasService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class ReporteNotasServiceImpl implements ReporteNotasService {

    private final CalificacionRepository calificacionRepo;
    private final AlumnoRepository alumnoRepo;
    private final CursoRepository cursoRepository;
    private final CicloLectivoRepository cicloLectivoRepository;
    private final HistorialCursoRepository historialCursoRepo;
    private final MesaExamenAlumnoRepository mesaExamenAlumnoRepo;


    @Transactional(readOnly = true)
    public CalificacionesAlumnoAnioResponse listarResumenPorAnio(Long alumnoId, int anio) {
        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);
        Alumno alumno = alumnoRepo.findById(alumnoId).orElseThrow(() -> new ReporteNotasException("No existe el alumno con ese id"));
        List<Calificacion> califs = calificacionRepo.findByAlumnoAndAnio(alumnoId, desde, hasta);

        Map<Long, CalificacionesMateriaResumenDto> map = new LinkedHashMap<>();

        for (Calificacion c : califs) {
            Long materiaId = c.getHistorialMateria().getMateriaCurso().getMateria().getId();
            String materiaNombre = c.getHistorialMateria().getMateriaCurso().getMateria().getNombre();

            CalificacionesMateriaResumenDto row = map.computeIfAbsent(materiaId, id ->
                    CalificacionesMateriaResumenDto.builder()
                            .materiaId(id)
                            .materiaNombre(materiaNombre)
                            .e1Notas(new Integer[]{null, null, null, null})
                            .e2Notas(new Integer[]{null, null, null, null})
                            .build()
            );

            Integer n = c.getNumeroNota();
            if (n == null || n < 1 || n > 4) continue;
            int idx = n - 1; // 0..3

            if (c.getEtapa() == 1) row.getE1Notas()[idx] = c.getNota();
            else if (c.getEtapa() == 2) row.getE2Notas()[idx] = c.getNota();
        }

        for (CalificacionesMateriaResumenDto r : map.values()) {
            Double e1 = promedio(r.getE1Notas());
            Double e2 = promedio(r.getE2Notas());
            Double pg = promedioGeneral(e1, e2);
            r.setE1(e1);
            r.setE2(e2);
            r.setPg(pg);
            
            // NUEVA LÓGICA: Buscar nota de mesa de examen (coloquio/examen final)
            List<MesaExamenAlumno> mesas = mesaExamenAlumnoRepo
                .findByAlumno_IdAndMesaExamen_FechaBetween(alumnoId, desde, hasta);
            
            // Filtrar por materia y obtener la más reciente
            MesaExamenAlumno mesaMasReciente = mesas.stream()
                .filter(mea -> mea.getMesaExamen().getMateriaCurso().getMateria().getId().equals(r.getMateriaId()))
                .filter(mea -> mea.getNotaFinal() != null)
                .max((a, b) -> {
                    LocalDate fechaA = a.getMesaExamen().getFecha();
                    LocalDate fechaB = b.getMesaExamen().getFecha();
                    if (fechaA == null && fechaB == null) return 0;
                    if (fechaA == null) return -1;
                    if (fechaB == null) return 1;
                    return fechaA.compareTo(fechaB);
                })
                .orElse(null);
                
            // Determinar si es coloquio o examen final
            if (mesaMasReciente != null) {
                boolean apr1 = e1 != null && e1 >= 6.0;
                boolean apr2 = e2 != null && e2 >= 6.0;
                
                // Si aprobó solo una etapa → COLOQUIO
                // Si desaprobó ambas etapas → EXAMEN FINAL
                if (apr1 ^ apr2) {  // XOR - solo una etapa aprobada
                    r.setCo(mesaMasReciente.getNotaFinal());
                    r.setEx(null);
                } else {  // ambas desaprobadas
                    r.setCo(null);
                    r.setEx(mesaMasReciente.getNotaFinal());
                }
            } else {
                r.setCo(null);
                r.setEx(null);
            }
            
            // Calcular PFA (Promedio Final Anual = Nota Final)
            // Si tiene mesa de examen, usar esa nota; si no, usar PG redondeado
            Double pfa;
            if (mesaMasReciente != null) {
                pfa = mesaMasReciente.getNotaFinal().doubleValue();
            } else if (pg != null) {
                pfa = (double) Math.round(pg);
            } else {
                pfa = null;
            }
            r.setPfa(pfa);
            
            // Calcular ESTADO basado en la nota final (PFA), no en el PG
            if (pfa != null && pfa >= 6.0) {
                r.setEstado("Aprobado");
            } else {
                r.setEstado("Desaprobado");
            }
        }

        var coll = java.text.Collator.getInstance(new java.util.Locale("es", "AR"));
        coll.setStrength(java.text.Collator.PRIMARY);

        List<CalificacionesMateriaResumenDto> materias = new ArrayList<>(map.values());
        materias.sort(
                java.util.Comparator.comparing(
                        CalificacionesMateriaResumenDto::getMateriaNombre,
                        java.util.Comparator.nullsLast(coll)
                )
        );

        CalificacionesAlumnoResumenDto resumen = CalificacionesAlumnoResumenDto.builder()
                .alumnoId(alumnoId)
                .nombre(alumno.getNombre())
                .apellido(alumno.getApellido())
                .dni(alumno.getDni())
                .anio(anio)
                .materias(materias)
                .build();

        return CalificacionesAlumnoAnioResponse.builder()
                .calificacionesAlumnoResumenDto(resumen)
                .code(0)
                .mensaje("OK")
                .build();
    }


    @Transactional(readOnly = true)
    public CalificacionesCursoAnioResponse listarResumenCursoPorAnio(Long cursoId, int anio) {
        if (cursoId == null) throw new ReporteNotasException("cursoId es requerido");

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ReporteNotasException("Curso no encontrado (id=" + cursoId + ")"));

        LocalDate mid = LocalDate.of(anio, 7, 1);
        Long cicloId = cicloLectivoRepository
                .findByFechaDesdeBeforeAndFechaHastaAfter(mid, mid)
                .orElseThrow(() -> new ReporteNotasException("No hay ciclo lectivo que contenga el año " + anio))
                .getId();

        List<HistorialCurso> hcs = historialCursoRepo.findAbiertosByCursoAndCiclo(cursoId, cicloId);
        List<Alumno> alumnos = hcs.stream().map(HistorialCurso::getAlumno).toList();

        if (alumnos.isEmpty()) {
            return CalificacionesCursoAnioResponse.builder()
                    .curso(CursoMapper.toDto(curso))
                    .anio(anio)
                    .alumnos(List.of())
                    .total(0)
                    .code(0)
                    .mensaje("OK")
                    .build();
        }

        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);
        List<Long> alumnoIds = alumnos.stream().map(Alumno::getId).toList();
        List<Calificacion> califs = calificacionRepo.findByAlumnosAndAnio(alumnoIds, desde, hasta);


        Map<Long, Map<Long, CalificacionesMateriaResumenDto>> porAlumno = new LinkedHashMap<>();

        for (Calificacion c : califs) {
            Long aId = c.getHistorialMateria().getHistorialCurso().getAlumno().getId();
            var materia = c.getHistorialMateria().getMateriaCurso().getMateria();
            Long mId = materia.getId();
            String mNombre = materia.getNombre();

            Map<Long, CalificacionesMateriaResumenDto> porMateria =
                    porAlumno.computeIfAbsent(aId, k -> new LinkedHashMap<>());

            CalificacionesMateriaResumenDto row = porMateria.computeIfAbsent(mId, id ->
                    CalificacionesMateriaResumenDto.builder()
                            .materiaId(id)
                            .materiaNombre(mNombre)
                            .e1Notas(new Integer[]{null, null, null, null})
                            .e2Notas(new Integer[]{null, null, null, null})
                            .build()
            );

            Integer n = c.getNumeroNota();
            if (n == null || n < 1 || n > 4) continue;

            int idx = n - 1;
            if (c.getEtapa() == 1) row.getE1Notas()[idx] = c.getNota();
            else if (c.getEtapa() == 2) row.getE2Notas()[idx] = c.getNota();
        }

        var coll = java.text.Collator.getInstance(new java.util.Locale("es", "AR"));
        coll.setStrength(java.text.Collator.PRIMARY);

        List<CalificacionesAlumnoResumenDto> filas = new ArrayList<>();
        for (Alumno a : alumnos) {
            Map<Long, CalificacionesMateriaResumenDto> materiasMap =
                    porAlumno.getOrDefault(a.getId(), Map.of());

            for (CalificacionesMateriaResumenDto r : materiasMap.values()) {
                Double e1 = promedio(r.getE1Notas());
                Double e2 = promedio(r.getE2Notas());
                Double pg = promedioGeneral(e1, e2);
                r.setE1(e1);
                r.setE2(e2);
                r.setPg(pg);
                r.setEstado(pg != null && pg >= 6.0 ? "Aprobado" : "Desaprobado");
            }


            List<CalificacionesMateriaResumenDto> materias = new ArrayList<>(materiasMap.values());
            materias.sort(Comparator.comparing(CalificacionesMateriaResumenDto::getMateriaNombre,
                    Comparator.nullsLast(coll)));

            filas.add(CalificacionesAlumnoResumenDto.builder()
                    .alumnoId(a.getId())
                    .dni(a.getDni())
                    .apellido(a.getApellido())
                    .nombre(a.getNombre())
                    .materias(materias)
                    .build());
        }

        filas.sort(Comparator
                .comparing(CalificacionesAlumnoResumenDto::getApellido, coll)
                .thenComparing(CalificacionesAlumnoResumenDto::getNombre, coll));

        return CalificacionesCursoAnioResponse.builder()
                .curso(CursoMapper.toDto(curso))
                .anio(anio)
                .alumnos(filas)
                .total(filas.size())
                .code(0)
                .mensaje("OK")
                .build();
    }


    private static Double promedio(Integer[] notas) {
        int suma = 0, n = 0;
        for (Integer v : notas)
            if (v != null) {
                suma += v;
                n++;
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
