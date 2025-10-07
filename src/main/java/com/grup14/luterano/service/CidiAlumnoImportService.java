package com.grup14.luterano.service;

import com.grup14.luterano.response.imports.ImportResultResponse;

import java.io.InputStream;

public interface CidiAlumnoImportService {
    ImportResultResponse importAlumnos(InputStream in, boolean dryRun, String charsetOpt);
}
