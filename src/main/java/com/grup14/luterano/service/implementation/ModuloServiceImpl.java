package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.modulo.ModuloDto;
import com.grup14.luterano.dto.modulo.ModuloEstadoDto;
import com.grup14.luterano.dto.modulo.ModuloReservaEstadoDto;
import com.grup14.luterano.entities.HorarioClaseModulo;
import com.grup14.luterano.entities.Modulo;
import com.grup14.luterano.entities.ReservaEspacio;
import com.grup14.luterano.entities.enums.DiaSemana;
import com.grup14.luterano.exeptions.ModuloException;
import com.grup14.luterano.mappers.HorarioClaseModuloMapper;
import com.grup14.luterano.mappers.ModuloMapper;
import com.grup14.luterano.repository.*;
import com.grup14.luterano.response.modulo.*;
import com.grup14.luterano.service.ModuloService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ModuloServiceImpl implements ModuloService {
    private final ModuloRepository moduloRepository;
    private final HorarioClaseModuloRepository horarioRepository;
    private final CursoRepository cursoRepository;
    private final ReservaEspacioRepository reservaEspacioRepository;
    private final EspacioAulicoRepository espacioAulicoRepository;

    @Override
    @Transactional(readOnly = true)
    public ModuloListResponse modulosLibresDelCursoPorDia(Long cursoId, DiaSemana dia) {
        cursoRepository.findById(cursoId).orElseThrow(() ->
                new ModuloException("No existe el curso con id " + cursoId));
        List<Modulo> libres = moduloRepository.findModulosLibresPorCursoYDia(cursoId, dia);
        return ModuloListResponse.builder()
                .modulos(libres.stream().map(ModuloMapper::toDto).toList())
                .code(0)
                .mensaje("Se lista correctametne los modulos libres")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ModuloSemanaResponse modulosLibresDelCursoTodaLaSemana(Long cursoId) {
        Map<DiaSemana, List<ModuloDto>> mapa = new java.util.LinkedHashMap<>();
        cursoRepository.findById(cursoId).orElseThrow(() ->
                new ModuloException("No existe el curso con id " + cursoId));
        for (DiaSemana d : DiaSemana.values()) {
            var libres = moduloRepository.findModulosLibresPorCursoYDia(cursoId, d);
            var dtos = libres.stream().map(ModuloMapper::toDto).toList();
            mapa.put(d, dtos);
        }

        return ModuloSemanaResponse.builder()
                .code(0)
                .mensaje("Se listan correctamente los módulos libres de toda la semana")
                .modulosPorDia(mapa)
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public ModuloListResponse todosLosModulos() {
        var todos = moduloRepository.findAllByOrderByOrdenAsc();
        return ModuloListResponse.builder()
                .code(0)
                .mensaje("Se listan correctamente todos los módulos")
                .modulos(todos.stream().map(ModuloMapper::toDto).toList())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ModuloEstadoListResponse modulosDelCursoPorDiaConEstado(Long cursoId, DiaSemana dia) {
        if (cursoId == null) throw new ModuloException("cursoId es obligatorio.");
        if (dia == null) throw new ModuloException("El parámetro 'dia' es obligatorio.");

        if (!cursoRepository.existsById(cursoId)) {
            throw new ModuloException("Curso no encontrado (id=" + cursoId + ").");
        }

        // Grilla completa
        List<Modulo> modulos = moduloRepository.findAllByOrderByOrdenAsc();

        // Ocupados por el curso ese día
        List<HorarioClaseModulo> ocupados = horarioRepository.findByMateriaCurso_Curso_IdAndDiaSemana(cursoId, dia);
        Map<Long, HorarioClaseModulo> porModuloId = ocupados.stream()
                .collect(Collectors.toMap(h -> h.getModulo().getId(), Function.identity(), (a, b) -> a));

        var lista = modulos.stream().map(m -> {
            var h = porModuloId.get(m.getId());
            return ModuloEstadoDto.builder()
                    .modulo(ModuloMapper.toDto(m))
                    .ocupado(h != null)
                    .horario(h != null ? HorarioClaseModuloMapper.toDto(h) : null)
                    .build();
        }).toList();

        return ModuloEstadoListResponse.builder()
                .code(0)
                .mensaje("Se listan correctamente los módulos con su estado para el día " + dia)
                .modulos(lista)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ModuloEstadoSemanaResponse modulosDelCursoSemanaConEstado(Long cursoId) {
        if (cursoId == null) throw new ModuloException("cursoId es obligatorio.");
        if (!cursoRepository.existsById(cursoId)) {
            throw new ModuloException("Curso no encontrado (id=" + cursoId + ").");
        }

        Map<DiaSemana, List<ModuloEstadoDto>> mapa = new LinkedHashMap<>();
        for (DiaSemana d : DiaSemana.values()) {
            ModuloEstadoListResponse dayResp = modulosDelCursoPorDiaConEstado(cursoId, d);
            mapa.put(d, dayResp.getModulos());
        }

        return ModuloEstadoSemanaResponse.builder()
                .code(0)
                .mensaje("Se listan correctamente los módulos con su estado para toda la semana")
                .modulosPorDia(mapa)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ModuloReservaEstadoResponse obtenerModulosConReservas(Long espacioAulicoId, LocalDate fecha) {
        if (espacioAulicoId == null) {
            throw new ModuloException("espacioAulicoId es obligatorio");
        }
        if (fecha == null) {
            throw new ModuloException("fecha es obligatoria");
        }

        // Validar que el espacio áulico existe
        if (!espacioAulicoRepository.existsById(espacioAulicoId)) {
            throw new ModuloException("Espacio áulico no encontrado (id=" + espacioAulicoId + ")");
        }

        // Obtener todos los módulos ordenados
        List<Modulo> todosLosModulos = moduloRepository.findAllByOrderByOrdenAsc();

        // Obtener reservas activas para el espacio y fecha
        List<ReservaEspacio> reservasActivas = reservaEspacioRepository
                .findActiveReservasByEspacioAndFecha(espacioAulicoId, fecha);

        // Crear mapa de módulos ocupados para búsqueda rápida
        Map<Long, ReservaEspacio> modulosOcupados = reservasActivas.stream()
                .collect(Collectors.toMap(
                        r -> r.getModulo().getId(),
                        Function.identity(),
                        (existing, replacement) -> existing // En caso de conflicto, mantener el primero
                ));

        // Mapear a DTOs con estado de ocupación
        List<ModuloReservaEstadoDto> modulosConEstado = todosLosModulos.stream()
                .map(modulo -> {
                    ReservaEspacio reserva = modulosOcupados.get(modulo.getId());
                    boolean ocupado = reserva != null;
                    String motivoOcupacion = null;

                    if (ocupado) {
                        String solicitante = reserva.getUsuario().getName(); // User tiene 'name' no 'nombre'
                        String curso = reserva.getCurso().getAnio() + "° " + reserva.getCurso().getDivision().toString(); // Curso no tiene 'nombre'
                        motivoOcupacion = String.format("Reserva de %s para %s", solicitante, curso);
                    }

                    return ModuloReservaEstadoDto.builder()
                            .id(modulo.getId())
                            .nombre("Módulo " + modulo.getOrden()) // Módulo no tiene 'nombre'
                            .horaInicio(modulo.getHoraDesde()) // Es 'horaDesde' no 'horaInicio'
                            .horaFin(modulo.getHoraHasta()) // Es 'horaHasta' no 'horaFin'
                            .orden(modulo.getOrden())
                            .ocupado(ocupado)
                            .motivoOcupacion(motivoOcupacion)
                            .build();
                })
                .collect(Collectors.toList());

        return ModuloReservaEstadoResponse.builder()
                .modulos(modulosConEstado)
                .code(0)
                .mensaje("Se listan correctamente los módulos con su estado de reserva para el espacio y fecha especificados")
                .build();
    }
}
