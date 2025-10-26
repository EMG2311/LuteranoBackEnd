package com.grup14.luterano.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity@Builder@Data@AllArgsConstructor@NoArgsConstructor
public class ActaExamen {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "mesa_id", unique = true)
    private MesaExamen mesa;

    private String numeroActa;
    private LocalDate fechaCierre;

    @Builder.Default
    private boolean cerrada = false;

    private String observaciones;
}