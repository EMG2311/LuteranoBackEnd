package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.CursoDto;
import com.grup14.luterano.dto.reporteAnual.InasistenciasResumenDto;
import com.grup14.luterano.dto.reporteAnual.MateriaAnualDetalleDto;
import com.grup14.luterano.dto.reporteAnual.ReporteAnualAlumnoDto;
import com.grup14.luterano.dto.reporteNotas.CalificacionesAlumnoResumenDto;
import com.grup14.luterano.dto.reporteNotas.CalificacionesMateriaResumenDto;
import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.HistorialCurso;
import com.grup14.luterano.entities.HistorialMateria;
import com.grup14.luterano.entities.MesaExamenAlumno;
import com.grup14.luterano.entities.enums.EstadoAsistencia;
import com.grup14.luterano.entities.enums.EstadoMateriaAlumno;
import com.grup14.luterano.mappers.CursoMapper;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.response.reporteAnual.ReporteAnualAlumnoResponse;
import com.grup14.luterano.service.NotaFinalService;
import com.grup14.luterano.service.ReporteAnualService;
import com.grup14.luterano.service.ReporteNotasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteAnualServiceImpl implements ReporteAnualService {

    private final AlumnoRepository alumnoRepo;
    private final CicloLectivoRepository cicloRepo;
    private final HistorialCursoRepository historialCursoRepo;
    private final HistorialMateriaRepository historialMateriaRepo;
    private final MesaExamenAlumnoRepository mesaExamenAlumnoRepo;
    private final AsistenciaAlumnoRepository asistenciaAlumnoRepo;
    private final ReporteNotasService reporteNotasService;
    private final NotaFinalService notaFinalService;

    @Override
    @Transactional(readOnly = true)
    public ReporteAnualAlumnoResponse informeAnualAlumno(Long alumnoId, int anio) {
        if (alumnoId == null) throw new IllegalArgumentException("alumnoId es requerido");

        Alumno alumno = alumnoRepo.findById(alumnoId)
                .orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado (id=" + alumnoId + ")"));

        return generarReporteAnual(alumno, anio);
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteAnualAlumnoResponse informeAnualAlumnoPorDni(String dni, int anio) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("DNI es requerido");
        }

        Alumno alumno = alumnoRepo.findByDni(dni)
                .orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado con DNI: " + dni));

        return generarReporteAnual(alumno, anio);
    }

    private ReporteAnualAlumnoResponse generarReporteAnual(Alumno alumno, int anio) {
        LocalDate mid = LocalDate.of(anio, 7, 1);
        var ciclo = cicloRepo.findByFechaDesdeBeforeAndFechaHastaAfter(mid, mid)
                .orElseThrow(() -> new IllegalArgumentException("No hay ciclo lectivo que contenga el año " + anio));

        // Historial del curso vigente en el año
        HistorialCurso hc = historialCursoRepo.findVigenteEnFecha(alumno.getId(), ciclo.getId(), mid)
                .orElse(null);

        // Curso del año: priorizar HistorialCurso del ciclo; si no existe, usar cursoActual del alumno
        CursoDto cursoDto = null;
        if (hc != null && hc.getCurso() != null) {
            cursoDto = CursoMapper.toDto(hc.getCurso());
        } else if (alumno.getCursoActual() != null) {
            cursoDto = CursoMapper.toDto(alumno.getCursoActual());
        }

        // 1) Resumen de notas por materia del servicio existente
        CalificacionesAlumnoResumenDto califResumen = Optional.ofNullable(
                reporteNotasService.listarResumenPorAnio(alumno.getId(), anio)
        ).map(r -> r.getCalificacionesAlumnoResumenDto()).orElse(null);

        Map<Long, CalificacionesMateriaResumenDto> porMateria = new HashMap<>();
        if (califResumen != null && califResumen.getMaterias() != null) {
            porMateria = califResumen.getMaterias().stream()
                    .collect(Collectors.toMap(CalificacionesMateriaResumenDto::getMateriaId, x -> x));
        }

        // 2) Notas finales de mesa en el año (coloquio y examen)
        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);
        List<MesaExamenAlumno> finales = mesaExamenAlumnoRepo
                .findByAlumno_IdAndMesaExamen_FechaBetween(alumno.getId(), desde, hasta);

        Map<Long, MesaExamenAlumno> ultimoFinalPorMateria = new HashMap<>();
        Map<Long, MesaExamenAlumno> ultimoColoquisPorMateria = new HashMap<>();
        Map<Long, MesaExamenAlumno> ultimoExamenPorMateria = new HashMap<>();

        for (MesaExamenAlumno mea : finales) {
            var materia = mea.getMesaExamen().getMateriaCurso().getMateria();
            Long mId = materia.getId();

            // Final general (última mesa del año para esa materia)
            MesaExamenAlumno current = ultimoFinalPorMateria.get(mId);
            if (current == null || (current.getMesaExamen().getFecha() != null &&
                    mea.getMesaExamen().getFecha() != null &&
                    mea.getMesaExamen().getFecha().isAfter(current.getMesaExamen().getFecha()))) {
                ultimoFinalPorMateria.put(mId, mea);
            }

            // Separar por condición
            if (mea.getCondicionRinde() != null) {
                switch (mea.getCondicionRinde()) {
                    case COLOQUIO -> {
                        MesaExamenAlumno currentCo = ultimoColoquisPorMateria.get(mId);
                        if (currentCo == null || (currentCo.getMesaExamen().getFecha() != null &&
                                mea.getMesaExamen().getFecha() != null &&
                                mea.getMesaExamen().getFecha().isAfter(currentCo.getMesaExamen().getFecha()))) {
                            ultimoColoquisPorMateria.put(mId, mea);
                        }
                    }
                    case EXAMEN -> {
                        MesaExamenAlumno currentEx = ultimoExamenPorMateria.get(mId);
                        if (currentEx == null || (currentEx.getMesaExamen().getFecha() != null &&
                                mea.getMesaExamen().getFecha() != null &&
                                mea.getMesaExamen().getFecha().isAfter(currentEx.getMesaExamen().getFecha()))) {
                            ultimoExamenPorMateria.put(mId, mea);
                        }
                    }
                }
            }
        }

        // 3) Previas: materias DESAPROBADAS en años ANTERIORES al año consultado
        //    (si luego aprueban por mesa, tu lógica de promoción/mesa ya debería
        //     haber actualizado el estado a APROBADA y por lo tanto dejará de ser previa)
        Set<Long> materiasPreviasSet = new LinkedHashSet<>();

        List<HistorialCurso> historialCursos = historialCursoRepo.findHistorialCompletoByAlumnoId(alumno.getId());
        for (HistorialCurso hcAnterior : historialCursos) {
            int anioCurso = hcAnterior.getCicloLectivo().getFechaDesde().getYear();

            // Solo años anteriores al que se está consultando
            if (anioCurso >= anio) continue;

            List<HistorialMateria> hmsAnio = historialMateriaRepo.findAllByHistorialCursoId(hcAnterior.getId());
            for (HistorialMateria hm : hmsAnio) {
                if (hm.getEstado() == EstadoMateriaAlumno.DESAPROBADA
                        && hm.getMateriaCurso() != null
                        && hm.getMateriaCurso().getMateria() != null) {

                    Long materiaId = hm.getMateriaCurso().getMateria().getId();
                    materiasPreviasSet.add(materiaId);
                }
            }
        }

        // 4) Inasistencias del año
        Double ponderado = 0.0;
        var ponderados = asistenciaAlumnoRepo
                .sumarInasistenciasPorAlumnoEntreFechas(desde, hasta, java.util.List.of(alumno.getId()));
        if (ponderados != null && !((List<?>) ponderados).isEmpty()) {
            Object[] row = (Object[]) ((List<?>) ponderados).get(0);
            if (row != null && row.length > 1 && row[1] instanceof Number n) {
                ponderado = n.doubleValue();
            } else if (row != null && row.length > 0 && row[0] instanceof Number n2) {
                // Dependiendo del proveedor JPA puede devolver solo un valor
                ponderado = n2.doubleValue();
            }
        }

        long ausentes = 0, tardes = 0, justificados = 0, conLicencia = 0, retiros = 0;
        for (Object[] r : asistenciaAlumnoRepo.contarPorEstadoEntreFechas(alumno.getId(), desde, hasta)) {
            if (r == null || r.length < 2) continue;
            EstadoAsistencia estado = (EstadoAsistencia) r[0];
            long count = ((Number) r[1]).longValue();
            switch (estado) {
                case AUSENTE -> ausentes += count;
                case TARDE -> tardes += count;
                case JUSTIFICADO -> justificados += count;
                case CON_LICENCIA -> conLicencia += count;
                case RETIRO -> retiros += count;
                default -> { }
            }
        }

        InasistenciasResumenDto inasDto = InasistenciasResumenDto.builder()
                .ponderado(ponderado)
                .ausentes(ausentes)
                .tardes(tardes)
                .justificados(justificados)
                .conLicencia(conLicencia)
                .retiros(retiros)
                .build();

        // 5) Armar lista de materias con mezcla de datos (resumen + finales + estado materia)

        // HistorialMateria del alumno para el año consultado
        List<HistorialMateria> hms = new ArrayList<>();
        if (hc != null) {
            hms = historialMateriaRepo.findAllByHistorialCursoId(hc.getId());
        }

        Map<Long, MateriaAnualDetalleDto> materias = new LinkedHashMap<>();
        if (porMateria != null) {
            for (var entry : porMateria.entrySet()) {
                Long mId = entry.getKey();
                CalificacionesMateriaResumenDto r = entry.getValue();
                MesaExamenAlumno mea = ultimoFinalPorMateria.get(mId);
                MesaExamenAlumno meaColoquio = ultimoColoquisPorMateria.get(mId);
                MesaExamenAlumno meaExamen = ultimoExamenPorMateria.get(mId);

                String estadoFinal = mea == null ? null :
                        (mea.getNotaFinal() != null && mea.getNotaFinal() >= 6 ? "APROBADO" : "DESAPROBADO");
                Integer notaColoquio = meaColoquio != null ? meaColoquio.getNotaFinal() : null;
                Integer notaExamen = meaExamen != null ? meaExamen.getNotaFinal() : null;

                String estadoMateria = null;
                if (!hms.isEmpty()) {
                    estadoMateria = hms.stream()
                            .filter(x -> x.getMateriaCurso().getMateria().getId().equals(mId))
                            .map(HistorialMateria::getEstado)
                            .map(EstadoMateriaAlumno::name)
                            .findFirst().orElse(null);
                }

                materias.put(mId, MateriaAnualDetalleDto.builder()
                        .materiaId(mId)
                        .materiaNombre(r.getMateriaNombre())
                        .e1Notas(r.getE1Notas())
                        .e2Notas(r.getE2Notas())
                        .e1(r.getE1())
                        .e2(r.getE2())
                        .pg(r.getPg())
                        .estado(r.getEstado())
                        .co(notaColoquio)
                        .ex(notaExamen)
                        .notaFinal(notaFinalService.calcularNotaFinal(alumno.getId(), mId, anio))
                        .estadoFinal(estadoFinal)
                        .estadoMateria(estadoMateria)
                        .build());
            }
        }

        // Si hubo finales de materias que no aparecen en promedios (sin cursada), agregarlas
        for (var entry : ultimoFinalPorMateria.entrySet()) {
            Long mId = entry.getKey();
            if (materias.containsKey(mId)) continue;
            MesaExamenAlumno mea = entry.getValue();
            MesaExamenAlumno meaColoquio = ultimoColoquisPorMateria.get(mId);
            MesaExamenAlumno meaExamen = ultimoExamenPorMateria.get(mId);

            String nombre = mea.getMesaExamen().getMateriaCurso().getMateria().getNombre();
            String estadoFinal = mea.getNotaFinal() != null && mea.getNotaFinal() >= 6 ? "APROBADO" : "DESAPROBADO";
            Integer notaColoquio = meaColoquio != null ? meaColoquio.getNotaFinal() : null;
            Integer notaExamen = meaExamen != null ? meaExamen.getNotaFinal() : null;

            String estadoMateria = null;
            if (!hms.isEmpty()) {
                estadoMateria = hms.stream()
                        .filter(x -> x.getMateriaCurso().getMateria().getId().equals(mId))
                        .map(HistorialMateria::getEstado)
                        .map(EstadoMateriaAlumno::name)
                        .findFirst().orElse(null);
            }

            materias.put(mId, MateriaAnualDetalleDto.builder()
                    .materiaId(mId)
                    .materiaNombre(nombre)
                    .e1Notas(new Integer[]{null, null, null, null})
                    .e2Notas(new Integer[]{null, null, null, null})
                    .e1(null).e2(null).pg(null)
                    .estado(null)
                    .co(notaColoquio)
                    .ex(notaExamen)
                    .notaFinal(notaFinalService.calcularNotaFinal(alumno.getId(), mId, anio))
                    .estadoFinal(estadoFinal)
                    .estadoMateria(estadoMateria)
                    .build());
        }

        // Ordenar materias por nombre
        var coll = java.text.Collator.getInstance(
                new java.util.Locale.Builder().setLanguage("es").setRegion("AR").build());
        coll.setStrength(java.text.Collator.PRIMARY);
        List<MateriaAnualDetalleDto> materiasList = new ArrayList<>(materias.values());
        materiasList.sort(java.util.Comparator.comparing(MateriaAnualDetalleDto::getMateriaNombre,
                java.util.Comparator.nullsLast(coll)));

        // Fallback: si NO hay historial de ese año pero sí promedios,
        // inferir previas desde PG / nota final del año consultado.
        if (hms.isEmpty() && porMateria != null) {
            for (var entry : porMateria.entrySet()) {
                Long mId = entry.getKey();
                var r = entry.getValue();
                boolean desaprobadoPorPromedio = r.getPg() != null && r.getPg() < 6.0;
                MesaExamenAlumno mea = ultimoFinalPorMateria.get(mId);
                boolean desaprobadoPorFinal = mea != null && mea.getNotaFinal() != null && mea.getNotaFinal() < 6;
                if (desaprobadoPorPromedio || desaprobadoPorFinal) {
                    materiasPreviasSet.add(mId);
                }
            }
        }

        // 6) Promedio final del curso
        BigDecimal promedioFinalCurso;
        if (hc != null && hc.getPromedio() != null) {
            promedioFinalCurso = hc.getPromedio();
        } else {
            promedioFinalCurso = calcularPromedioCursoDesdeMaterias(
                    alumno.getId(), anio, ultimoFinalPorMateria, porMateria);
        }

        // Legajo: por ahora asumimos DNI como legajo
        String legajo = alumno.getDni();

        ReporteAnualAlumnoDto dto = ReporteAnualAlumnoDto.builder()
                .alumnoId(alumno.getId())
                .anio(anio)
                .dni(alumno.getDni())
                .legajo(legajo)
                .apellido(alumno.getApellido())
                .nombre(alumno.getNombre())
                .curso(cursoDto)
                .materias(materiasList)
                .promedioFinalCurso(promedioFinalCurso)
                .inasistencias(inasDto)
                .materiasPreviasIds(new ArrayList<>(materiasPreviasSet))
                .build();

        return ReporteAnualAlumnoResponse.builder()
                .data(dto)
                .code(0)
                .mensaje("OK")
                .build();
    }

    private BigDecimal calcularPromedioCursoDesdeMaterias(Long alumnoId, int anio,
                                                          Map<Long, MesaExamenAlumno> finales,
                                                          Map<Long, CalificacionesMateriaResumenDto> porMateria) {
        if (porMateria == null || porMateria.isEmpty()) return null;
        double suma = 0.0;
        int n = 0;
        for (var entry : porMateria.entrySet()) {
            Long mId = entry.getKey();
            Integer notaFinal = notaFinalService.calcularNotaFinal(alumnoId, mId, anio);
            if (notaFinal != null) {
                suma += notaFinal;
                n++;
            }
        }
        if (n == 0) return null;
        double promedio = Math.round((suma / n) * 10.0) / 10.0;
        return BigDecimal.valueOf(promedio).setScale(1, RoundingMode.HALF_UP);
    }
}
