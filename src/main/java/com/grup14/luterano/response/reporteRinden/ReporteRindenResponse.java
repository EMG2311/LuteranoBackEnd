package com.grup14.luterano.response.reporteRinden;

import com.grup14.luterano.dto.CursoDto;
import com.grup14.luterano.dto.MateriaDto;
import com.grup14.luterano.dto.ReporteRindeDto;
import lombok.*;

import java.util.List;
@Getter@Setter@AllArgsConstructor@NoArgsConstructor@Builder
public class ReporteRindenResponse {
    private CursoDto curso;
    private MateriaDto materia;
    private Integer anio;

    private List<ReporteRindeDto> filas;

    private int total;
    private int totalColoquio;
    private int totalExamen;

    private int code;
    private String mensaje;
}
