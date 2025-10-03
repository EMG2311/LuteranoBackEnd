package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.CicloLectivoDto;
import com.grup14.luterano.entities.CicloLectivo;
import com.grup14.luterano.exeptions.AulaException;
import com.grup14.luterano.exeptions.CicloLectivoException;
import com.grup14.luterano.mappers.CicloLectivoMapper;
import com.grup14.luterano.repository.CicloLectivoRepository;
import com.grup14.luterano.response.cicloLectivo.CicloLectivoResponse;
import com.grup14.luterano.response.cicloLectivo.CicloLectivoResponseList;
import com.grup14.luterano.service.CicloLectivoService;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
public class CicloLectivoServiceImpl implements CicloLectivoService {

    private static final Logger logger = LoggerFactory.getLogger(CicloLectivoServiceImpl.class);

    @Autowired
    private CicloLectivoRepository cicloLectivoRepository;

    //Lógica compartida para construir un CicloLectivo para un año dado.

    private CicloLectivo construirCicloLectivo(int anio) {
        // 1. Validar duplicados
        String nombreCiclo = "Ciclo Lectivo " + anio;
        if (cicloLectivoRepository.existsByNombre(nombreCiclo)) {
            logger.warn("Intento fallido de crear ciclo lectivo: El año {} ya existe.", anio);
            throw new CicloLectivoException("Ya existe un ciclo lectivo para el año: " + anio);
        }

        // 2. Construir fechas: 01/01 a las 00:00:00 y 31/12 a las 23:59:59
        // Nota: Usamos LocalDate para simplificar, pero la lógica de fechas es correcta.
        LocalDate fechaDesde = LocalDate.of(anio, 1, 1);
        LocalDate fechaHasta = LocalDate.of(anio, 12, 31);

        return CicloLectivo.builder()
                .nombre(nombreCiclo) // Se asigna aquí, aunque @PrePersist también lo hace
                .fechaDesde(fechaDesde)
                .fechaHasta(fechaHasta)
                .build();
    }

    // Impl metodos de la interfaz

    @Override
    public  CicloLectivoResponse crearSiguienteCicloLectivo() {
        int anioSiguiente;

        // 1. Buscar el último ciclo para determinar el año de inicio
        Optional<CicloLectivo> ultimoCiclo = cicloLectivoRepository.findTopByOrderByFechaHastaDesc();

        if (ultimoCiclo.isPresent()) {
            // El año siguiente es el año del último ciclo + 1
            anioSiguiente = ultimoCiclo.get().getFechaDesde().getYear() + 1;
        } else {
            // Si no hay ninguno, empezar con el año actual
            anioSiguiente = Year.now().getValue();
        }

        // 2. Construir y guardar
        CicloLectivo nuevoCiclo = construirCicloLectivo(anioSiguiente);
        CicloLectivo savedCiclo = cicloLectivoRepository.save(nuevoCiclo);

        CicloLectivoDto dto = CicloLectivoMapper.toDto(savedCiclo); // Mapeo para el Response

        logger.info("Ciclo Lectivo creado automáticamente: {}", savedCiclo.getNombre());

        return CicloLectivoResponse.builder()
                .CicloLectivo(dto)
                .code(0)
                .mensaje("Ciclo lectivo creado exitosamente.")
                .build();
    }

    @Override
    public  CicloLectivoResponse crearCicloLectivoPorAnio(int anio) {

        // 1. Construir y guardar para el año manual
        CicloLectivo nuevoCiclo = construirCicloLectivo(anio);
        CicloLectivo savedCiclo = cicloLectivoRepository.save(nuevoCiclo);

        CicloLectivoDto dto = CicloLectivoMapper.toDto(savedCiclo);

        logger.info("Ciclo Lectivo creado manualmente: {}", savedCiclo.getNombre());

        return CicloLectivoResponse.builder()
                .CicloLectivo(dto)
                .code(0)
                .mensaje("Ciclo lectivo creado manualmente para el año " + anio + ".")
                .build();
    }

    @Override
    public CicloLectivoResponse getCicloLectivoById(Long id) {

        CicloLectivo cicloLectivo = cicloLectivoRepository.findById(id).orElseThrow(() -> new CicloLectivoException("Ciclo Lectivo no encontrado con ID:: " + id));

        return CicloLectivoResponse.builder()
                .CicloLectivo(CicloLectivoMapper.toDto(cicloLectivo))
                .code(0)
                .mensaje("Ciclo Lectivo encontrado.")
                .build();

    }

    @Override
    public CicloLectivoResponseList ListCiclosLectivos() {

        List<CicloLectivoDto> ciclos = cicloLectivoRepository.findAll().stream()
        .map(CicloLectivoMapper::toDto)
        .toList();

        // Mapea la lista de entidades a una lista de DTOs
        return CicloLectivoResponseList.builder()
                .CicloLectivoDtos(ciclos)
                .code(0)
                .mensaje("Ciclos Lectivos listados correctamente.")
                .build();

    }


}
