package com.grup14.luterano.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
