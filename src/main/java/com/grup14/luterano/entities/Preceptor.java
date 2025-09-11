package com.grup14.luterano.entities;

import com.grup14.luterano.commond.Persona;
import com.grup14.luterano.commond.PersonaConUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity@SuperBuilder@Data@AllArgsConstructor@NoArgsConstructor
public class Preceptor extends PersonaConUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "preceptor")
    @Builder.Default
    private List<Curso> cursos = new ArrayList<>();
}
