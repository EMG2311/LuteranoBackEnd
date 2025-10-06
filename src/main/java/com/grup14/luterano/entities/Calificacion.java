package com.grup14.luterano.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Builder@Data@NoArgsConstructor@AllArgsConstructor
public class Calificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Max(value = 10, message = "La nota no puede ser mayor a 10")
    @Min(value = 1, message = "La nota no puede ser menor a 1")
    private Integer nota;

    private int numeroNota; // ejemplo: 1er parcial, 2do parcial, etc.

    private LocalDate fecha;

    @ManyToOne(optional = false)
    @JoinColumn(name = "historial_materia_id")
    private HistorialMateria historialMateria;
}