package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.EspacioAulicoDto;
import com.grup14.luterano.entities.EspacioAulico;
import com.grup14.luterano.exeptions.EspacioAulicoException;
import com.grup14.luterano.mappers.EspacioAulicoMapper;
import com.grup14.luterano.repository.EspacioAulicoRepository;
import com.grup14.luterano.repository.UserRepository;
import com.grup14.luterano.request.espacioAulico.EspacioAulicoRequest;
import com.grup14.luterano.request.espacioAulico.EspacioAulicoUpdateRequest;
import com.grup14.luterano.response.espacioAulico.EspacioAulicoResponse;
import com.grup14.luterano.response.espacioAulico.EspacioAulicoResponseList;
import com.grup14.luterano.service.EspacioAulicoService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class EspacioAulicoServiceImpl implements EspacioAulicoService {

    private final EspacioAulicoRepository repository;

    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(DocenteServiceImpl.class);

    public EspacioAulicoServiceImpl(EspacioAulicoRepository repository,
                                    UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public EspacioAulicoResponse crearEspacioAulico(EspacioAulicoRequest request) {

        repository.findByNombreIgnoreCase(request.getNombre())
                .ifPresent(espacio -> {
                    throw new EspacioAulicoException("Ya existe un espacio áulico con el nombre: " + request.getNombre());
                });

        EspacioAulico espacioAulico = EspacioAulicoMapper.toEntity(request);

        try {
            repository.save(espacioAulico);
            logger.info("Espacio áulico creado con ID: {}", espacioAulico.getId());
            return EspacioAulicoResponse.builder()
                    .espacioAulicoDto(EspacioAulicoMapper.toDto(espacioAulico))
                    .code(0)
                    .mensaje("Espacio áulico creado exitosamente")
                    .build();
        } catch (Exception e) {
            logger.error("Error al crear el espacio áulico: {}", e.getMessage());
            throw new EspacioAulicoException("Error al crear el espacio áulico: " + e.getMessage());
        }

    }

    @Override
    @Transactional
    public EspacioAulicoResponse updateEspacioAulico(EspacioAulicoUpdateRequest espacioAulicoUpdateRequest) {

        EspacioAulico espacioExistente = repository.findById(espacioAulicoUpdateRequest.getId())
                .orElseThrow(() -> new EspacioAulicoException("No se encontró el espacio áulico con ID: " + espacioAulicoUpdateRequest.getId()));

        //  Actualizar campos
        if (!espacioAulicoUpdateRequest.getNombre().isEmpty())
            espacioExistente.setNombre(espacioAulicoUpdateRequest.getNombre());
        if (!espacioAulicoUpdateRequest.getUbicacion().isEmpty())
            espacioExistente.setUbicacion(espacioAulicoUpdateRequest.getUbicacion());
        if (espacioAulicoUpdateRequest.getCapacidad() != null)
            espacioExistente.setCapacidad(espacioAulicoUpdateRequest.getCapacidad());

        espacioExistente = repository.save(espacioExistente);
        logger.info("Espacio áulico actualizado con ID: {}", espacioExistente.getId());
        return EspacioAulicoResponse.builder()
                .espacioAulicoDto(EspacioAulicoMapper.toDto(espacioExistente))
                .code(0)
                .mensaje("Espacio áulico actualizado correctamente")
                .build();
    }

    @Override
    public EspacioAulicoResponse deleteEspacioAulico(Long id) {

        EspacioAulico espacioAulico = repository.findById(id)
                .orElseThrow(() -> new EspacioAulicoException("No se encontró el espacio áulico con ID: " + id));

        repository.delete(espacioAulico);
        logger.info("Espacio áulico eliminado con ID: {}", id);
        return EspacioAulicoResponse.builder()
                .code(0)
                .mensaje("Espacio áulico eliminado correctamente")
                .build();

    }

    @Override
    public EspacioAulicoResponseList listEspacioAulico() {

        List<EspacioAulicoDto> espacios = repository.findAll()
                .stream()
                .map(EspacioAulicoMapper::toDto)
                .toList();
        return EspacioAulicoResponseList.builder()
                .espacioAulicoDtos(espacios)
                .code(0)
                .mensaje("Lista de espacios áulicos obtenida correctamente")
                .build();

    }
}
