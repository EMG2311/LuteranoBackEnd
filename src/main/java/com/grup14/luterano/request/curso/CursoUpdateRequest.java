package com.grup14.luterano.request.curso;


import com.grup14.luterano.entities.enums.Division;
import com.grup14.luterano.entities.enums.Nivel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoUpdateRequest {

    @NotNull(message = "El ID del curso no puede ser nulo")
    private Long id;
    @Min(value = 1, message = "El año tiene que ser mayor/igual a 1")
    @Max(value = 6, message = "El año tiene qeu ser menor/igual a 6")
    private Integer anio;
    private Division division;
    private Nivel nivel;
    private Long aulaId; // quiero solo el id del aula

    // private AulaDto aula;
    // private List<MateriaCursoDto> dictados;


}
