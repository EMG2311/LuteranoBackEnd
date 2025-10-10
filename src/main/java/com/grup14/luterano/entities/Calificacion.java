package com.grup14.luterano.entities;

import com.grup14.luterano.entities.enums.Cuatrimestre;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "calificacion",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_calif_hist_etapa_nro",
                columnNames = {"historial_materia_id","etapa","numero_nota"}
        ),
        indexes = {
                @Index(name = "ix_calif_hm_etapa", columnList = "historial_materia_id,etapa")
        }
)
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Calificacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Max(value = 10, message = "La nota no puede ser mayor a 10")
    @Min(value = 1,  message = "La nota no puede ser menor a 1")
    private Integer nota;

    @Min(1) @Max(4)
    @Column(name = "numero_nota", nullable = false)
    private int numeroNota;

    @Min(1)
    @Max(2)
    @Column(nullable = false)
    private int etapa;

    private LocalDate fecha;

    @ManyToOne(optional = false)
    @JoinColumn(name = "historial_materia_id", nullable = false)
    private HistorialMateria historialMateria;
}