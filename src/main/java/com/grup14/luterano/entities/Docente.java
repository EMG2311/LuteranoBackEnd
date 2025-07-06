package com.grup14.luterano.entities;

import com.grup14.luterano.commond.Persona;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity@SuperBuilder@AllArgsConstructor@NoArgsConstructor
public class Docente extends Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToMany
    private List<Materia> materias;

}