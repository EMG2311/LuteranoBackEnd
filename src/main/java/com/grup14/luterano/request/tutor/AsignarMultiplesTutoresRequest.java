package com.grup14.luterano.request.tutor;

import jakarta.validation.constraints.NotEmpty;
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
public class AsignarMultiplesTutoresRequest {
    @NotNull(message = "El ID del alumno es obligatorio")
    private Long alumnoId;
    
    @NotEmpty(message = "Debe proporcionar al menos un tutor")
    private List<Long> tutorIds;
}