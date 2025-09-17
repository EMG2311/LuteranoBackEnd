package com.grup14.luterano.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MateriaCurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Materia materia;

    @ManyToOne(optional = false)
    private Curso curso;

    @ManyToOne
    private Docente docente;

    @OneToMany(mappedBy = "materiaCurso")
    private List<HistorialMateria> historiales = new ArrayList<>();
}