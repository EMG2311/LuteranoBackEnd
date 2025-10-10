package com.grup14.luterano.request.calificacion;

import com.grup14.luterano.dto.CalificacionDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CalificacionRequest extends CalificacionDto {

    @NotNull(message = "El alumnoId es obligatorio")
    private Long alumnoId;

    @NotNull(message = "La materiaId es obligatoria")
    private Long materiaId;

}
