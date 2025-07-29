package com.grup14.luterano.entities;

import com.grup14.luterano.entities.enums.Division;
import com.grup14.luterano.entities.enums.Nivel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data@AllArgsConstructor@NoArgsConstructor@Builder
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private int numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Division division;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Nivel nivel;

    @OneToOne
    @JoinColumn(name = "aula_id", unique = true)
    private Aula aula;

    @ManyToMany(mappedBy = "cursos")
    private List<Materia> materias;
}