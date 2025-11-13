package com.grup14.luterano.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDesempenoDocenteDto {
    // Información del docente
    private Long docenteId;
    private String docenteNombre;
    private String docenteApellido;
    private String docenteNombreCompleto;

    // Información de la materia y curso
    private Long materiaId;
    private String materiaNombre;
    private Long cursoId;
    private String cursoNombre; // "1° A PRIMARIA"
    private Integer cursoAnio;
    private String cursoDivision;

    // Estadísticas de las 4 notas por etapa
    private Integer totalNotasE1; // Total de notas en etapa 1 (4 notas × cantidad de alumnos)
    private Integer notasDesaprobadasE1; // Notas < 6 en etapa 1
    private Double porcentajeDesaprobadasE1; // (desaprobadas / total) × 100

    private Integer totalNotasE2; // Total de notas en etapa 2
    private Integer notasDesaprobadasE2; // Notas < 6 en etapa 2
    private Double porcentajeDesaprobadasE2; // (desaprobadas / total) × 100

    // Estadísticas generales
    private Integer totalNotas; // E1 + E2
    private Integer totalDesaprobadas; // E1 + E2 desaprobadas
    private Double porcentajeDesaprobadas; // Total desaprobadas / total × 100

    // Indicadores de alerta
    private String nivelAlerta; // "NORMAL", "ATENCION", "CRITICO"
    private String observaciones; // Descripción del problema detectado

    // Comparación con el promedio general
    private Double promedioGeneralMateria; // Promedio de todas las divisiones de esta materia
    private Double diferenciaPorcentaje; // Diferencia con el promedio general
}