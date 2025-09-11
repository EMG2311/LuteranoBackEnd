package com.grup14.luterano.dto;

import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.enums.Nivel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class MateriaDto {
    private Long id;
    @NotNull(message = "El nombre de la materia es obligatorio")
    private String nombreMateria;
    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;
    @NotNull(message ="El nivel es obligatorio")
    private Nivel nivel;
    // Elimino la lista de dictados para romper el bucle
   // private List<MateriaCursoDto> dictados;
}
