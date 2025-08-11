package com.grup14.luterano.dto;

import com.grup14.luterano.entities.Curso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AulaDto {

    private Long id;
    private String nombre;
    private String ubicacion;
    private int capacidad;
    private CursoDto curso;
}
