package com.grup14.luterano.entities;

import com.grup14.luterano.entities.enums.EstadoMesaExamen;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MesaExamen {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDate fecha;

    @ManyToOne
    private Modulo modulo;

    @ManyToOne
    private Aula aula;

    @Enumerated(EnumType.STRING)
    private EstadoMesaExamen estado;
}
