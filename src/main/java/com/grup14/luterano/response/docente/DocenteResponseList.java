package com.grup14.luterano.response.docente;

import com.grup14.luterano.dto.docente.DocenteDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DocenteResponseList {
    private List<DocenteDto> docenteDtos;
    private Integer code;
    private String mensaje;
}
