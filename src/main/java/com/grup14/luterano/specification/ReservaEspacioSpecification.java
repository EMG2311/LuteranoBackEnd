package com.grup14.luterano.specification;

import com.grup14.luterano.entities.ReservaEspacio;
import com.grup14.luterano.entities.enums.EstadoReserva;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class ReservaEspacioSpecification {


     // Filtra las reservas por el ID del usuario solicitante.
    public static Specification<ReservaEspacio> usuarioIdEquals(Long userId) {
        if (userId == null) {
            return null; // No aplica filtro si el ID es nulo
        }
        return (root, query, criteriaBuilder) -> {
            // Se asume que la entidad ReservaEspacio tiene un campo 'usuario' (ManyToOne)
            Join<ReservaEspacio, Object> userJoin = root.join("usuario", JoinType.INNER);
            return criteriaBuilder.equal(userJoin.get("id"), userId);
        };
    }

    //Filtra las reservas por el estado de la reserva (PENDIENTE, APROBADA, etc.).

    public static Specification<ReservaEspacio> estadoEquals(EstadoReserva estado) {
        if (estado == null) {
            return null; // No aplica filtro si el estado es nulo
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("estado"), estado);
    }

    //Filtra las reservas por el ID del espacio áulico.

    public static Specification<ReservaEspacio> espacioAulicoIdEquals(Long espacioId) {
        if (espacioId == null) {
            return null; // No aplica filtro si el ID es nulo
        }
        return (root, query, criteriaBuilder) -> {
            // Se asume que la entidad ReservaEspacio tiene un campo 'espacioAulico' (ManyToOne)
            Join<ReservaEspacio, Object> espacioJoin = root.join("espacioAulico", JoinType.INNER);
            return criteriaBuilder.equal(espacioJoin.get("id"), espacioId);
        };
    }
}
