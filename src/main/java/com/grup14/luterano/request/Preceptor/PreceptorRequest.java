package com.grup14.luterano.request.Preceptor;

import com.grup14.luterano.commond.GeneroEnum;
import com.grup14.luterano.commond.PersonaConUsuarioDto;
import com.grup14.luterano.dto.PreceptorDto;
import com.grup14.luterano.dto.UserDto;
import com.grup14.luterano.entities.enums.TipoDoc;
import com.grup14.luterano.validation.MayorDeEdad;
import com.grup14.luterano.validation.MayorDeEdadGruoup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
@SuperBuilder
public class PreceptorRequest extends PersonaConUsuarioDto{
    public PreceptorRequest(){}

}
