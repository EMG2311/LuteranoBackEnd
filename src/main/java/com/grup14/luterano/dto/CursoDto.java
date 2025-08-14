package com.grup14.luterano.dto;
import com.grup14.luterano.entities.Aula;
import com.grup14.luterano.entities.Materia;
import com.grup14.luterano.entities.enums.Nivel;
import com.grup14.luterano.entities.enums.Division;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoDto {

    private Long id; // se usa el id en el dto ?

    @NotNull(message = "El numero del curso no puede ser nulo")
    private int numero;

    @NotNull(message = "La division no puede ser nula")
    private Division division;

    @NotNull(message = "El nivel no puede ser nulo")
    private Nivel nivel;

    private AulaDto aula;

    private List<MateriaDto> materias;


}
