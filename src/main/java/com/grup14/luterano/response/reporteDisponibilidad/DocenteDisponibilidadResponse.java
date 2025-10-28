package com.grup14.luterano.response.reporteDisponibilidad;

import com.grup14.luterano.dto.reporteDisponibilidad.DocenteDisponibilidadDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocenteDisponibilidadResponse {
    private DocenteDisponibilidadDto data;
    private Integer code;
    private String mensaje;
}
