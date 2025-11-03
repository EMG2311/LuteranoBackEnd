package com.grup14.luterano.dto;

import com.grup14.luterano.entities.enums.Division;
import com.grup14.luterano.entities.enums.Nivel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReporteLibreDto {
    private Long alumnoId;
    private String dni;
    private String apellido;
    private String nombre;

    private Long cursoId;
    private Integer anio;          // del curso
    private Nivel nivel;           // BASICO / ORIENTADO
    private Division division;     // A, B, ...

    private String cursoEtiqueta;  // ej: "1° BASICO"
    private String motivo;         // "Inasistencias > 25"
    private Double inasistenciasAcum; // total ponderado (dos decimales)
}