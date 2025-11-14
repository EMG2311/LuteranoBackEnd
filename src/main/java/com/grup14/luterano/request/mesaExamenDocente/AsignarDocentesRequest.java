package com.grup14.luterano.request.mesaExamenDocente;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignarDocentesRequest {

    @NotEmpty(message = "Debe asignar al menos 1 docente")
    private List<Long> docenteIds;
}