package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.*;
import com.grup14.luterano.entities.enums.*;
import com.grup14.luterano.mappers.NivelMapper;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.response.imports.ImportResultResponse;
import com.grup14.luterano.service.CidiAlumnoImportService;
import com.grup14.luterano.utils.CsvUtils;
import com.grup14.luterano.utils.CursoResolver;
import com.grup14.luterano.utils.FechaParser;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CidiAlumnoImportServiceImpl implements CidiAlumnoImportService {
    private final HistorialMateriaRepository historialMateriaRepository;
    private final AlumnoRepository alumnoRepository;
    private final CursoRepository cursoRepository;
    private final HistorialCursoRepository historialCursoRepository;
    private final CicloLectivoRepository cicloLectivoRepository;
    private final PlatformTransactionManager txManager;

    @Override
    public ImportResultResponse importAlumnos(InputStream in, boolean dryRun, String charsetOpt) {
        ImportResultResponse res = new ImportResultResponse();
        List<String> errors = new ArrayList<>();
        res.setErrors(errors);

        int row = 1, inserted = 0, updated = 0, skipped = 0;

        try (CSVParser parser = CsvUtils.createParser(in, charsetOpt)) {

            var headersCanon = parser.getHeaderMap().keySet().stream()
                    .map(HeaderAliases::canon).collect(java.util.stream.Collectors.toSet());

            List<String> requeridosPretty = List.of(
                    "Grado/Año", "División", "Plan de Estu.", "Nro. Docum.", "Apellido", "Nombre", "Fecha Nacimiento");
            List<String> faltantes = new ArrayList<>();
            for (String pretty : requeridosPretty) {
                if (!headersCanon.contains(HeaderAliases.canon(pretty))) faltantes.add(pretty);
            }
            if (!faltantes.isEmpty()) {
                errors.add("Faltan columnas requeridas: " + String.join(", ", faltantes));
            }

            // TransactionTemplate ya con REQUIRES_NEW
            TransactionTemplate newTx = new TransactionTemplate(txManager);
            newTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            for (CSVRecord rec : parser) {
                row++;
                try {
                    String gradoRaw = CsvUtils.get(rec, "grado/ano");
                    String divisionRaw = CsvUtils.get(rec, "division");
                    String planRaw = CsvUtils.get(rec, "plan de estu");
                    String dni = CsvUtils.get(rec, "nro docum");
                    String apellido = CsvUtils.get(rec, "apellido");
                    String nombre = CsvUtils.get(rec, "nombre");
                    String fNacStr = CsvUtils.get(rec, "fecha nacimiento");

                    if (isBlank(dni) || isBlank(nombre) || isBlank(apellido)) {
                        skipped++;
                        continue;
                    }

                    Integer anio = CursoResolver.parseAnio(gradoRaw);
                    Division division = CursoResolver.parseDivision(divisionRaw);
                    Nivel nivel = NivelMapper.fromPlanEstudio(planRaw);
                    if (anio == null || division == null || nivel == null) {
                        throw new IllegalArgumentException("Curso inválido: " + gradoRaw + " / " + divisionRaw + " / " + planRaw);
                    }

                    Curso curso = CursoResolver.findOrThrow(cursoRepository, anio, division, nivel);
                    Date fechaNac = FechaParser.parseToDate(fNacStr);

                    if (dryRun) {
                        if (alumnoRepository.findByDni(dni).isPresent()) updated++;
                        else inserted++;
                        continue;
                    }

                    Boolean existed = newTx.execute(status ->
                            upsertAlumnoFila(dni, nombre, apellido, fechaNac, curso) // ⬅️ sin @Transactional
                    );

                    if (Boolean.TRUE.equals(existed)) updated++;
                    else inserted++;

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

    // 👉 Sin @Transactional. Todo se ejecuta dentro del TransactionTemplate.
    Boolean upsertAlumnoFila(String dni, String nombre, String apellido, Date fechaNac, Curso cursoDestino) {
        LocalDate hoy = LocalDate.now();
        CicloLectivo cicloActual = cicloLectivoRepository
                .findByFechaDesdeBeforeAndFechaHastaAfter(hoy, hoy)
                .orElseThrow(() -> new IllegalStateException("No hay ciclo lectivo activo para la fecha " + hoy));

        // 1) Alumno por DNI
        Alumno alumno = alumnoRepository.findByDni(dni).orElse(null);
        boolean existed = (alumno != null);

        if (!existed) {
            alumno = new Alumno();
            alumno.setDni(dni);
            alumno.setTipoDoc(TipoDoc.DNI);
            alumno.setEstado(EstadoAlumno.REGULAR);
        }

        // Datos personales
        alumno.setNombre(nombre);
        alumno.setApellido(apellido);
        alumno.setFechaNacimiento(fechaNac);

        // Asegurar alumno en DB antes de usar en Historial
        alumno = alumnoRepository.save(alumno); // ⬅️ usar repository, no EM

        // 2) Historial abierto en ciclo actual
        Optional<HistorialCurso> abiertoOpt =
                historialCursoRepository.findByAlumno_IdAndCicloLectivo_IdAndFechaHastaIsNull(alumno.getId(), cicloActual.getId());

        if (abiertoOpt.isPresent()) {
            HistorialCurso abierto = abiertoOpt.get();
            if (abierto.getCurso().getId().equals(cursoDestino.getId())) {
                alumno.setCursoActual(cursoDestino);
                alumnoRepository.save(alumno);
                return existed;
            }


            cerrarHistorialCursoYMaterias(abierto, hoy.minusDays(1));
            crearHistorialYActualizarAlumno(alumno, cursoDestino, cicloActual);

        } else {
            crearHistorialYActualizarAlumno(alumno, cursoDestino, cicloActual);
        }

        return existed;
    }

    private void crearHistorialYActualizarAlumno(Alumno alumno, Curso curso, CicloLectivo ciclo) {
        HistorialCurso nuevo = HistorialCurso.builder()
                .alumno(alumno)
                .curso(curso)
                .cicloLectivo(ciclo)
                .fechaDesde(LocalDate.now())
                .build();
        historialCursoRepository.save(nuevo);


        if (alumno.getHistorialCursos() != null) alumno.getHistorialCursos().add(nuevo);

        alumno.setCursoActual(curso);
        alumnoRepository.save(alumno);
    }

    private void cerrarHistorialCursoYMaterias(HistorialCurso hc, LocalDate fechaCierre) {
        // cerrar HM “en curso” del HC origen
        for (HistorialMateria hm : hc.getHistorialMaterias()) {
            if (hm.getEstado() == null || hm.getEstado() == EstadoMateriaAlumno.CURSANDO) {
                hm.setEstado(EstadoMateriaAlumno.TRASLADADA);
            }
        }
        historialMateriaRepository.saveAll(hc.getHistorialMaterias());

        // cerrar HC
        hc.setFechaHasta(fechaCierre);
        historialCursoRepository.save(hc);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
