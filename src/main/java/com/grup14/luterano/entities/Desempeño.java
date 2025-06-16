package com.grup14.luterano.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity@Builder@AllArgsConstructor@NoArgsConstructor@Data
public class Desempeño {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private int calificacion;
    private String comentario;

    @ManyToOne
    private Docente docente;
}
