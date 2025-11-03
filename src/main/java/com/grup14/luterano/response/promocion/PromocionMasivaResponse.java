package com.grup14.luterano.response.promocion;

import com.grup14.luterano.dto.promocion.AlumnoPromocionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromocionMasivaResponse {

    private Integer procesados;
    private Integer promocionados;
    private Integer repitentes;
    private Integer egresados;
    private Integer excluidos; // Nuevos excluidos por repetición
    private Integer noProcesados;
    private Boolean dryRun;

    private List<AlumnoPromocionDto> resumen;

    private Integer code;
    private String mensaje;
}