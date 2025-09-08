package com.grup14.luterano.request.aula;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AulaUpdateRequest {

    @NotNull(message = "El ID es obligatorio")
    private Long id;
    private String nombre;
    private String ubicacion;
    private Integer capacidad; // cambio de int a Integer para permitir nulls en la actualización.
    // El curso no se actualiza aquí.
}
