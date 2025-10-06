package com.grup14.luterano.dto;

import com.grup14.luterano.entities.CicloLectivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder@NoArgsConstructor@AllArgsConstructor@Data
public class CicloLectivoDto{
    private Long id;
    private String nombre;
    //private LocalDate fechaDesde;
    //private LocalDate fechaHasta;
}
