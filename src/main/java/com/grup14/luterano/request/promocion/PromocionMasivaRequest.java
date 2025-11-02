package com.grup14.luterano.request.promocion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromocionMasivaRequest {
    
    @NotNull(message = "El año es obligatorio")
    @Min(value = 2020, message = "El año debe ser mayor a 2020")
    @Max(value = 2030, message = "El año debe ser menor a 2030")
    private Integer anio;
    
    @NotNull(message = "El ciclo lectivo es obligatorio")
    private Long cicloLectivoId;
    
    @Builder.Default
    private Boolean procesarEgresados = true;
    
    @Builder.Default
    @Min(value = 1, message = "El máximo de repeticiones debe ser al menos 1")
    @Max(value = 5, message = "El máximo de repeticiones no puede ser mayor a 5")
    private Integer maxRepeticiones = 2;
    
    @Builder.Default
    private Boolean dryRun = false; // Para simular sin hacer cambios
}