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

    @NotEmpty(message = "Debe asignar exactamente 3 docentes")
    @Size(min = 3, max = 3, message = "Debe asignar exactamente 3 docentes")
    private List<Long> docenteIds;
}