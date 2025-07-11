package com.grup14.luterano.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity@Builder@Data@AllArgsConstructor@NoArgsConstructor
public class Horario {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private DiaSemana diaSemana;

    @ManyToOne
    private Docente docente;

    private LocalTime horaDesde;
    private LocalTime horaHasta;

    private boolean colegio;

    @ManyToOne
    private Materia materia;
}
