package com.grup14.luterano.response.Materia;

import com.grup14.luterano.dto.MateriaDto;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.enums.Nivel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder@AllArgsConstructor@NoArgsConstructor@Data
public class MateriaResponse {
    private MateriaDto materiaDto;
    private Integer code;
    private String mensaje;
}
