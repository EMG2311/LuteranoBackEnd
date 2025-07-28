package com.grup14.luterano.entities;

import com.grup14.luterano.commond.Persona;
import com.grup14.luterano.commond.PersonaConUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Entity@SuperBuilder@AllArgsConstructor@NoArgsConstructor@Data
public class Docente extends PersonaConUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    private Set<Materia> materias;

}