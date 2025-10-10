package com.grup14.luterano.request.calificacion;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class CalificacionUpdateRequest {
    @Min(value = 1, message = "La nota mínima es 1")
    @Max(value = 10, message = "La nota máxima es 10")
    private Integer nota;
    private LocalDate fecha;
    @NotNull(message = "El alumno Id es obligatorio")
    private Long alumnoId;
    @NotNull(message = "La materia Id es obligatoria")
    private Long materiaId;
    @NotNull(message = "La calificacion Id es obligatoria")
    private Long califId;
}
