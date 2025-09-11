package com.grup14.luterano.dto;

import com.grup14.luterano.commond.PersonaConUsuarioDto;
import com.grup14.luterano.entities.Preceptor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
public class PreceptorDto extends PersonaConUsuarioDto {
    private List<CursoDto> cursos = new ArrayList<>();
    public PreceptorDto(){}
}
