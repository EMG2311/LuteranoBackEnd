package com.grup14.luterano.dto.mesaExamenDocente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesaExamenDocenteDto {
    private Long id;
    private Long docenteId;
    private String apellidoDocente;
    private String nombreDocente;
    private String nombreCompleto;
    private String nombreMateria;
    private boolean esDocenteMateria;
}