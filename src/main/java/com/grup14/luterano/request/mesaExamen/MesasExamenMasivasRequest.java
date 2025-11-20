package com.grup14.luterano.request.mesaExamen;


import com.grup14.luterano.entities.enums.TipoMesa;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MesasExamenMasivasRequest {

    // Turno al que pertenecen TODAS las mesas creadas
    private Long turnoId;

    // Fecha de la mesa; debe caer dentro del turno.
    // De acá sacamos el año de ciclo para buscar quién la desaprobó.
    private LocalDate fechaMesa;

    // Cursos para los que se van a crear mesas
    private List<Long> cursoIds;

    // Opcional: materias específicas. Si está vacío/null => todas las materias del curso.
    private List<Long> materiaIds;
    private TipoMesa tipoMesa;
}