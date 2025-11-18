package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.Calificacion;
import com.grup14.luterano.entities.CicloLectivo;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.HistorialCurso;
import com.grup14.luterano.entities.HistorialMateria;
import com.grup14.luterano.entities.MateriaCurso;
import com.grup14.luterano.entities.enums.Nivel;
import com.grup14.luterano.exeptions.CalificacionException;
import com.grup14.luterano.mappers.NivelMapper;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.response.imports.ImportResultResponse;
import com.grup14.luterano.service.CidiNotasImportService;
import com.grup14.luterano.utils.CsvUtils;
import com.grup14.luterano.utils.CursoResolver;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.InputStream;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CidiNotasImportServiceImpl implements CidiNotasImportService {

    private static final Logger log = LoggerFactory.getLogger(CidiNotasImportServiceImpl.class);

    private final AlumnoRepository alumnoRepository;
    private final CursoRepository cursoRepository;
    private final HistorialCursoRepository historialCursoRepository;
    private final HistorialMateriaRepository historialMateriaRepository;
    private final MateriaCursoRepository materiaCursoRepository;
    private final CalificacionRepository calificacionRepository;
    private final CicloLectivoRepository cicloLectivoRepository;
    private final PlatformTransactionManager txManager;

    @Override
    public ImportResultResponse importNotas(InputStream in, boolean dryRun, String charsetOpt) {
        ImportResultResponse res = new ImportResultResponse();
        List<String> errors = new ArrayList<>();
        res.setErrors(errors);

        int row = 1, inserted = 0, updated = 0, skipped = 0;

        try (CSVParser parser = CsvUtils.createParser(in,
                (charsetOpt != null && !charsetOpt.isBlank()) ? charsetOpt : "UTF-8")) {

            // ==== LOG de encabezados crudos ====
            Set<String> rawHeaders = parser.getHeaderMap().keySet();
            log.info("[CIDI-NOTAS] Headers crudos CSV: {}", rawHeaders);

            // log cada header con su canon y sus códigos hex
            for (String h : rawHeaders) {
                log.info("[CIDI-NOTAS] Header raw='{}' canon='{}' hex='{}'",
                        h,
                        canonHeader(h),
                        toHex(h)
                );
            }

            // ==== Validar encabezados con canonicalización robusta ====
            Set<String> headersCanon = rawHeaders.stream()
                    .map(CidiNotasImportServiceImpl::canonHeader)
                    .collect(Collectors.toSet());

            log.info("[CIDI-NOTAS] Headers canon: {}", headersCanon);

            List<String> faltantes = new ArrayList<>();

            // Grado/Año
            if (!containsHeader(headersCanon,
                    "Grado/Año", "grado/ano", "Grado Ano", "Grado Año")) {
                faltantes.add("Grado/Año");
            }

            // División
            if (!containsHeader(headersCanon,
                    "División", "division", "División ")) {
                faltantes.add("División");
            }

            // Plan de Estu.
            if (!containsHeader(headersCanon,
                    "Plan de Estu.", "Plan de Estudio", "Plan Estu", "Plan Est.")) {
                faltantes.add("Plan de Estu.");
            }

            // N° Documento (DNI)
            if (!containsHeader(headersCanon,
                    "N° Documento", "Nº Documento", "Nro. Docum.", "Nro Documento", "DNI")) {
                faltantes.add("N° Documento");
            }

            // Apellido
            if (!containsHeader(headersCanon, "Apellido")) {
                faltantes.add("Apellido");
            }

            // Nombre
            if (!containsHeader(headersCanon, "Nombre")) {
                faltantes.add("Nombre");
            }

            // Fecha Nacimiento
            if (!containsHeader(headersCanon,
                    "Fecha Nacimiento", "F. Nacimiento", "Fecha nac.", "Fecha Nac")) {
                faltantes.add("Fecha Nacimiento");
            }

            // Espacio Curricular
            if (!containsHeader(headersCanon,
                    "Espacio Curricular", "Materia", "Asignatura")) {
                faltantes.add("Espacio Curricular");
            }

            if (!faltantes.isEmpty()) {
                log.warn("[CIDI-NOTAS] Encabezados requeridos faltantes: {}. Headers canon vistos: {}",
                        faltantes, headersCanon);
                errors.add("Faltan columnas requeridas: " + String.join(", ", faltantes));
                res.setTotalRows(0);
                res.setInserted(0);
                res.setUpdated(0);
                res.setSkipped(0);
                return res;
            }

            // ==== Transacción por fila ====
            TransactionTemplate newTx = new TransactionTemplate(txManager);
            newTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            LocalDate hoy = LocalDate.now();
            CicloLectivo cicloActual = cicloLectivoRepository
                    .findByFechaDesdeBeforeAndFechaHastaAfter(hoy, hoy)
                    .orElseThrow(() -> new IllegalStateException("No hay ciclo lectivo activo."));

            for (CSVRecord rec : parser) {
                row++;
                try {
                    final int currentRow = row;
                    UpsertCount filaCount = newTx.execute(status ->
                            procesarFila(rec, cicloActual, hoy, dryRun, currentRow)
                    );

                    if (filaCount != null) {
                        inserted += filaCount.inserted();
                        updated += filaCount.updated();
                    }

                } catch (Exception exRow) {
                    skipped++;
                    errors.add("Fila " + row + ": " + exRow.getMessage());
                    log.warn("[CIDI-NOTAS] Error en fila {}: {}", row, exRow.getMessage(), exRow);
                }
            }

        } catch (Exception e) {
            errors.add("Error general: " + e.getMessage());
            log.error("[CIDI-NOTAS] Error general en importNotas", e);
        }

        res.setTotalRows(row - 1);
        res.setInserted(inserted);
        res.setUpdated(updated);
        res.setSkipped(skipped);
        return res;
    }

    // ================= resto igual =================

    private UpsertCount procesarFila(CSVRecord rec,
                                     CicloLectivo cicloActual,
                                     LocalDate hoy,
                                     boolean dryRun,
                                     int rowNumber) {

        String gradoRaw = CsvUtils.get(rec, "Grado/Año", "grado/ano", "Grado Ano");
        String divisionRaw = CsvUtils.get(rec, "División", "division");
        String planRaw = CsvUtils.get(rec, "Plan de Estu.", "Plan de Estudio", "Plan Estu");
        String dni = CsvUtils.get(rec, "N° Documento", "Nº Documento", "Nro. Docum.", "DNI", "Nro Documento");
        String apellido = CsvUtils.get(rec, "Apellido");
        String nombre = CsvUtils.get(rec, "Nombre");
        String materiaNom = CsvUtils.get(rec, "Espacio Curricular", "Materia", "Asignatura");

        if (isBlank(dni) || isBlank(nombre) || isBlank(apellido) || isBlank(materiaNom)) {
            return new UpsertCount(0, 0);
        }

        Integer anio = CursoResolver.parseAnio(gradoRaw);
        var division = CursoResolver.parseDivision(divisionRaw);
        Nivel nivel = NivelMapper.fromPlanEstudio(planRaw);

        if (anio == null || division == null || nivel == null) {
            throw new IllegalArgumentException("Curso inválido en fila " + rowNumber + ": " +
                    gradoRaw + "/" + divisionRaw + "/" + planRaw);
        }

        Curso curso = CursoResolver.findOrThrow(cursoRepository, anio, division, nivel);

        Alumno alumno = alumnoRepository.findByDni(dni)
                .orElseThrow(() -> new CalificacionException("No existe alumno con DNI " + dni));

        var hcOpt = historialCursoRepository
                .findByAlumno_IdAndCicloLectivo_IdAndFechaHastaIsNull(alumno.getId(), cicloActual.getId());

        if (hcOpt.isEmpty() || !hcOpt.get().getCurso().getId().equals(curso.getId())) {
            throw new CalificacionException("El alumno " + apellido +
                    " no cursa el curso indicado en el ciclo actual.");
        }
        HistorialCurso hc = hcOpt.get();

        MateriaCurso mc = materiaCursoRepository
                .findByCursoAndMateriaNombre(curso.getId(), materiaNom)
                .orElseThrow(() -> new CalificacionException(
                        "La materia '" + materiaNom + "' no está asignada al curso."));

        HistorialMateria hm = historialMateriaRepository
                .findByHistorialCurso_IdAndMateriaCurso_Id(hc.getId(), mc.getId())
                .orElseGet(() -> {
                    HistorialMateria nuevo = HistorialMateria.builder()
                            .historialCurso(hc)
                            .materiaCurso(mc)
                            .build();
                    return dryRun ? nuevo : historialMateriaRepository.save(nuevo);
                });

        LocalDate fecha = hoy;
        UpsertCount c1 = upsertNotasFila(hm, 1, rec,
                "Nota 1 Etapa 1", "Nota 2 Etapa 1", "Nota 3 Etapa 1", "Nota 4 Etapa 1",
                fecha, dryRun);
        UpsertCount c2 = upsertNotasFila(hm, 2, rec,
                "Nota 1 Etapa 2", "Nota 2 Etapa 2", "Nota 3 Etapa 2", "Nota 4 Etapa 2",
                fecha, dryRun);

        return new UpsertCount(c1.inserted() + c2.inserted(), c1.updated() + c2.updated());
    }

    private UpsertCount upsertNotasFila(HistorialMateria hm, int etapa, CSVRecord rec,
                                        String col1, String col2, String col3, String col4,
                                        LocalDate fecha, boolean dryRun) {

        int ins = 0, upd = 0;
        String[] cols = {col1, col2, col3, col4};

        for (int i = 0; i < cols.length; i++) {
            String raw = CsvUtils.get(rec, cols[i]);
            if (raw == null) continue;

            String t = raw.trim();
            // Celda completamente vacía => no hacer nada
            if (t.isEmpty()) continue;

            int numeroNota = i + 1;

            // Caso especial: "-" => borrar nota existente si la hay
            if ("-".equals(t) || "–".equals(t)) { // contemplamos guion normal y guion largo
                if (hm.getId() != null) {
                    Optional<Calificacion> existingDel =
                            calificacionRepository.findByHistorialMateria_IdAndEtapaAndNumeroNota(
                                    hm.getId(), etapa, numeroNota);

                    if (existingDel.isPresent()) {
                        if (!dryRun) {
                            calificacionRepository.delete(existingDel.get());
                        }
                        // Lo contamos como "updated" porque es un cambio en esa celda
                        upd++;
                    }
                }
                // Pasamos a la siguiente columna
                continue;
            }

            // Resto de casos: debe ser un número válido 1..10
            Integer nota = parseNota(t);

            Optional<Calificacion> existing = (hm.getId() == null)
                    ? Optional.empty()
                    : calificacionRepository.findByHistorialMateria_IdAndEtapaAndNumeroNota(
                    hm.getId(), etapa, numeroNota);

            if (existing.isPresent()) {
                Calificacion c = existing.get();
                boolean changed = !Objects.equals(c.getNota(), nota) ||
                        (fecha != null && !fecha.equals(c.getFecha()));
                if (changed) {
                    c.setNota(nota);
                    c.setFecha(fecha);
                    if (!dryRun) calificacionRepository.save(c);
                    upd++;
                }
            } else {
                Calificacion nueva = Calificacion.builder()
                        .historialMateria(hm)
                        .etapa(etapa)
                        .numeroNota(numeroNota)
                        .nota(nota)
                        .fecha(fecha)
                        .build();
                if (!dryRun) calificacionRepository.save(nueva);
                ins++;
            }
        }

        return new UpsertCount(ins, upd);
    }

    private Integer parseNota(String raw) {
        try {
            int n = Integer.parseInt(raw.trim());
            if (n < 1 || n > 10) {
                throw new IllegalArgumentException("Nota fuera de rango [1..10]: " + raw);
            }
            return n;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Nota inválida: " + raw);
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    // ===== Helpers headers =====

    private static String canonHeader(String h) {
        if (h == null) return "";
        String noBom = h.replace("\uFEFF", "");
        String n = Normalizer.normalize(noBom, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        n = n.replaceAll("[^a-zA-Z0-9]+", "");
        return n.toLowerCase(Locale.ROOT);
    }

    private static boolean containsHeader(Set<String> headersCanon, String... candidates) {
        for (String c : candidates) {
            if (headersCanon.contains(canonHeader(c))) {
                return true;
            }
        }
        return false;
    }

    // para ver caracteres raros en los headers
    private static String toHex(String s) {
        StringBuilder sb = new StringBuilder();
        for (char ch : s.toCharArray()) {
            sb.append(String.format("%04x ", (int) ch));
        }
        return sb.toString().trim();
    }

    private record UpsertCount(int inserted, int updated) {}
}
