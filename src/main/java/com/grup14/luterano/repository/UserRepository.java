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
    @Query("""
     SELECT u
        FROM User u
        WHERE NOT EXISTS (
            SELECT 1 FROM Docente d WHERE d.user.id = u.id
        )
        AND NOT EXISTS (
            SELECT 1 FROM Preceptor p WHERE p.user.id = u.id
        )
""")
    Optional<List<User>> findUsuariosSinAsignar();

    @Query("""
    SELECT u
      FROM User u
      LEFT JOIN Docente d ON d.user.id = u.id
      LEFT JOIN Preceptor p ON p.user.id = u.id
      WHERE u.rol = :role
        AND d.id IS NULL
        AND p.id IS NULL
""")
    Optional<List<User>> findUsuariosSinAsignarPorRol(@Param("role") Role role);
}
