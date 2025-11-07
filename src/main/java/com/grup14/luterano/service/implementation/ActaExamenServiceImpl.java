package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.ActaExamen;
import com.grup14.luterano.entities.MesaExamen;
import com.grup14.luterano.entities.enums.EstadoMesaExamen;
import com.grup14.luterano.mappers.ActaExamenMapper;
import com.grup14.luterano.repository.ActaExamenRepository;
import com.grup14.luterano.repository.MesaExamenRepository;
import com.grup14.luterano.request.actaExamen.ActaCreateRequest;
import com.grup14.luterano.request.actaExamen.ActaUpdateRequest;
import com.grup14.luterano.response.actaExamen.ActaExamenListResponse;
import com.grup14.luterano.response.actaExamen.ActaExamenResponse;
import com.grup14.luterano.service.ActaExamenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ActaExamenServiceImpl implements ActaExamenService {

    private static final Logger log = LoggerFactory.getLogger(ActaExamenServiceImpl.class);

    private final ActaExamenRepository actaRepo;
    private final MesaExamenRepository mesaRepo;

    @Override
    public ActaExamenResponse generar(ActaCreateRequest req) {
        if (req.getMesaId() == null) {
            return ActaExamenResponse.builder().code(-1).mensaje("mesaId es requerido").build();
        }

        MesaExamen mesa = mesaRepo.findById(req.getMesaId())
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        if (mesa.getEstado() != EstadoMesaExamen.FINALIZADA) {
            return ActaExamenResponse.builder().code(-1)
                    .mensaje("La mesa debe estar FINALIZADA para generar el acta")
                    .build();
        }

        // Idempotente: si ya existe, devolverla
        var existente = actaRepo.findByMesa_Id(mesa.getId());
        if (existente.isPresent()) {
            log.info("Acta ya existe para mesa {}. Devuelvo existente.", mesa.getId());
            return ActaExamenResponse.builder()
                    .code(0).mensaje("Acta ya existente")
                    .acta(ActaExamenMapper.toDto(existente.get()))
                    .build();
        }

        // generar número (si no vino en request)
        String numero = (req.getNumeroActa() != null && !req.getNumeroActa().isBlank())
                ? req.getNumeroActa()
                : generarNumeroActa(mesa);

        ActaExamen acta = ActaExamen.builder()
                .mesa(mesa)
                .numeroActa(numero)
                .fechaCierre(LocalDate.now())
                .cerrada(true)
                .observaciones(req.getObservaciones())
                .build();

        actaRepo.save(acta);
        log.info("Acta generada id={} para mesa={} numero={}", acta.getId(), mesa.getId(), numero);
        return ActaExamenResponse.builder().code(0).mensaje("Acta generada").acta(ActaExamenMapper.toDto(acta)).build();
    }

    @Override
    public ActaExamenResponse actualizar(ActaUpdateRequest req) {
        ActaExamen acta = actaRepo.findById(req.getId())
                .orElseThrow(() -> new RuntimeException("Acta no encontrada"));

        if (req.getNumeroActa() != null && !req.getNumeroActa().isBlank())
            acta.setNumeroActa(req.getNumeroActa());
        if (req.getObservaciones() != null)
            acta.setObservaciones(req.getObservaciones());

        actaRepo.save(acta);
        return ActaExamenResponse.builder().code(0).mensaje("Acta actualizada").acta(ActaExamenMapper.toDto(acta)).build();
    }

    @Override
    public ActaExamenResponse eliminar(Long id) {
        actaRepo.deleteById(id);
        return ActaExamenResponse.builder().code(0).mensaje("Acta eliminada").build();
    }

    @Override
    public ActaExamenResponse obtenerPorId(Long id) {
        var acta = actaRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Acta no encontrada"));
        return ActaExamenResponse.builder().code(1).mensaje("OK").acta(ActaExamenMapper.toDto(acta)).build();
    }

    @Override
    public ActaExamenResponse obtenerPorMesa(Long mesaId) {
        var acta = actaRepo.findByMesa_Id(mesaId)
                .orElseThrow(() -> new RuntimeException("No hay acta para la mesa " + mesaId));
        return ActaExamenResponse.builder().code(0).mensaje("OK").acta(ActaExamenMapper.toDto(acta)).build();
    }

    @Override
    public ActaExamenResponse obtenerPorNumero(String numeroActa) {
        var acta = actaRepo.findByNumeroActa(numeroActa)
                .orElseThrow(() -> new RuntimeException("No hay acta con número " + numeroActa));
        return ActaExamenResponse.builder().code(0).mensaje("OK").acta(ActaExamenMapper.toDto(acta)).build();
    }

    @Override
    public ActaExamenListResponse buscarPorNumeroLike(String q) {
        var list = actaRepo.searchByNumeroLike(q);
        return ActaExamenListResponse.builder()
                .code(0).mensaje("OK").total(list.size())
                .actas(list.stream().map(ActaExamenMapper::toDto).toList())
                .build();
    }

    @Override
    public ActaExamenListResponse listarPorTurno(Long turnoId) {
        var list = actaRepo.listarPorTurno(turnoId);
        return ActaExamenListResponse.builder()
                .code(0).mensaje("OK").total(list.size())
                .actas(list.stream().map(ActaExamenMapper::toDto).toList())
                .build();
    }

    @Override
    public ActaExamenListResponse listarPorCurso(Long cursoId) {
        var list = actaRepo.listarPorCurso(cursoId);
        return ActaExamenListResponse.builder()
                .code(0).mensaje("OK").total(list.size())
                .actas(list.stream().map(ActaExamenMapper::toDto).toList())
                .build();
    }

    @Override
    public ActaExamenListResponse listarEntreFechas(LocalDate desde, LocalDate hasta) {
        var list = actaRepo.listarEntreFechas(desde, hasta);
        return ActaExamenListResponse.builder()
                .code(0).mensaje("OK").total(list.size())
                .actas(list.stream().map(ActaExamenMapper::toDto).toList())
                .build();
    }

    // ------ Helpers ------

    private static String generarNumeroActa(MesaExamen mesa) {
        // Formato ejemplo: ACTA-<anioTurno>-<mesTurno>-<mesaId>-<hash>
        String suf = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        var t = mesa.getTurno();
        String mes = (t != null && t.getFechaInicio() != null) ? String.valueOf(t.getFechaInicio().getMonthValue()) : "00";
        String anio = (t != null) ? String.valueOf(t.getAnio()) : "0000";
        return "ACTA-" + anio + "-" + mes + "-" + mesa.getId() + "-" + suf;
    }


}
