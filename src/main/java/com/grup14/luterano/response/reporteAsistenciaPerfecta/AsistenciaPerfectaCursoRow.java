package com.grup14.luterano.response.reporteAsistenciaPerfecta;

import com.grup14.luterano.dto.CursoDto;
import com.grup14.luterano.dto.reporteAsistenciaPerfecta.AlumnoLigeroDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaPerfectaCursoRow {
    private CursoDto curso;
    private List<AlumnoLigeroDto> alumnos;
    private int totalPerfectos;
}
