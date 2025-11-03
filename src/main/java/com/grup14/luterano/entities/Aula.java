package com.grup14.luterano.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Aula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String ubicacion;
    @Column(nullable = false)
    private Integer capacidad;
    @OneToOne(mappedBy = "aula")
    private Curso curso;


}