package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.TurnoDto;
import com.grup14.luterano.entities.MesaExamen;
import com.grup14.luterano.entities.TurnoExamen;
import com.grup14.luterano.exeptions.TurnoExamenException;
import com.grup14.luterano.mappers.TurnoMapper;
import com.grup14.luterano.repository.MesaExamenRepository;
import com.grup14.luterano.repository.TurnoExamenRepository;
import com.grup14.luterano.response.turnoExamen.TurnoListResponse;
import com.grup14.luterano.response.turnoExamen.TurnoResponse;
import com.grup14.luterano.service.TurnoExamenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TurnoExamenServiceImpl implements TurnoExamenService {

    private static final Logger log = LoggerFactory.getLogger(TurnoExamenServiceImpl.class);
    private final TurnoExamenRepository repo;
    private final MesaExamenRepository mesaRepo;

    @Override
    public TurnoListResponse listar(Integer anio) {
        List<TurnoExamen> list = (anio == null) ? repo.findAll() : repo.findByAnioOrderByFechaInicioAsc(anio);
        return TurnoListResponse.builder()
                .code(0).mensaje("OK")
                .turnos(list.stream().map(TurnoMapper::toDto).toList())
                .build();
    }

    @Override
    public TurnoResponse crear(TurnoDto dto) {
        if (dto.getAnio() == null || dto.getMes() == null)
            return TurnoResponse.builder().code(-1).mensaje("anio y mes son requeridos").build();

        YearMonth ym = YearMonth.of(dto.getAnio(), dto.getMes());
        LocalDate desde = ym.atDay(1);
        LocalDate hasta = ym.atEndOfMonth();

        if (repo.existsOverlappingInYear(dto.getAnio(), desde, hasta)) {
            throw new TurnoExamenException("Ya existe un turno que cubre ese mes del " + dto.getAnio());
        }
        String nombre = (dto.getNombre() != null && !dto.getNombre().isBlank())
                ? dto.getNombre()
                : nombreTurno(dto.getMes(), dto.getAnio());

        TurnoExamen t = TurnoExamen.builder()
                .nombre(nombre)
                .anio(dto.getAnio())
                .fechaInicio(desde)
                .fechaFin(hasta)
                .activo(Boolean.TRUE.equals(dto.getActivo()))
                .build();

        repo.save(t);
        log.info("Turno creado id={} nombre={} {}..{}", t.getId(), t.getNombre(), desde, hasta);
        return TurnoResponse.builder().code(0).mensaje("Turno creado").turno(TurnoMapper.toDto(t)).build();
    }

    @Override
    public TurnoResponse actualizar(Long id, TurnoDto dto) {
        TurnoExamen t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        if (dto.getNombre() != null) t.setNombre(dto.getNombre());
        if (dto.getActivo() != null) t.setActivo(dto.getActivo());

        if (dto.getAnio() != null || dto.getMes() != null) {
            int nuevoAnio = (dto.getAnio() != null) ? dto.getAnio() : t.getAnio();
            int mesActual = (t.getFechaInicio() != null) ? t.getFechaInicio().getMonthValue() : 1;
            int nuevoMes = (dto.getMes() != null) ? dto.getMes() : mesActual;

            YearMonth ym = YearMonth.of(nuevoAnio, nuevoMes);
            LocalDate desde = ym.atDay(1);
            LocalDate hasta = ym.atEndOfMonth();

            boolean overlap = repo.existsOverlappingInYearExcludingId(t.getId(), nuevoAnio, desde, hasta);
            if (overlap) {
                return TurnoResponse.builder()
                        .code(-1)
                        .mensaje("Ya existe otro turno que cubre ese mes del " + nuevoAnio)
                        .build();
            }

            List<MesaExamen> fuera = mesaRepo.findByTurnoOutsideRange(t.getId(), desde, hasta);
            if (!fuera.isEmpty()) {
                String detalles = fuera.stream()
                        .limit(5) // evita respuestas gigantes
                        .map(m -> "mesaId=" + m.getId() + " fecha=" + m.getFecha())
                        .collect(java.util.stream.Collectors.joining(", "));
                String extra = (fuera.size() > 5) ? " (+" + (fuera.size() - 5) + " más)" : "";
                return TurnoResponse.builder()
                        .code(-1)
                        .mensaje("No se puede cambiar el rango: hay " + fuera.size() +
                                " mesa(s) con fecha fuera del nuevo turno. " + detalles + extra)
                        .build();
            }
            t.setAnio(nuevoAnio);
            t.setFechaInicio(desde);
            t.setFechaFin(hasta);
        }

        repo.save(t);
        return TurnoResponse.builder()
                .code(0)
                .mensaje("Turno actualizado")
                .turno(TurnoMapper.toDto(t))
                .build();
    }

    @Override
    public TurnoResponse eliminar(Long id) {
        repo.deleteById(id);
        return TurnoResponse.builder().code(0).mensaje("Turno eliminado").build();
    }

    private static String nombreTurno(int mes, int anio) {
        String[] m = {"", "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};
        return m[mes] + " " + anio;
    }
}