package com.grup14.luterano.dto.calificaciones;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionDto {
    private Long id;

    @Min(value = 1, message = "La nota mínima es 1")
    @Max(value = 10, message = "La nota máxima es 10")
    private Integer nota;

    @Min(value = 1, message = "La etapa debe ser >= 1")
    private int etapa;

    @Min(value = 1, message = "El número de nota debe ser >= 1")
    @Max(value = 4, message = "El número de nota debe ser <= 4")
    private int numeroNota;

    private LocalDate fecha;
    private Long materiaId;
    private String materiaNombre;
    private Long materiaCursoId;
}
