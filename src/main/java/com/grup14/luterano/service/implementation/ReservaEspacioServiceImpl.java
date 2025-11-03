package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.ReservaEspacioDto;
import com.grup14.luterano.entities.*;
import com.grup14.luterano.entities.enums.EstadoReserva;
import com.grup14.luterano.exeptions.ReservaEspacioException;
import com.grup14.luterano.mappers.ReservaEspacioMapper;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.request.espacioAulico.ReservaEspacioFiltroRequest;
import com.grup14.luterano.request.espacioAulico.ReservaEspacioRequest;
import com.grup14.luterano.response.espacioAulico.ReservaEspacioResponse;
import com.grup14.luterano.response.espacioAulico.ReservaEspacioResponseList;
import com.grup14.luterano.service.ReservaEspacioService;
import com.grup14.luterano.specification.ReservaEspacioSpecification;
import com.grup14.luterano.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReservaEspacioServiceImpl implements ReservaEspacioService {

    private final ReservaEspacioRepository reservaRepository;
    private final EspacioAulicoRepository espacioAulicoRepository;
    private final CursoRepository cursoRepository;
    private final AlumnoRepository alumnoRepository;
    private final ModuloRepository moduloRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public ReservaEspacioResponse solicitarReserva(ReservaEspacioRequest request) {

        // 1. OBTENER ID DEL USUARIO DESDE EL CONTEXTO DE SEGURIDAD (Seguro y Correcto)
        Long solicitanteId = SecurityUtils.getCurrentUserId();
        Long cursoId = request.getCursoId(); // Asumimos que ahora el Request tiene getCursoId()

        //  Validar que las entidades base existan (Espacio, Modulo, Curso, User)
        EspacioAulico espacio = espacioAulicoRepository.findById(request.getEspacioAulicoId())
                .orElseThrow(() -> new ReservaEspacioException("Espacio Áulico no encontrado."));
        Modulo modulo = moduloRepository.findById(request.getModuloId())
                .orElseThrow(() -> new ReservaEspacioException("Módulo de Horario no encontrado."));

        User usuario = userRepository.findById(solicitanteId)
                .orElseThrow(() -> new ReservaEspacioException("Usuario solicitante no encontrado en BD."));

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ReservaEspacioException("Curso no encontrado en BD."));


// Obtener la capacidad de Espacio Aúlico
        int capacidadEspacio = espacio.getCapacidad();

// Contar la cantidad de alumnos asignados al curso
        int totalAlumnos = (int) alumnoRepository.countByCursoActual_Id(cursoId);

        if (totalAlumnos > capacidadEspacio) {
            throw new ReservaEspacioException(
                    "La cantidad de alumnos requerida (" + totalAlumnos +
                            ") supera la capacidad máxima del espacio áulico (" + capacidadEspacio + ")."
            );
        }

        //  Validar Disponibilidad (CRÍTICO: Bloqueo de Espacio/Fecha/Módulo)
        boolean isReserved = reservaRepository.existsActiveReservation(
                request.getEspacioAulicoId(),
                request.getFecha(),
                request.getModuloId());

        if (isReserved) {
            throw new ReservaEspacioException("El espacio ya está reservado o pendiente de aprobación en esa fecha y módulo.");
        }

        //  Crear Reserva en estado PENDIENTE
        ReservaEspacio nuevaReserva = ReservaEspacio.builder()
                .espacioAulico(espacio)
                .modulo(modulo)
                .curso(curso)
                .cantidadAlumnos(totalAlumnos)
                .usuario(usuario)
                .fecha(request.getFecha())
                .motivoSolicitud(request.getMotivoSolicitud())
                .estado(EstadoReserva.PENDIENTE) // Se setea por defecto en la entidad, pero lo explicitamos
                .build();

        ReservaEspacio savedReserva = reservaRepository.save(nuevaReserva);
        ReservaEspacioDto dto = ReservaEspacioMapper.toDto(savedReserva);

        return ReservaEspacioResponse.builder()
                .reservaEspacioDto(dto) // El DTO mapeado
                .code(0)
                .mensaje("Reserva solicitada exitosamente.")
                .build();

    }

    @Override
    @Transactional
    public ReservaEspacioResponse cancelarReserva(Long reservaId) {

        // 1. Obtener el ID del usuario autenticado
        Long userId = SecurityUtils.getCurrentUserId();

        // 2. Buscar la reserva
        ReservaEspacio reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ReservaEspacioException("Reserva no encontrada con ID: " + reservaId));

        // 3. Validar PROPIEDAD
        if (!reserva.getUsuario().getId().equals(userId)) {
            throw new SecurityException("No tiene permiso para cancelar esta reserva.");
        }

        // 4. Marcar como cancelada (la entidad tiene la lógica de estado)
        reserva.marcarComoCancelada();

        // 5. Guardar y responder
        ReservaEspacio savedReserva = reservaRepository.save(reserva);
        ReservaEspacioDto dto = ReservaEspacioMapper.toDto(savedReserva);

        return ReservaEspacioResponse.builder()
                .reservaEspacioDto(dto)
                .code(0)
                .mensaje("Reserva cancelada exitosamente.")
                .build();

    }

    @Override
    public ReservaEspacioResponseList listReservas() {
        List<ReservaEspacioDto> reservas = reservaRepository.findAll().stream()
                .map(ReservaEspacioMapper::toDto)
                .collect(Collectors.toList());

        return ReservaEspacioResponseList.builder()
                .reservaEspacioDtos(reservas)
                .code(0)
                .mensaje("Lista de reservas obtenida correctamente")
                .build();
    }


    @Override
    public ReservaEspacioResponseList obtenerReservas(ReservaEspacioFiltroRequest request) {
        // Filtro por ID de Espacio Aúlico,Usuario y Estado de Reserva
        Specification<ReservaEspacio> spec = Specification.where(
                        ReservaEspacioSpecification.espacioAulicoIdEquals(request.getEspacioAulicoId()))
                .and(ReservaEspacioSpecification.usuarioIdEquals(request.getUsuarioId()))
                .and(ReservaEspacioSpecification.estadoEquals(request.getEstado()));


        List<ReservaEspacioDto> reservas = reservaRepository.findAll(spec).stream()
                .map(ReservaEspacioMapper::toDto)
                .collect(Collectors.toList());

        return ReservaEspacioResponseList.builder()
                .reservaEspacioDtos(reservas)
                .code(0)
                .mensaje("OK")
                .build();

    }


    @Override
    public ReservaEspacioResponse aprobarReserva(Long reservaId) {

        ReservaEspacio reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ReservaEspacioException("Error: Reserva con ID " + reservaId + " no encontrada."));

        if (!Objects.equals(reserva.getEstado(), EstadoReserva.PENDIENTE)) {
            throw new ReservaEspacioException("Solo se pueden aprobar reservas en estado PENDIENTE. Estado actual: " + reserva.getEstado());
        }

        // Re-Validación de unicidad/disponibilidad: verifica si otra reserva ya tomó el espacio.
        boolean isAnotherActive = reservaRepository.existsActiveReservationExcluding(
                reserva.getEspacioAulico().getId(),
                reserva.getFecha(),
                reserva.getModulo().getId(),
                reservaId // Excluye la reserva actual de la búsqueda
        );

        if (isAnotherActive) {
            throw new ReservaEspacioException("No se puede aprobar: El espacio ha sido tomado por otra reserva APROBADA o PENDIENTE desde que se solicitó esta.");
        }

        reserva.marcarComoAprobada();
        ReservaEspacio savedReserva = reservaRepository.save(reserva);

        ReservaEspacioDto dto = ReservaEspacioMapper.toDto(savedReserva);
        return ReservaEspacioResponse.builder()
                .reservaEspacioDto(dto)
                .code(0)
                .mensaje("Reserva aprobada exitosamente.")
                .build();

    }

    @Override
    @Transactional
    public ReservaEspacioResponse denegarReserva(Long reservaId, String motivo) {

        if (motivo == null || motivo.trim().isEmpty()) {
            throw new ReservaEspacioException("El motivo de denegación es obligatorio.");
        }

        ReservaEspacio reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ReservaEspacioException("Error: Reserva con ID " + reservaId + " no encontrada."));

        try {
            reserva.marcarComoDenegada(motivo);

            ReservaEspacio updatedReserva = reservaRepository.save(reserva);


            ReservaEspacioDto dto = ReservaEspacioMapper.toDto(updatedReserva);
            return ReservaEspacioResponse.builder()
                    .reservaEspacioDto(dto)
                    .code(0)
                    .mensaje("Reserva denegada .")
                    .build();

        } catch (IllegalStateException e) {
            throw new ReservaEspacioException("Error al denegar: " + e.getMessage());
        }
    }

}



