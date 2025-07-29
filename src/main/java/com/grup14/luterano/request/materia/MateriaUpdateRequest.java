package com.grup14.luterano.request.materia;

import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.enums.Nivel;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder@AllArgsConstructor@NoArgsConstructor@Data
public class MateriaUpdateRequest {
    @NotNull(message = "El id es obligatorio")
    private Long id;
    private String nombreMateria;
    private String descripcion; // corregido también el nombre
    private Nivel nivel;
}
