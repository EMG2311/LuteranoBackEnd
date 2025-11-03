package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.AsistenciaDocente;
import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.exeptions.AsistenciaDocenteException;
import com.grup14.luterano.mappers.AsistenciaDocenteMapper;
import com.grup14.luterano.repository.AsistenciaDocenteRepository;
import com.grup14.luterano.repository.DocenteRepository;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.AsistenciaDocenteUpdateRequest;
import com.grup14.luterano.response.asistenciaDocente.AsistenciaDocenteResponse;
import com.grup14.luterano.response.asistenciaDocente.AsistenciaDocenteResponseList;
import com.grup14.luterano.service.AsistenciaDocenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AsistenciaDocenteServiceImpl implements AsistenciaDocenteService {

    private final AsistenciaDocenteRepository asistenciaDocenteRepo;
    private final DocenteRepository docenteRepo;
    private final UserRepository userRepo;

    private User currentUserOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        return userRepo.findByEmail(auth.getName()).orElse(null);
    }

    @Override
    @Transactional
    public AsistenciaDocenteResponse upsert(AsistenciaDocenteUpdateRequest req) {
        if (req.getDocenteId() == null || req.getFecha() == null || req.getEstado() == null) {
            throw new AsistenciaDocenteException("Debe indicar docenteId, fecha y estado");
        }

        Docente docente = docenteRepo.findById(req.getDocenteId())
                .orElseThrow(() -> new AsistenciaDocenteException("Docente no encontrado"));

        var entidad = asistenciaDocenteRepo.findByDocente_IdAndFecha(req.getDocenteId(), req.getFecha())
                .orElseGet(() -> AsistenciaDocente.builder()
                        .docente(docente)
                        .fecha(req.getFecha())
                        .estado(req.getEstado())
                        .build());

        asistenciaDocenteRepo.save(entidad);

        return AsistenciaDocenteResponse.builder()
                .asistenciaDocenteDto(AsistenciaDocenteMapper.toDto(entidad))
                .code(0)
                .mensaje("OK")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AsistenciaDocenteResponseList listarPorDocenteYFecha(Long docenteId, LocalDate fecha) {
        if (docenteId == null || fecha == null) {
            throw new AsistenciaDocenteException("Debe indicar docenteId y fecha");
        }
        docenteRepo.findById(docenteId)
                .orElseThrow(() -> new AsistenciaDocenteException("Docente no encontrado"));

        var dto = asistenciaDocenteRepo.findByDocente_IdAndFecha(docenteId, fecha)
                .map(AsistenciaDocenteMapper::toDto).orElse(null);

        return AsistenciaDocenteResponseList.builder()
                .items(dto == null ? java.util.List.of() : java.util.List.of(dto))
                .code(200)
                .mensaje("OK")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AsistenciaDocenteResponseList listarPorFecha(LocalDate fecha) {
        if (fecha == null) throw new AsistenciaDocenteException("Debe indicar fecha");

        var list = asistenciaDocenteRepo.findByFecha(fecha);
        return AsistenciaDocenteResponseList.builder()
                .items(AsistenciaDocenteMapper.toDtoList(list))
                .code(200)
                .mensaje("OK")
                .build();
    }
}
