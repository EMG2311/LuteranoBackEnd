package com.grup14.luterano.response.mesaExamenDocente;

import com.grup14.luterano.dto.mesaExamenDocente.DocenteDisponibleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocentesDisponiblesResponse {
    private List<DocenteDisponibleDto> docentes;
    private Long mesaExamenId;
    private String nombreMateria;
    private Integer totalDocentes;
    private Integer docentesQueDALaMateria;
    private Integer code;
    private String mensaje;
}