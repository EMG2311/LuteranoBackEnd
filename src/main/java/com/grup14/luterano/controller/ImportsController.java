package com.grup14.luterano.controller;

import com.grup14.luterano.response.imports.ImportResultResponse;
import com.grup14.luterano.service.CidiAlumnoImportService;
import com.grup14.luterano.service.CidiNotasImportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/import")
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')")
public class ImportsController {

    private final CidiAlumnoImportService cidiAlumnoImportService;
    private final CidiNotasImportService cidiNotasImportService;
    @PostMapping(value = "/alumnos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importa el csv que se trae del cidi para cargar los alumnos",
            description = "La variable dryRun si es true no persiste los datos (hace una prueba), si es false si")
    public ResponseEntity<ImportResultResponse> importCidi(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dryRun", defaultValue = "true") boolean dryRun,
            @RequestParam(value = "charset", required = false) String charset // ej: windows-1252
    ) throws IOException {
        if (file.isEmpty()) return ResponseEntity.badRequest().build();
        var res = cidiAlumnoImportService.importAlumnos(file.getInputStream(), dryRun, charset);
        return ResponseEntity.ok(res);
    }


    @PostMapping(value = "/notas", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importa el CSV para cargar/actualizar NOTAS (Etapa 1 y 2, 4 notas c/u)",
            description = """
                    Lee columnas como:
                    - Grado/Año; División; Turno; Plan de Estu.; Ciclo; N° Documento; Apellido; Nombre; Fecha Nacimiento
                    - Espacio Curricular; Código Espacio Curricular; Curso
                    - Nota 1..4 Etapa 1; Nota 1..4 Etapa 2
                    Valida: alumno existe, cursa el curso en el ciclo lectivo activo, materia asignada al curso.
                    Hace upsert por (historialMateria, etapa, numeroNota). dryRun=true = no persiste.
                    """)
    public ResponseEntity<ImportResultResponse> importNotas(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dryRun", defaultValue = "true") boolean dryRun,
            @RequestParam(value = "charset", required = false) String charset // ej: windows-1252
    ) throws IOException {
        if (file.isEmpty()) return ResponseEntity.badRequest().build();
        var res = cidiNotasImportService.importNotas(file.getInputStream(), dryRun, charset);
        return ResponseEntity.ok(res);
    }
}
