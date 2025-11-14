package com.grup14.luterano.entities;

import com.grup14.luterano.entities.enums.EstadoMesaExamen;
import com.grup14.luterano.entities.enums.TipoMesa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MesaExamen {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDate fecha;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TipoMesa tipoMesa = TipoMesa.EXAMEN;  // Por defecto es examen final
    
    @ManyToOne(optional = false)
    private TurnoExamen turno;
    
    @ManyToOne(optional = false)
    private MateriaCurso materiaCurso;

    @ManyToOne
    private Aula aula;

    @Enumerated(EnumType.STRING)
    private EstadoMesaExamen estado;

    @OneToMany(mappedBy = "mesaExamen", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 25)
    @Builder.Default
    @ToString.Exclude
    private List<MesaExamenAlumno> alumnos = new ArrayList<>();

    @OneToMany(mappedBy = "mesaExamen", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    @Builder.Default
    @ToString.Exclude
    private List<MesaExamenDocente> docentes = new ArrayList<>();

    @OneToOne(mappedBy = "mesa", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    private ActaExamen acta;
    
    /**
     * Valida si la mesa puede tener la cantidad actual de docentes
     */
    public boolean validarCantidadDocentes() {
        int cantidadDocentes = docentes != null ? docentes.size() : 0;
        return switch (tipoMesa) {
            case COLOQUIO -> cantidadDocentes <= 1;  // Máximo 1 docente
            case EXAMEN -> cantidadDocentes <= 3;    // Máximo 3 docentes
        };
    }
    
    /**
     * Valida si un alumno puede inscribirse en esta mesa según su condición
     */
    public boolean puedeInscribirAlumno(MesaExamenAlumno alumnoMesa) {
        if (alumnoMesa.getCondicionRinde() == null) return false;
        
        return switch (tipoMesa) {
            case COLOQUIO -> alumnoMesa.getCondicionRinde() == com.grup14.luterano.entities.enums.CondicionRinde.COLOQUIO;
            case EXAMEN -> true;  // Cualquier condición puede rendir examen final
        };
    }
}
