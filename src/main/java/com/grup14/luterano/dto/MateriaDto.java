package com.grup14.luterano.dto;

import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.enums.Nivel;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MateriaDto {
    private Long id;
     @NotNull(message = "El nombre de la materia es obligatoria")
    private String nombreMateria;
     @NotNull(message ="El nivel es obligatorio")
    private Nivel nivel;
    private Curso curso;
}
