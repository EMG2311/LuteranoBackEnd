package com.grup14.luterano.request.Preceptor;

import com.grup14.luterano.commond.GeneroEnum;
import com.grup14.luterano.entities.enums.TipoDoc;
import com.grup14.luterano.validation.MayorDeEdad;
import com.grup14.luterano.validation.MayorDeEdadGruoup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreceptorUpdateRequest {
    private Long id;
    private String nombre;
    private String apellido;
    private GeneroEnum genero;
    private TipoDoc tipoDoc;
    private String dni;
    private String email;
    private String direccion;
    private String telefono;
    @MayorDeEdad(groups = {MayorDeEdadGruoup.class})
    private Date fechaNacimiento;
    private Date fechaIngreso;
}
