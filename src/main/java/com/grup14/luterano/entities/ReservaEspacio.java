package com.grup14.luterano.entities;

import com.grup14.luterano.entities.enums.DiaSemana;
import com.grup14.luterano.entities.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "reserva_espacio_aulicos",
        // RESTRICCIÓN CLAVE: NO debe haber dos reservas APROBADAS/PENDIENTES para el mismo espacio, el mismo día y el mismo módulo.
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_espacio_fecha_modulo",
                        columnNames = {"espacio_aulico_id", "fecha", "modulo_id"}
                )
        }
)

public class ReservaEspacio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="fecha", nullable = false)
    private LocalDate fecha;

    @ManyToOne(optional = false)
    private Curso curso;

    @ManyToOne(optional = false)
    @JoinColumn(name = "espacio_aulico_id", nullable = false)
    private EspacioAulico espacioAulico;

    @ManyToOne(optional=false) @JoinColumn(name="modulo_id", nullable=false)
    private Modulo modulo;

    @ManyToOne(optional=false)
    @JoinColumn(name = "usuario_solicitante_id", nullable = false)
    private User usuarioSolicitante;

    private String motivoSolicitud;

    //  ESTADO Y GESTIÓN ADMINISTRATIVA
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado = EstadoReserva.PENDIENTE; // Inicia siempre PENDIENTE

    private String motivoDenegacion; // Usado solo si el estado es DENEGADA


}
