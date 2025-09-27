package com.grup14.luterano.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialMateria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "historial_curso_id")
    private HistorialCurso historialCurso;

    @ManyToOne(optional = false)
    @JoinColumn(name = "materia_curso_id")
    private MateriaCurso materiaCurso;

    private BigDecimal promedio; // promedio de esa materia en particular

    @OneToMany(mappedBy = "historialMateria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Calificacion> calificaciones = new ArrayList<>();
}