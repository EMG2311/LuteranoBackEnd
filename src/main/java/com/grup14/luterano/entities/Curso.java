package com.grup14.luterano.entities;

import com.grup14.luterano.entities.enums.Division;
import com.grup14.luterano.entities.enums.Nivel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data@AllArgsConstructor@NoArgsConstructor@Builder
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(nullable = false)
    @Min(value = 1,message = "El año tiene que ser mayor/igual a 1")
    @Max(value = 6,message = "El año tiene qeu ser menor/igual a 6")
    private int anio;
    @Enumerated(EnumType.STRING)
    private Division division;
    @Enumerated(EnumType.STRING)
    private Nivel nivel;

    @OneToOne
    @JoinColumn(name = "aula_id", unique = true)
    private Aula aula;

    @OneToMany(mappedBy = "curso")
    @Builder.Default // <-- inicializa la lista para evitar NullPointerException
    private List<MateriaCurso> dictados = new ArrayList<>();
}