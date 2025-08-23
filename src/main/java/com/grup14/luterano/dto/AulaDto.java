package com.grup14.luterano.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder ;


@SuperBuilder(toBuilder=true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AulaDto {

    private Long id;
    @NotBlank(message = "El nombre del aula es obligatoria")
    private String nombre;
    @NotBlank(message = "La ubicacion del aula es obligatoria")
    private String ubicacion;
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Integer capacidad;
    private CursoDto curso;
}
