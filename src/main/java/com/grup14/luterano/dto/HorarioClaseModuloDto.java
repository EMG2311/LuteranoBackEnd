package com.grup14.luterano.dto;

import com.grup14.luterano.dto.materiaCurso.MateriaCursoDto;
import com.grup14.luterano.dto.modulo.ModuloDto;
import com.grup14.luterano.entities.enums.DiaSemana;
import lombok.Builder;
import lombok.Data;

@Data@Builder
public class HorarioClaseModuloDto {
    private DiaSemana diaSemana;
    private ModuloDto modulo;
    private MateriaCursoDto materiaCurso;
}
