package com.grup14.luterano.service;

import com.grup14.luterano.entities.ReservaEspacio;
import com.grup14.luterano.entities.enums.EstadoReserva;
import com.grup14.luterano.request.espacioAulico.ReservaEspacioFiltroRequest;
import com.grup14.luterano.request.espacioAulico.ReservaEspacioRequest;
import com.grup14.luterano.response.espacioAulico.ReservaEspacioResponse;
import com.grup14.luterano.response.espacioAulico.ReservaEspacioResponseList;

import java.util.List;

public interface ReservaEspacioService {

    ReservaEspacioResponse solicitarReserva(ReservaEspacioRequest request);

    ReservaEspacioResponse cancelarReserva(Long reservaId );

    // Obtener lista de reservas (filtradas por estado, o todas si 'estado' es null)
    ReservaEspacioResponseList listReservas();
    ReservaEspacioResponseList obtenerReservas(ReservaEspacioFiltroRequest reservaEspacioFiltroRequest);

    // --- Gestión Administrativa ---
    ReservaEspacioResponse aprobarReserva(Long reservaId);
    ReservaEspacioResponse denegarReserva(Long reservaId, String motivo);


}
