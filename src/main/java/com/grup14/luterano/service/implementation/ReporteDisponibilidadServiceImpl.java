package com.grup14.luterano.service.implementation;

import com.grup14.luterano.dto.reporteDisponibilidad.BloqueOcupadoDto;
import com.grup14.luterano.dto.reporteDisponibilidad.DiaAgendaDto;
import com.grup14.luterano.dto.reporteDisponibilidad.DocenteDisponibilidadDto;
import com.grup14.luterano.entities.HorarioClaseModulo;
import com.grup14.luterano.entities.MateriaCurso;
import com.grup14.luterano.entities.enums.DiaSemana;
import com.grup14.luterano.repository.DocenteRepository;
import com.grup14.luterano.repository.HorarioClaseModuloRepository;
import com.grup14.luterano.repository.MateriaCursoRepository;
import com.grup14.luterano.repository.ModuloRepository;
import com.grup14.luterano.response.reporteDisponibilidad.DocenteDisponibilidadResponse;
import com.grup14.luterano.service.ReporteDisponibilidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteDisponibilidadServiceImpl implements ReporteDisponibilidadService {

    private final DocenteRepository docenteRepo;
    private final MateriaCursoRepository materiaCursoRepo;
    private final HorarioClaseModuloRepository horarioRepo;
    private final ModuloRepository moduloRepo;

    @Override
    @Transactional(readOnly = true)
    public DocenteDisponibilidadResponse disponibilidadDocente(Long docenteId) {
        var docente = docenteRepo.findById(docenteId)
                .orElseThrow(() -> new IllegalArgumentException("Docente no encontrado (id=" + docenteId + ")"));

        // Materias dictadas
        List<MateriaCurso> mcs = materiaCursoRepo.findByDocente_Id(docenteId);
        List<String> materias = mcs.stream()
                .map(mc -> mc.getMateria() != null ? mc.getMateria().getNombre() : null)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        // Obtener TODOS los módulos de la institución
        List<com.grup14.luterano.entities.Modulo> todosLosModulos = moduloRepo.findAllByOrderByOrdenAsc();
        
        // Crear lista de módulos disponibles para el frontend
        List<com.grup14.luterano.dto.modulo.ModuloDto> modulosDisponibles = todosLosModulos.stream()
                .map(m -> com.grup14.luterano.dto.modulo.ModuloDto.builder()
                        .id(m.getId())
                        .orden(m.getOrden())
                        .desde(m.getHoraDesde() != null ? m.getHoraDesde().toString() : null)
                        .hasta(m.getHoraHasta() != null ? m.getHoraHasta().toString() : null)
                        .build())
                .toList();

        // Horarios ocupados por el docente (agrupados por día y módulo)
        List<HorarioClaseModulo> bloquesOcupados = horarioRepo.findByMateriaCurso_Docente_IdOrderByDiaSemanaAscModulo_OrdenAsc(docenteId);
        
        // Crear un mapa para acceso rápido: Día -> ModuloId -> HorarioClaseModulo
        Map<DiaSemana, Map<Long, HorarioClaseModulo>> ocupadosPorDiaYModulo = new EnumMap<>(DiaSemana.class);
        for (HorarioClaseModulo h : bloquesOcupados) {
            ocupadosPorDiaYModulo
                    .computeIfAbsent(h.getDiaSemana(), k -> new HashMap<>())
                    .put(h.getModulo().getId(), h);
        }

        // Crear la agenda completa para cada día
        List<DiaAgendaDto> agenda = new ArrayList<>();
        for (DiaSemana dia : DiaSemana.values()) {
            List<BloqueOcupadoDto> bloquesDelDia = new ArrayList<>();
            double horasOcupadasDia = 0.0;

            Map<Long, HorarioClaseModulo> ocupadosDelDia = ocupadosPorDiaYModulo.getOrDefault(dia, new HashMap<>());

            // Para cada módulo de la institución
            for (com.grup14.luterano.entities.Modulo modulo : todosLosModulos) {
                HorarioClaseModulo horarioOcupado = ocupadosDelDia.get(modulo.getId());
                
                double horas = 0.0;
                if (modulo.getHoraDesde() != null && modulo.getHoraHasta() != null) {
                    var dur = Duration.between(modulo.getHoraDesde(), modulo.getHoraHasta());
                    horas = dur.toMinutes() / 60.0;
                }

                if (horarioOcupado != null) {
                    // Módulo ocupado por el docente
                    var mc = horarioOcupado.getMateriaCurso();
                    var curso = mc.getCurso();
                    var materia = mc.getMateria();

                    BloqueOcupadoDto bloque = BloqueOcupadoDto.builder()
                            .moduloId(modulo.getId())
                            .orden(modulo.getOrden())
                            .horaDesde(modulo.getHoraDesde())
                            .horaHasta(modulo.getHoraHasta())
                            .cursoId(curso != null ? curso.getId() : null)
                            .cursoAnio(curso != null ? curso.getAnio() : null)
                            .cursoDivision(curso != null && curso.getDivision() != null ? curso.getDivision().name() : null)
                            .materiaId(materia != null ? materia.getId() : null)
                            .materiaNombre(materia != null ? materia.getNombre() : null)
                            .horas(horas)
                            .estaLibre(false)
                            .build();
                    
                    bloquesDelDia.add(bloque);
                    horasOcupadasDia += horas;
                } else {
                    // Módulo libre
                    BloqueOcupadoDto bloque = BloqueOcupadoDto.builder()
                            .moduloId(modulo.getId())
                            .orden(modulo.getOrden())
                            .horaDesde(modulo.getHoraDesde())
                            .horaHasta(modulo.getHoraHasta())
                            .cursoId(null)
                            .cursoAnio(null)
                            .cursoDivision(null)
                            .materiaId(null)
                            .materiaNombre("Libre")
                            .horas(horas)
                            .estaLibre(true)
                            .build();
                    
                    bloquesDelDia.add(bloque);
                }
            }

            // Ordenar bloques por orden de módulo
            bloquesDelDia.sort(Comparator.comparing(BloqueOcupadoDto::getOrden, Comparator.nullsLast(Integer::compareTo)));

            DiaAgendaDto diaDto = DiaAgendaDto.builder()
                    .dia(dia)
                    .bloques(bloquesDelDia)
                    .horasOcupadasDia(redondear1(horasOcupadasDia))
                    .build();
            
            agenda.add(diaDto);
        }

        double horasTotal = agenda.stream().mapToDouble(DiaAgendaDto::getHorasOcupadasDia).sum();

        var dto = DocenteDisponibilidadDto.builder()
                .docenteId(docente.getId())
                .dni(docente.getDni())
                .apellido(docente.getApellido())
                .nombre(docente.getNombre())
                .materias(materias)
                .modulosDisponibles(modulosDisponibles)
                .agenda(agenda)
                .horasOcupadasTotal(redondear1(horasTotal))
                .build();

        return com.grup14.luterano.response.reporteDisponibilidad.DocenteDisponibilidadResponse.builder()
                .data(dto)
                .code(0)
                .mensaje("OK")
                .build();
    }

    private double redondear1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
