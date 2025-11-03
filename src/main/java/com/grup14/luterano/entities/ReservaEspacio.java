package com.grup14.luterano.entities;

import com.grup14.luterano.entities.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ReservaEspacio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Curso curso;

    private int cantidadAlumnos;

    @ManyToOne(optional = false)
    @JoinColumn(name = "espacio_aulico_id", nullable = false)
    private EspacioAulico espacioAulico;

    @ManyToOne(optional = false)
    @JoinColumn(name = "modulo_id", nullable = false)
    private Modulo modulo;

    // --- Información Temporal y Solicitante ---

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_solicitante_id", nullable = false)
    private User usuario;

    private String motivoSolicitud;

    // --- Gestión Administrativa y Estado ---

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado = EstadoReserva.PENDIENTE; // Inicia siempre PENDIENTE

    private String motivoDenegacion; // Usado solo si el estado es DENEGADA

    // --- Métodos Internos de Gestión de Estado ---

    public boolean isBloqueante() {
        return this.estado == EstadoReserva.PENDIENTE || this.estado == EstadoReserva.APROBADA;
    }

    public void marcarComoAprobada() {
        if (!Objects.equals(this.estado, EstadoReserva.PENDIENTE)) {
            throw new IllegalStateException("Solo se puede aprobar una reserva en estado PENDIENTE.");
        }
        this.estado = EstadoReserva.APROBADA;
    }

    public void marcarComoDenegada(String motivo) {
        if (!Objects.equals(this.estado, EstadoReserva.PENDIENTE)) {
            throw new IllegalStateException("Solo se puede denegar una reserva en estado PENDIENTE.");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de denegación es obligatorio.");
        }
        this.estado = EstadoReserva.DENEGADA;
        this.motivoDenegacion = motivo;
    }

    public void marcarComoCancelada() {
        // La cancelación permite PENDIENTE o APROBADA
        if (this.estado == EstadoReserva.DENEGADA || this.estado == EstadoReserva.CANCELADA) {
            throw new IllegalStateException("Una reserva ya denegada o cancelada no puede ser cancelada nuevamente.");
        }
        this.estado = EstadoReserva.CANCELADA;
    }

}
