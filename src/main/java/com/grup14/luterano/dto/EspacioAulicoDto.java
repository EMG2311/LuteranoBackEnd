package com.grup14.luterano.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspacioAulicoDto {
    private Long id;
    private String nombre;
    private String ubicacion;
    private int capacidad;
}
