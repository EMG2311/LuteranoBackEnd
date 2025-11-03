package com.grup14.luterano.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "modulo", uniqueConstraints = {
        @UniqueConstraint(name = "uk_modulo_orden", columnNames = {"orden"})
})
public class Modulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Secuencia del día: 1..N (cada uno es un “medio módulo”)
     */
    @Column(nullable = false)
    private int orden;

    @Column(nullable = false)
    private LocalTime horaDesde;

    @Column(nullable = false)
    private LocalTime horaHasta;
}