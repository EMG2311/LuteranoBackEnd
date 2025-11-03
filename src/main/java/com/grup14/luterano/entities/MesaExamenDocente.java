package com.grup14.luterano.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "mesa_examen_docente",
        uniqueConstraints = @UniqueConstraint(columnNames = {"mesa_examen_id", "docente_id"}))
public class MesaExamenDocente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mesa_examen_id")
    private MesaExamen mesaExamen;

    @ManyToOne(optional = false)
    @JoinColumn(name = "docente_id")
    private Docente docente;

    @Column(nullable = false)
    private boolean esDocenteMateria; // true si el docente da la materia
}