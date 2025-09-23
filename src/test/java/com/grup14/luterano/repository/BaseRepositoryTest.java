package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Docente;
import com.grup14.luterano.entities.Role;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Clase base para tests de repositorios.
 * Provee métodos de utilidad para crear entidades válidas.
 */
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public abstract class BaseRepositoryTest {

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected DocenteRepository docenteRepository;

    // 🟦 Crea un rol persistido en la BD de test
    protected Role createRole(String name) {
        Role rol = new Role();
        rol.setName(name);
        return roleRepository.save(rol);
    }

    // 🟦 Crea un usuario persistido con rol y userStatus
    protected User createUser(String name, String lastName, String email, Role rol) {
        User user = new User();
        user.setName(name);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword("12345"); // mínimo 5 caracteres
        user.setRol(rol);
        user.setUserStatus(UserStatus.CREADO);
        return userRepository.save(user);
    }

    // 🟦 Crea un docente persistido con usuario asociado
    protected Docente createDocente(String dni, String email, User user) {
        Docente docente = new Docente();
        docente.setDni(dni);
        docente.setEmail(email); // findByEmail depende de esta columna
        docente.setUser(user);
        return docenteRepository.save(docente);
    }
}
