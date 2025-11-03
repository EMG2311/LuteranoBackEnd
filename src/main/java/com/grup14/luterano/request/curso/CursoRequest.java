package com.grup14.luterano.request.curso;

import com.grup14.luterano.entities.enums.Division;
import com.grup14.luterano.entities.enums.Nivel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

//@SuperBuilder @Data
//@NoArgsConstructor
@Getter
@Setter
@Builder
public class CursoRequest {
    @NotNull
    @Positive
    @Min(value = 1, message = "El año tiene que ser mayor/igual a 1")
    @Max(value = 6, message = "El año tiene qeu ser menor/igual a 6")
    private Integer anio;
    @NotNull(message = "La division es obligatoria")
    private Division division;
    @NotNull(message = "El nivel es obligatorio")
    private Nivel nivel;
    @NotNull(message = "El id del aula es obligatoria")
    private Long aulaId; // quiero solo el id del aula

}
