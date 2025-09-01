package com.grup14.luterano.entities;

import com.grup14.luterano.entities.enums.Division;
import com.grup14.luterano.entities.enums.Nivel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data@AllArgsConstructor@NoArgsConstructor@Builder
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int anio;
    @Enumerated(EnumType.STRING)
    private Division division;
    @Enumerated(EnumType.STRING)
    private Nivel nivel;

    @OneToOne
    @JoinColumn(name = "aula_id", unique = true)
    private Aula aula;

    @OneToMany(mappedBy = "curso")
    private List<MateriaCurso> dictados = new ArrayList<>();
}