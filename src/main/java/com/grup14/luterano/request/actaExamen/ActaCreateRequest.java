package com.grup14.luterano.request.actaExamen;

import lombok.Data;

@Data
public class ActaCreateRequest {
    private Long mesaId;            // REQUERIDO
    private String numeroActa;      // opcional (si no viene, se autogenera)
    private String observaciones;   // opcional
}