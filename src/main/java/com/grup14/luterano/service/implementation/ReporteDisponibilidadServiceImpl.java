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

        // Horario ocupado (por día)
        List<HorarioClaseModulo> bloques = horarioRepo.findByMateriaCurso_Docente_IdOrderByDiaSemanaAscModulo_OrdenAsc(docenteId);

        Map<DiaSemana, List<BloqueOcupadoDto>> porDia = new EnumMap<>(DiaSemana.class);
        for (HorarioClaseModulo h : bloques) {
            var mod = h.getModulo();
            var mc = h.getMateriaCurso();
            var curso = mc.getCurso();
            var materia = mc.getMateria();
            double horas = 0.0;
            if (mod.getHoraDesde() != null && mod.getHoraHasta() != null) {
                var dur = Duration.between(mod.getHoraDesde(), mod.getHoraHasta());
                horas = dur.toMinutes() / 60.0;
            }
            BloqueOcupadoDto b = BloqueOcupadoDto.builder()
                    .moduloId(mod.getId())
                    .orden(mod.getOrden())
                    .horaDesde(mod.getHoraDesde())
                    .horaHasta(mod.getHoraHasta())
                    .cursoId(curso != null ? curso.getId() : null)
                    .cursoAnio(curso != null ? curso.getAnio() : null)
                    .cursoDivision(curso != null && curso.getDivision() != null ? curso.getDivision().name() : null)
                    .materiaId(materia != null ? materia.getId() : null)
                    .materiaNombre(materia != null ? materia.getNombre() : null)
                    .horas(horas)
                    .build();

            porDia.computeIfAbsent(h.getDiaSemana(), k -> new ArrayList<>()).add(b);
        }

        List<DiaAgendaDto> agenda = porDia.entrySet().stream()
                .map(e -> {
                    var bloquesDia = e.getValue();
                    // ordenar por orden de módulo y horaDesde
                    bloquesDia.sort(Comparator
                            .comparing(BloqueOcupadoDto::getOrden, Comparator.nullsLast(Integer::compareTo))
                            .thenComparing(BloqueOcupadoDto::getHoraDesde, Comparator.nullsLast(Comparator.naturalOrder())));
                    double horasDia = bloquesDia.stream().mapToDouble(BloqueOcupadoDto::getHoras).sum();
                    return DiaAgendaDto.builder()
                            .dia(e.getKey())
                            .bloques(bloquesDia)
                            .horasOcupadasDia(redondear1(horasDia))
                            .build();
                })
                .sorted(Comparator.comparing(d -> d.getDia().ordinal()))
                .collect(Collectors.toList());

        double horasTotal = agenda.stream().mapToDouble(DiaAgendaDto::getHorasOcupadasDia).sum();

        var dto = DocenteDisponibilidadDto.builder()
                .docenteId(docente.getId())
                .dni(docente.getDni())
                .apellido(docente.getApellido())
                .nombre(docente.getNombre())
                .materias(materias)
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
