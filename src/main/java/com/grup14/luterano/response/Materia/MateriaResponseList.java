package com.grup14.luterano.response.Materia;

import com.grup14.luterano.dto.MateriaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MateriaResponseList {
    private List<MateriaDto> materiasDto;
    private Integer code;
    private String mensaje;
}