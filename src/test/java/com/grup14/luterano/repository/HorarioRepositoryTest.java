package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.Horario;
import com.grup14.luterano.entities.Materia;
import com.grup14.luterano.entities.Role;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.Nivel;
import com.grup14.luterano.entities.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class HorarioRepositoryTest {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void guardarYBuscarHorario() {
        // Rol y usuario para que Docente.getNombre() no sea null
        Role rol = new Role();
        rol.setName("DOCENTE");
        roleRepository.save(rol);

        User user = new User();
        user.setName("Laura");
        user.setLastName("Martínez");
        user.setEmail("laura@test.com");
        user.setPassword("clave123"); // >= 5 chars
        user.setRol(rol);
        user.setUserStatus(UserStatus.CREADO);
        userRepository.save(user);

        // Docente asociado a ese user
        Docente docente = new Docente();
        docente.setDni("99887766");
        docente.setEmail("laura@test.com");
        docente.setUser(user);
        docenteRepository.save(docente);

        // Materia con todos los NOT NULL completos
        Materia materia = new Materia();
        materia.setNombre("Matemática");
        materia.setDescripcion("Materia básica de cálculo");
        materia.setNivel(Nivel.BASICO); // 👈 enum, no String
        materiaRepository.save(materia);

        // Horario
        Horario horario = new Horario();
        horario.setDocente(docente);
        horario.setMateria(materia);
        horario.setHoraDesde(LocalTime.of(8, 0));
        horario.setHoraHasta(LocalTime.of(10, 0));
        horarioRepository.save(horario);

        // Verificación
        assertThat(horarioRepository.findAll()).hasSize(1);
        Horario guardado = horarioRepository.findAll().get(0);
        assertThat(guardado.getMateria().getNombre()).isEqualTo("Matemática");
        assertThat(guardado.getDocente().getNombre()).isEqualTo("Laura"); // viene de user
    }
}
