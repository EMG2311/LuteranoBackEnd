package com.grup14.luterano.request.espacioAulico;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspacioAulicoRequest {

    @NotBlank(message = "El nombre del espacio es obligatorio.")
    private String nombre;

    @NotBlank(message = "La ubicación del espacio es obligatoria.")
    private String ubicacion;

    @NotNull(message = "La capacidad es obligatoria.")
    @Min(value = 1, message = "La capacidad debe ser al menos 1.")
    private Integer capacidad;
}
