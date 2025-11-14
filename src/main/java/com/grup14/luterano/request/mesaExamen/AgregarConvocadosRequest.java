package com.grup14.luterano.request.mesaExamen;

import lombok.Data;

import java.util.List;

@Data
public class AgregarConvocadosRequest {
    private List<Long> alumnoIds;
}