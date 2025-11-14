package com.grup14.luterano.dto.reporteDisponibilidad;

import com.grup14.luterano.dto.modulo.ModuloDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocenteDisponibilidadDto {
    private Long docenteId;
    private String dni;
    private String apellido;
    private String nombre;

    private List<String> materias; // nombres de materias que dicta
    private List<ModuloDto> modulosDisponibles; // todos los módulos de la institución

    private List<DiaAgendaDto> agenda; // horarios completos (ocupados y libres) por día
    private double horasOcupadasTotal;
}
