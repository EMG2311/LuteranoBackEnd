package com.grup14.luterano.service;

import com.grup14.luterano.response.cicloLectivo.CicloLectivoResponse;
import com.grup14.luterano.response.cicloLectivo.CicloLectivoResponseList;

public interface CicloLectivoService {
    CicloLectivoResponse crearSiguienteCicloLectivo();

    CicloLectivoResponse crearCicloLectivoPorAnio(int anio);

    CicloLectivoResponse getCicloLectivoById(Long id);

    CicloLectivoResponseList ListCiclosLectivos();

}
