package com.grup14.luterano;

import com.grup14.luterano.commond.GeneroEnum;
import com.grup14.luterano.dto.CursoDto;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import com.grup14.luterano.entities.enums.TipoDoc;
import com.grup14.luterano.request.alumno.AlumnoRequest;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.service.AlumnoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@Profile("seed") // activar con: -Dspring.profiles.active=seed
@RequiredArgsConstructor
public class AlumnoNombresComunesSeeder implements CommandLineRunner {

    private final AlumnoService alumnoService;

    private static final int ALUMNOS_POR_CURSO = 30;
    private static final ZoneId Z = ZoneId.systemDefault();
    private static final ThreadLocalRandom R = ThreadLocalRandom.current();
    private static final AtomicInteger CORR = new AtomicInteger(1);

    // ======== Pools de nombres/apellidos comunes (AR/ES/LatAm) ========
    private static final String[] MASC = {
            "Juan","Pedro","Nicolás","Matías","Lucas","Santiago","Diego","Martín","Facundo","Gabriel",
            "Bruno","Agustín","Franco","Emiliano","Tomás","Felipe","Gonzalo","Julián","Benjamín","Joaquín"
    };
    private static final String[] FEM = {
            "María","Sofía","Camila","Valentina","Julieta","Martina","Agustina","Lucía","Carolina","Daniela",
            "Florencia","Antonella","Milagros","Josefina","Victoria","Paula","Elena","Candela","Abril","Bianca"
    };
    private static final String[] APELLIDOS = {
            "García","González","Rodríguez","Fernández","López","Martínez","Pérez","Gómez","Díaz","Sánchez",
            "Romero","Álvarez","Torres","Ruiz","Suárez","Ramírez","Flores","Acosta","Benítez","Molina"
    };
    private static final String[] CALLES = {
            "San Martín","Belgrano","Rivadavia","Sarmiento","Mitre","Lavalle","Moreno","Catamarca","Córdoba","Santa Fe",
            "Italia","España","Urquiza","Entre Ríos","Independencia","Alsina","Salta","Tucumán","Mendoza","Corrientes"
    };

    @Override
    public void run(String... args) {
        log.info("==> Sembrando alumnos con nombres/apellidos comunes (30 por curso, 1A..6B IDs 1..12)");

        // Año/División -> ID curso (según tu tabla/captura)
        Map<String, Long> cursoId = new LinkedHashMap<>();
        cursoId.put("1A", 1L);  cursoId.put("1B", 2L);
        cursoId.put("2A", 3L);  cursoId.put("2B", 4L);
        cursoId.put("3A", 5L);  cursoId.put("3B", 6L);
        cursoId.put("4A", 7L);  cursoId.put("4B", 8L);
        cursoId.put("5A", 9L);  cursoId.put("5B", 10L);
        cursoId.put("6A", 11L); cursoId.put("6B", 12L);

        cursoId.forEach((anioDiv, idCurso) -> {
            CursoDto cursoDto = new CursoDto();
            cursoDto.setId(idCurso);

            for (int i = 1; i <= ALUMNOS_POR_CURSO; i++) {
                int seq = CORR.getAndIncrement();

                // Alterna género y elige nombres comunes
                GeneroEnum genero = (i % 2 == 0) ? GeneroEnum.MASCULINO : GeneroEnum.FEMENINO;
                String nombre = (genero == GeneroEnum.MASCULINO) ? pick(MASC) : pick(FEM);

                // Dos apellidos comunes
                String apellido = pick(APELLIDOS) + " " + pick(APELLIDOS);

                // Identificadores únicos
                String dni = String.valueOf(40_000_000 + seq);
                String email = emailFrom(nombre, apellido, seq);

                // Dirección y teléfono
                String direccion = String.format("%s %d", pick(CALLES), 100 + (seq % 800));
                String telefono = String.format("11-%04d-%04d", 1000 + (seq % 8000), 1000 + ((seq * 7) % 8000));

                // Fechas (≥18 años para pasar la validación)
                LocalDate nacimiento = LocalDate.now()
                        .minusYears(18 + (seq % 5)) // 18..22
                        .withMonth(3).withDayOfMonth(10);
                LocalDate ingreso = LocalDate.now()
                        .minusYears(1 + (seq % 2)) // hace 1 o 2 años
                        .withMonth(3).withDayOfMonth(1);

                AlumnoRequest req = AlumnoRequest.builder()
                        .nombre(nombre)
                        .apellido(apellido)
                        .genero(genero)
                        .tipoDoc(TipoDoc.DNI)
                        .dni(dni)
                        .email(email)
                        .direccion(direccion)
                        .telefono(telefono)
                        .fechaNacimiento(toDate(nacimiento))
                        .fechaIngreso(toDate(ingreso))
                        .cursoActual(cursoDto)
                        .estado(EstadoAlumno.REGULAR)
                        .build();

                try {
                    AlumnoResponse res = alumnoService.crearAlumno(req);
                    if (res != null && res.getCode() != null && res.getCode() < 0) {
                        log.warn("No se creó {} {} (DNI {}): code={}, msg={}",
                                req.getNombre(), req.getApellido(), req.getDni(), res.getCode(), res.getMensaje());
                    } else {
                        log.debug("Creado: {} {} | DNI {} | Curso id={}", req.getNombre(), req.getApellido(), req.getDni(), idCurso);
                    }
                } catch (Exception e) {
                    log.error("Error creando alumno (DNI {}): {}", req.getDni(), e.getMessage(), e);
                }
            }
        });

        log.info("==> Siembra finalizada.");
    }

    // ======== Utils ========
    private static String pick(String[] arr) {
        return arr[R.nextInt(arr.length)];
    }

    private static Date toDate(LocalDate ld) {
        return Date.from(ld.atStartOfDay(Z).toInstant());
    }

    private static String emailFrom(String nombre, String apellido, int seq) {
        String base = (normalize(nombre) + "." + normalize(apellido).replace(" ", "."))
                .toLowerCase(Locale.ROOT);
        // Limpiar caracteres no válidos para email
        base = base.replaceAll("[^a-z0-9._-]", "");
        return base + "." + seq + "@luterano.edu";
    }

    private static String normalize(String s) {
        String n = Normalizer.normalize(s, Normalizer.Form.NFD);
        return n.replaceAll("\\p{M}+", ""); // quita acentos
    }
}