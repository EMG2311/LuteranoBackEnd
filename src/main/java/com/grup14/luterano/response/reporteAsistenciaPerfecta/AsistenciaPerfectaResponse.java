package com.grup14.luterano.response.reporteAsistenciaPerfecta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaPerfectaResponse {
    private Integer anio;
    private List<AsistenciaPerfectaCursoRow> cursos; // incluir cursos con lista vacía si no hay perfectos
    private int totalAlumnosPerfectos;
    private Integer code;
    private String mensaje;
}
