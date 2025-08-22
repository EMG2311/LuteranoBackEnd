package com.grup14.luterano.service;

import com.grup14.luterano.response.MateriaCurso.MateriaCursoListResponse;
import com.grup14.luterano.response.MateriaCurso.MateriaCursoResponse;

import java.util.List;

public interface MateriaCursoService {
    MateriaCursoResponse asignarMateriaACurso(Long materiaId, Long cursoId);
    MateriaCursoResponse quitarMateriaDeCurso(Long materiaId, Long cursoId);
    MateriaCursoListResponse listarMateriasDeCurso(Long cursoId);
    MateriaCursoListResponse listarCursosDeMateria(Long materiaId);
    MateriaCursoResponse desasignarDocente(Long materiaId, Long cursoId);
    MateriaCursoResponse asignarDocente(Long materiaId, Long cursoId, Long docenteId);
}
