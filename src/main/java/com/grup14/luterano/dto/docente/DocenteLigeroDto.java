package com.grup14.luterano.dto.docente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocenteLigeroDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String mail;
}
