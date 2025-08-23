package com.grup14.luterano.entities;

import com.grup14.luterano.entities.enums.Nivel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity@Builder@NoArgsConstructor@AllArgsConstructor@Data
public class Materia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Nivel nivel;

    @OneToMany(mappedBy = "materia")
    private List<MateriaCurso> dictados = new ArrayList<>();
}