package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Preceptor;
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
class PreceptorRepositoryTest {

    @Autowired
    private PreceptorRepository preceptorRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void guardarYBuscarPreceptor() {
        Role rol = new Role();
        rol.setName("PRECEPTOR");
        roleRepository.save(rol);

        User user = new User();
        user.setName("Esteban");
        user.setLastName("Ruiz");
        user.setEmail("esteban@test.com");
        user.setPassword("clave123");
        user.setRol(rol);
        user.setUserStatus(UserStatus.CREADO);
        userRepository.save(user);

        Preceptor preceptor = new Preceptor();
        preceptor.setDni("45678912");
        preceptor.setEmail("esteban@test.com");
        preceptor.setUser(user);
        preceptorRepository.save(preceptor);

        assertThat(preceptorRepository.findByEmail("esteban@test.com"))
                .isPresent()
                .get()
                .extracting(Preceptor::getNombre)
                .isEqualTo("Esteban");
    }
}
