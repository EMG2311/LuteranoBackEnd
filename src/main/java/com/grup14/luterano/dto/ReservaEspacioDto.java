package com.grup14.luterano.dto;

import com.grup14.luterano.dto.modulo.ModuloDto;
import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.EspacioAulico;
import com.grup14.luterano.entities.Modulo;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.EstadoReserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaEspacioDto {
    private Long id;
    private Long cursoId;
    private int cantidadAlumnos;
    private Long espacioAulicoId;
    private ModuloDto modulo;

    private LocalDate fecha;
    private Long usuarioId;
    private String motivoSolicitud;
    private EstadoReserva estado;
    private String motivoDenegacion;

}
