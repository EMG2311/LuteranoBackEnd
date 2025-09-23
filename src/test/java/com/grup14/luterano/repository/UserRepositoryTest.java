package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Role;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void guardarYBuscarUsuarioConRol() {
        // 🟦 Crear y guardar un rol
        Role rol = new Role();
        rol.setName("DOCENTE");
        roleRepository.save(rol);

        // 🟦 Crear un usuario con ese rol y userStatus
        User user = new User();
        user.setName("Ana");
        user.setLastName("Gomez");
        user.setEmail("ana@test.com");
        user.setPassword("secreta");
        user.setRol(rol);
        user.setUserStatus(UserStatus.CREADO); // 👈 obligatorio

        userRepository.save(user);

        // 🟦 Verificar búsqueda por email
        assertThat(userRepository.findByEmail("ana@test.com"))
                .isPresent()
                .get()
                .extracting(User::getName)
                .isEqualTo("Ana");
    }
}
