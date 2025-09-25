package com.grup14.luterano.repository;

import com.grup14.luterano.entities.Role;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.Rol;
import com.grup14.luterano.entities.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<List<User>> findByUserStatus(UserStatus userStatus);
    Optional<List<User>> findByRol(Role rol);
    Optional<User>findByName(String name);

    @Query("""
    SELECT u FROM User u
    WHERE u.id NOT IN (SELECT d.user.id FROM Docente d)
      AND u.id NOT IN (SELECT p.user.id FROM Preceptor p)
""")
    Optional<List<User>> findUsuariosSinAsignar();

    @Query("""
    SELECT u FROM User u
    WHERE u.rol = :role
      AND u.id NOT IN (SELECT d.user.id FROM Docente d)
      AND u.id NOT IN (SELECT p.user.id FROM Preceptor p)
""")
    Optional<List<User>> findUsuariosSinAsignarPorRol(@Param("role") Role role);
}
