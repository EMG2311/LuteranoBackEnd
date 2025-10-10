package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.calificaciones.CalificacionesAlumnoResumenDto;
import com.grup14.luterano.dto.calificaciones.CalificacionesMateriaResumenDto;
import com.grup14.luterano.entities.*;
import com.grup14.luterano.exeptions.CalificacionException;
import com.grup14.luterano.mappers.CalificacionMapper;
import com.grup14.luterano.mappers.CursoMapper;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.request.calificacion.CalificacionRequest;
import com.grup14.luterano.request.calificacion.CalificacionUpdateRequest;
import com.grup14.luterano.response.calificaciones.*;
import com.grup14.luterano.service.CalificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CalificacionServiceImpl implements CalificacionService {

    private final AlumnoRepository alumnoRepo;
    private final MateriaCursoRepository materiaCursoRepo;
    private final CicloLectivoRepository cicloRepo;
    private final HistorialCursoRepository historialCursoRepo;
    private final HistorialMateriaRepository historialMateriaRepo;
    private final CalificacionRepository calificacionRepo;
    private final CursoRepository cursoRepository;
    private final CicloLectivoRepository cicloLectivoRepository;


    @Transactional
    public CalificacionResponse crearCalificacion(CalificacionRequest req) {
        var alumno = alumnoRepo.findById(req.getAlumnoId())
                .orElseThrow(() -> new CalificacionException("Alumno no encontrado"));

        LocalDate fechaNota = (req.getFecha() != null) ? req.getFecha() : LocalDate.now();

        var ciclo = cicloRepo.findByFechaDesdeLessThanEqualAndFechaHastaGreaterThanEqual(fechaNota, fechaNota)
                .orElseThrow(() -> new CalificacionException("No hay ciclo lectivo para la fecha " + fechaNota));

        var hc = historialCursoRepo.findVigenteEnFecha(alumno.getId(), ciclo.getId(), fechaNota)
                .orElseThrow(() -> new CalificacionException("El alumno no cursaba en la fecha " + fechaNota));

        var mc = materiaCursoRepo.findByMateriaIdAndCursoId(req.getMateriaId(),hc.getCurso().getId())
                .orElseThrow(() -> new CalificacionException("El curso del alumno no dicta esa materia"));

        var hm = historialMateriaRepo.findByHistorialCurso_IdAndMateriaCurso_Id(hc.getId(), mc.getId())
                .orElseGet(() -> historialMateriaRepo.save(
                        HistorialMateria.builder().historialCurso(hc).materiaCurso(mc).build()
                ));

        if (calificacionRepo.existsByHistorialMateria_IdAndEtapaAndNumeroNota(hm.getId(), req.getEtapa(), req.getNumeroNota())) {
            throw new CalificacionException("Ya existe la nota " + req.getNumeroNota() + " en etapa " + req.getEtapa());
        }

        var cal = Calificacion.builder()
                .historialMateria(hm)
                .etapa(req.getEtapa())
                .numeroNota(req.getNumeroNota())
                .nota(req.getNota())
                .fecha(fechaNota)
                .build();

        cal = calificacionRepo.save(cal);

        return CalificacionResponse.builder()
                .calificacion(CalificacionMapper.toDto(cal))
                .code(0)
                .mensaje("Se registró correctamente la calificación")
                .build();
    }


    @Transactional(readOnly = true)
    public CalificacionResponse obtener(Long alumnoId, Long materiaId, Long califId) {

        var cal = calificacionRepo.findOwned(alumnoId, materiaId, califId)
                .orElseThrow(() -> new CalificacionException("Calificación no encontrada para ese alumno y materia"));
        return CalificacionResponse.builder()
                .calificacion(CalificacionMapper.toDto(cal))
                .code(0)
                .mensaje("Ok")
                .build();

    }

    @Transactional
    public CalificacionResponse actualizar(CalificacionUpdateRequest req) {
        var cal = calificacionRepo.findOwned(req.getAlumnoId(), req.getMateriaId(), req.getCalifId())
                .orElseThrow(() -> new CalificacionException(
                        "Calificación no encontrada para ese alumno y materia"));
        if(req.getNota()!=null){
            cal.setNota(req.getNota());
        }

        if (req.getFecha() != null) {
            cal.setFecha(req.getFecha());
        }

        cal = calificacionRepo.save(cal);

        return CalificacionResponse.builder()
                .calificacion(CalificacionMapper.toDto(cal))
                .code(0)
                .mensaje("Calificación actualizada correctamente")
                .build();
    }

    @Transactional
    public CalificacionResponse eliminar(Long alumnoId, Long materiaId, Long califId) {
        var cal = calificacionRepo.findOwned(alumnoId, materiaId, califId)
                .orElseThrow(() -> new CalificacionException("Calificación no encontrada para ese alumno y materia"));

        calificacionRepo.delete(cal);

        return CalificacionResponse.builder()
                .calificacion(null)
                .code(0)
                .mensaje("Calificación eliminada correctamente")
                .build();
    }

    @Transactional(readOnly = true)
    public CalificacionListResponse listarPorAnio(Long alumnoId, int anio) {
        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);

        var list = calificacionRepo.findByAlumnoAndAnio(alumnoId, desde, hasta)
                .stream().map(CalificacionMapper::toDto).toList();

        return CalificacionListResponse.builder()
                .calificaciones(list)
                .code(0)
                .mensaje("Ok")
                .build();
    }

    @Transactional(readOnly = true)
    public CalificacionListResponse listarPorAnioYEtapa(Long alumnoId, int anio, int etapa) {
        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);

        var list = calificacionRepo.findByAlumnoAndAnioAndEtapa(alumnoId, etapa, desde, hasta)
                .stream().map(CalificacionMapper::toDto).toList();

        return CalificacionListResponse.builder()
                .calificaciones(list)
                .code(0)
                .mensaje("Ok")
                .build();
    }

    @Transactional(readOnly = true)
    public CalificacionListResponse listarPorMateria(Long alumnoId, Long materiaId) {

        boolean cursaOMaterializoHM =
                historialMateriaRepo.existsByHistorialCurso_Alumno_IdAndMateriaCurso_Materia_Id(alumnoId, materiaId)
                        || historialCursoRepo.existsAlumnoCursoMateria(alumnoId, materiaId);

        if (!cursaOMaterializoHM) {
            throw new CalificacionException("El alumno no cursó esa materia.");
        }

        var list = calificacionRepo.findByAlumnoAndMateria(alumnoId, materiaId)
                .stream().map(CalificacionMapper::toDto).toList();

        return CalificacionListResponse.builder()
                .calificaciones(list)
                .code(0)
                .mensaje("Ok")
                .build();
    }



    @Transactional(readOnly = true)
    public CalificacionesAlumnoAnioResponse listarResumenPorAnio(Long alumnoId, int anio) {
        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);
        Alumno alumno= alumnoRepo.findById(alumnoId).orElseThrow(()-> new CalificacionException("No existe el alumno con ese id"));
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
            r.setEstado(pg != null && pg >= 6.0 ? "Aprobado" : "Desaprobado");
        }

        var coll = java.text.Collator.getInstance(new java.util.Locale("es","AR"));
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
        if (cursoId == null) throw new IllegalArgumentException("cursoId es requerido");

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado (id=" + cursoId + ")"));

        LocalDate mid = LocalDate.of(anio, 7, 1);
        Long cicloId = cicloLectivoRepository
                .findByFechaDesdeBeforeAndFechaHastaAfter(mid, mid)
                .orElseThrow(() -> new IllegalStateException("No hay ciclo lectivo que contenga el año " + anio))
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

        // Traer TODAS las calificaciones del año para estos alumnos de una vez (evita N+1)
        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);
        List<Long> alumnoIds = alumnos.stream().map(Alumno::getId).toList();
        List<Calificacion> califs = calificacionRepo.findByAlumnosAndAnio(alumnoIds, desde, hasta);

        // Estructura: alumnoId -> (materiaId -> resumen)
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
                            .e1Notas(new Integer[]{null,null,null,null})
                            .e2Notas(new Integer[]{null,null,null,null})
                            .build()
            );

            Integer n = c.getNumeroNota();
            if (n == null || n < 1 || n > 4) continue; // invalida -> ignorar (o lanzar excepción)

            int idx = n - 1; // 0..3
            if (c.getEtapa() == 1) row.getE1Notas()[idx] = c.getNota();
            else if (c.getEtapa() == 2) row.getE2Notas()[idx] = c.getNota();
        }

        // Armar listado por alumno con cálculo de E1/E2/PG/Estado y ordenamientos
        var coll = java.text.Collator.getInstance(new java.util.Locale("es", "AR"));
        coll.setStrength(java.text.Collator.PRIMARY);

        List<CalificacionesAlumnoResumenDto> filas = new ArrayList<>();
        for (Alumno a : alumnos) {
            Map<Long, CalificacionesMateriaResumenDto> materiasMap =
                    porAlumno.getOrDefault(a.getId(), Map.of());

            // calcular promedios y estado por materia
            for (CalificacionesMateriaResumenDto r : materiasMap.values()) {
                Double e1 = promedio(r.getE1Notas());
                Double e2 = promedio(r.getE2Notas());
                Double pg = promedioGeneral(e1, e2);
                r.setE1(e1);
                r.setE2(e2);
                r.setPg(pg);
                r.setEstado(pg != null && pg >= 6.0 ? "Aprobado" : "Desaprobado");
            }

            // ordenar materias por nombre
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
        for (Integer v : notas) if (v != null) { suma += v; n++; }
        if (n == 0) return null;
        return redondear1((double) suma / n);
    }
    private static Double promedioGeneral(Double e1, Double e2) {
        if (e1 == null && e2 == null) return null;
        if (e1 == null) return e2;
        if (e2 == null) return e1;
        return redondear1((e1 + e2) / 2.0);
    }
    private static Double redondear1(double v) { return Math.round(v * 10.0) / 10.0; }
}