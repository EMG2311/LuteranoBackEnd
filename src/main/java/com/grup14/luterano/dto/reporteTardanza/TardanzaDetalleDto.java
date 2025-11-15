package com.grup14.luterano.dto.reporteTardanza;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class TardanzaDetalleDto {
    private LocalDate fecha;
    private String observacion;
}
