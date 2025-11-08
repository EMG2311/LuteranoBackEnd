package com.grup14.luterano.dto.reporteHistorialAlumno;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReporteHistorialAlumnoDto {
    // Información básica del alumno
    private Long alumnoId;
    private String dni;
    private String apellido;
    private String nombre;
    private String genero;
    private String estadoActual;
    
    // Historial académico por años
    private List<HistorialCicloDto> historialPorCiclos;
    
    // Resumen estadístico
    private ResumenHistorialDto resumen;
    
    @Data
    @Builder
    public static class HistorialCicloDto {
        private Integer cicloAnio;
        private String cicloNombre;
        private Integer cursoAnio;
        private String cursoDivision;
        private String cursoNivel;
        private String estadoCiclo; // ACTIVO, CERRADO, PROMOCIONADO, REPITENTE
        private List<MateriaNotasDto> materias;
        private Double promedioGeneral;
        private Integer materiasAprobadas;
        private Integer materiasDesaprobadas;
        private Integer materiasTotal;
    }
    
    @Data
    @Builder
    public static class MateriaNotasDto {
        private Long materiaId;
        private String materiaNombre;
        private List<CalificacionDto> calificaciones;
        private Double promedioEtapa1;
        private Double promedioEtapa2;
        private Double promedioGeneral;
        private Integer notaFinal;
        private String estadoMateria; // APROBADA, DESAPROBADA, EN_CURSO
    }
    
    @Data
    @Builder
    public static class CalificacionDto {
        private Integer etapa;
        private Integer numeroNota;
        private Integer nota;
        private String fecha;
    }
    
    @Data
    @Builder
    public static class ResumenHistorialDto {
        private Integer totalCiclosLectivos;
        private Integer totalMateriasAprobadas;
        private Integer totalMateriasDesaprobadas;
        private Double promedioGeneralHistorico;
        private Integer cantidadRepeticiones;
        private String tendenciaAcademica; // MEJORANDO, ESTABLE, EMPEORANDO
        private List<String> logrosDestacados;
        private List<String> areasAMejorar;
    }
}