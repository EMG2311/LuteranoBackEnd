package com.grup14.luterano.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity@Builder@Data@AllArgsConstructor@NoArgsConstructor
public class ActaExamen {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDate fecha;
    private String detalle;

    @ManyToOne
    private MesaExamen mesaExamen;
}