package com.grup14.luterano.response.cicloLectivo;

import com.grup14.luterano.dto.AulaDto;
import com.grup14.luterano.dto.CicloLectivoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CicloLectivoResponseList {

    private List<CicloLectivoDto> CicloLectivoDtos;
    private Integer code;
    private String mensaje;

}