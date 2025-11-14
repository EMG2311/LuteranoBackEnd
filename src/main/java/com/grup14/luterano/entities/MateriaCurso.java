package com.grup14.luterano.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    @ToString.Exclude
    private Materia materia;

    @ManyToOne(optional = false)
    @ToString.Exclude
    private Curso curso;

    @ManyToOne
    @ToString.Exclude
    private Docente docente;

    @OneToMany(mappedBy = "materiaCurso")
    @ToString.Exclude
    @Builder.Default
    private List<HistorialMateria> historiales = new ArrayList<>();

    @OneToMany(mappedBy = "materiaCurso")
    @ToString.Exclude
    @Builder.Default
    private List<MesaExamen> mesasExamen = new ArrayList<>();
}