package com.grup14.luterano.request.curso;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntercambiarAulasRequest {
    @NotNull(message = "El id del primer curso es obligatorio")
    private Long cursoId1;
    @NotNull(message = "El id del segundo curso es obligatorio")
    private Long cursoId2;
}
