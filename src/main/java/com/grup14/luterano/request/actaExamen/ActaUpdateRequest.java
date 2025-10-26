package com.grup14.luterano.request.actaExamen;

import lombok.Data;

@Data
public class ActaUpdateRequest {
    private Long id;                // REQUERIDO
    private String numeroActa;      // opcional
    private String observaciones;   // opcional
}
