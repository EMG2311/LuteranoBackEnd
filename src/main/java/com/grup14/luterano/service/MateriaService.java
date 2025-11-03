package com.grup14.luterano.service;

import com.grup14.luterano.request.materia.MateriaRequest;
import com.grup14.luterano.request.materia.MateriaUpdateRequest;
import com.grup14.luterano.response.Materia.MateriaResponse;
import com.grup14.luterano.response.Materia.MateriaResponseList;

public interface MateriaService {

    MateriaResponse crearMateria(MateriaRequest materiaRequest);

    MateriaResponse updateMateria(MateriaUpdateRequest materiaUpdateRequest);

    MateriaResponseList listarMaterias();

    MateriaResponse borrarMateria(Long id);

}
