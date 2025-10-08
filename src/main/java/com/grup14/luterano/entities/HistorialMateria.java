package com.grup14.luterano.entities;

import com.grup14.luterano.entities.enums.ConductaValor;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HistorialMateria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "historial_curso_id")
    private HistorialCurso historialCurso;

    @ManyToOne(optional = false) @JoinColumn(name = "materia_curso_id")
    private MateriaCurso materiaCurso;

    // Conducta por etapa (map persistido en tabla secundaria)
    @ElementCollection
    @CollectionTable(
            name = "historial_materia_conducta",
            joinColumns = @JoinColumn(name = "historial_materia_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_hm_conducta_etapa",
                    columnNames = {"historial_materia_id","etapa"}
            )
    )
    @MapKeyColumn(name = "etapa")
    @Enumerated(EnumType.STRING)
    @Column(name = "valor", nullable = false)
    private Map<Integer, ConductaValor> conductaPorEtapa = new HashMap<>();

    @OneToMany(mappedBy = "historialMateria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Calificacion> calificaciones = new ArrayList<>();

}