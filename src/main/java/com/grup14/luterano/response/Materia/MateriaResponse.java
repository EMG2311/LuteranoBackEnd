package com.grup14.luterano.response.Materia;

import com.grup14.luterano.dto.MateriaDto;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.enums.Nivel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MateriaResponse {
    private MateriaDto materiaDto;
    private Integer code;
    private String mensaje;
}
