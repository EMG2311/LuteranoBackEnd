package com.grup14.luterano.request.espacioAulico;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DenegarEspacioAulicoRequest {
    @NotBlank(message = "El motivo de denegación es obligatorio.")
    private String motivo;
}
