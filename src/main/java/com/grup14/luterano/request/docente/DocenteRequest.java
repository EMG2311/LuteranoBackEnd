package com.grup14.luterano.request.docente;

import com.grup14.luterano.commond.Persona;
import com.grup14.luterano.commond.PersonaDto;
import com.grup14.luterano.dto.DocenteDto;
import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.Materia;
import com.grup14.luterano.response.docente.DocenteResponse;
import com.grup14.luterano.validation.MayorDeEdad;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;
import java.util.Set;

@SuperBuilder
@Data@NoArgsConstructor
public class DocenteRequest extends DocenteDto {

    public DocenteResponse toResponse(String mensaje,Integer code) {
        return DocenteResponse.builder()
                .docente(this)
                .code(code)
                .mensaje(mensaje)
                .build();
    }
}
