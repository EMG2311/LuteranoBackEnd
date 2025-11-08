package com.grup14.luterano.request.alumno;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarTutoresRequest {
    @NotNull(message = "El ID del alumno es obligatorio")
    private Long alumnoId;
    
    @NotNull(message = "La lista de tutores es obligatoria")
    private List<Long> tutorIds;
}