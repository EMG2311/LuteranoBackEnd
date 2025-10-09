package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.*;
import com.grup14.luterano.entities.enums.Nivel;
import com.grup14.luterano.exeptions.CalificacionException;
import com.grup14.luterano.mappers.NivelMapper;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.response.imports.ImportResultResponse;
import com.grup14.luterano.service.CidiNotasImportService;
import com.grup14.luterano.utils.CsvUtils;
import com.grup14.luterano.utils.CursoResolver;
import com.grup14.luterano.utils.HeaderAliases;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CidiNotasImportServiceImpl implements CidiNotasImportService {

    private final AlumnoRepository alumnoRepository;
    private final CursoRepository cursoRepository;
    private final HistorialCursoRepository historialCursoRepository;
    private final HistorialMateriaRepository historialMateriaRepository;
    private final MateriaCursoRepository materiaCursoRepository;
    private final CalificacionRepository calificacionRepository;
    private final CicloLectivoRepository cicloLectivoRepository;
    private final PlatformTransactionManager txManager;

    public ImportResultResponse importNotas(InputStream in, boolean dryRun, String charsetOpt) {
        ImportResultResponse res = new ImportResultResponse();
        List<String> errors = new ArrayList<>();
        res.setErrors(errors);

        int row = 1, inserted = 0, updated = 0, skipped = 0;

        try (CSVParser parser = CsvUtils.createParser(in, charsetOpt)) {

            // validar encabezados
            var headersCanon = parser.getHeaderMap().keySet().stream()
                    .map(HeaderAliases::canon)
                    .collect(Collectors.toSet());

            List<String> requeridosPretty = List.of(
                    "Grado/Año","División","Plan de Estu.","N° Documento",
                    "Apellido","Nombre","Fecha Nacimiento","Espacio Curricular");
            List<String> faltantes = new ArrayList<>();
            for (String pretty : requeridosPretty) {
                if (!headersCanon.contains(HeaderAliases.canon(pretty))) faltantes.add(pretty);
            }
            if (!faltantes.isEmpty()) {
                errors.add("Faltan columnas requeridas: " + String.join(", ", faltantes));
            }

            // transacción por fila
            TransactionTemplate newTx = new TransactionTemplate(txManager);
            newTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            LocalDate hoy = LocalDate.now();
            CicloLectivo cicloActual = cicloLectivoRepository
                    .findByFechaDesdeBeforeAndFechaHastaAfter(hoy, hoy)
                    .orElseThrow(() -> new IllegalStateException("No hay ciclo lectivo activo."));

            for (CSVRecord rec : parser) {
                row++;
                try {
                    String gradoRaw    = CsvUtils.get(rec, "Grado/Año");
                    String divisionRaw = CsvUtils.get(rec, "División");
                    String planRaw     = CsvUtils.get(rec, "Plan de Estu.");
                    String dni         = CsvUtils.get(rec, "N° Documento", "Nro. Docum.", "DNI");
                    String apellido    = CsvUtils.get(rec, "Apellido");
                    String nombre      = CsvUtils.get(rec, "Nombre");
                    String materiaNom  = CsvUtils.get(rec, "Espacio Curricular");

                    if (isBlank(dni) || isBlank(nombre) || isBlank(apellido) || isBlank(materiaNom)) {
                        skipped++;
                        continue;
                    }

                    Integer anio = CursoResolver.parseAnio(gradoRaw);
                    var division = CursoResolver.parseDivision(divisionRaw);
                    Nivel nivel  = NivelMapper.fromPlanEstudio(planRaw);

                    if (anio == null || division == null || nivel == null) {
                        throw new IllegalArgumentException("Curso inválido: " + gradoRaw + "/" + divisionRaw + "/" + planRaw);
                    }

                    Curso curso = CursoResolver.findOrThrow(cursoRepository, anio, division, nivel);

                    Alumno alumno = alumnoRepository.findByDni(dni)
                            .orElseThrow(() -> new CalificacionException("No existe alumno con DNI " + dni));

                    // historial del alumno en el ciclo actual
                    var hcOpt = historialCursoRepository.findByAlumno_IdAndCicloLectivo_IdAndFechaHastaIsNull(
                            alumno.getId(), cicloActual.getId());
                    if (hcOpt.isEmpty() || !hcOpt.get().getCurso().getId().equals(curso.getId())) {
                        throw new CalificacionException("El alumno " + apellido + " no cursa el curso indicado en el ciclo actual.");
                    }
                    HistorialCurso hc = hcOpt.get();

                    // materia asignada al curso
                    MateriaCurso mc = materiaCursoRepository
                            .findByCursoAndMateriaNombre(curso.getId(), materiaNom)
                            .orElseThrow(() -> new CalificacionException("La materia '" + materiaNom + "' no está asignada al curso."));

                    // historial materia
                    HistorialMateria hm = historialMateriaRepository
                            .findByHistorialCurso_IdAndMateriaCurso_Id(hc.getId(), mc.getId())
                            .orElseGet(() -> {
                                HistorialMateria nuevo = HistorialMateria.builder()
                                        .historialCurso(hc)
                                        .materiaCurso(mc)
                                        .build();
                                return historialMateriaRepository.save(nuevo);
                            });

                    // Etapa 1 y Etapa 2 (4 notas cada una)
                    LocalDate fecha = hoy;
                    UpsertCount c1 = upsertNotasFila(hm, 1, rec,
                            "Nota 1 Etapa 1","Nota 2 Etapa 1","Nota 3 Etapa 1","Nota 4 Etapa 1",
                            fecha, dryRun);
                    UpsertCount c2 = upsertNotasFila(hm, 2, rec,
                            "Nota 1 Etapa 2","Nota 2 Etapa 2","Nota 3 Etapa 2","Nota 4 Etapa 2",
                            fecha, dryRun);

                    inserted += c1.inserted + c2.inserted;
                    updated  += c1.updated  + c2.updated;

                } catch (Exception exRow) {
                    skipped++;
                    errors.add("Fila " + row + ": " + exRow.getMessage());
                }
            }

        } catch (Exception e) {
            errors.add("Error general: " + e.getMessage());
        }

        res.setTotalRows(row - 1);
        res.setInserted(inserted);
        res.setUpdated(updated);
        res.setSkipped(skipped);
        return res;
    }

    // Inserta o actualiza las notas de una etapa
    private UpsertCount upsertNotasFila(HistorialMateria hm, int etapa, CSVRecord rec,
                                        String col1, String col2, String col3, String col4,
                                        LocalDate fecha, boolean dryRun) {

        int ins = 0, upd = 0;
        String[] cols = {col1, col2, col3, col4};

        for (int i = 0; i < cols.length; i++) {
            String raw = CsvUtils.get(rec, cols[i]);
            if (isBlank(raw)) continue;

            Integer nota = parseNota(raw);
            int numeroNota = i + 1;

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
            if (n < 1 || n > 10) throw new IllegalArgumentException("Nota fuera de rango [1..10]: " + raw);
            return n;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Nota inválida: " + raw);
        }
    }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }

    private record UpsertCount(int inserted, int updated) {}
}