package com.grup14.luterano.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CicloLectivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    // No se recomienda usar @PrePersist en la entidad para lógica compleja de negocio,
    // pero se incluye la lógica para generar el nombre.
    @PrePersist
    @PreUpdate
    public void generateNombre() {
        if (this.fechaDesde != null) {
            this.nombre = "Ciclo Lectivo " + this.fechaDesde.getYear();
        }
    }
}
