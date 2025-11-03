package com.grup14.luterano.request.espacioAulico;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EspacioAulicoUpdateRequest {

    @NotNull(message = "El ID es obligatorio")
    private Long id;
    private String nombre;
    private String ubicacion;
    private Integer capacidad;
}
