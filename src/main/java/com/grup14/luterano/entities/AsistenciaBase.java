package com.grup14.luterano.entities;

import com.grup14.luterano.entities.enums.EstadoAsistencia;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@SuperBuilder
public abstract class AsistenciaBase {

    @Column(nullable = false)
    @PastOrPresent(message = "No se puede poner una fecha posterior a la actual")
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAsistencia estado;
    @Column(length = 300)
    private String observacion;
    @ManyToOne
    private User usuario;
}
