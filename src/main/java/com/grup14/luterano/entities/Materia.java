package com.grup14.luterano.entities;

import com.grup14.luterano.entities.enums.Nivel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity@Builder@NoArgsConstructor@AllArgsConstructor@Data
public class Materia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombreMateria;
    @Column(nullable = false)
    private String descipcion;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Nivel nivel;

    @ManyToMany
    @JoinTable(
            name = "materia_curso",
            joinColumns = @JoinColumn(name = "materia_id"),
            inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    private List<Curso> cursos;

}