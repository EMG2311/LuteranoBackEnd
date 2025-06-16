package com.grup14.luterano.entities;

import com.grup14.luterano.entities.enums.Nivel;
import jakarta.persistence.*;

@Entity
public class Materia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombreMateria;

    @Enumerated(EnumType.STRING)
    private Nivel nivel;

    @ManyToOne
    private Curso curso;
}