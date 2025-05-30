package com.grup14.luterano.repository;

import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<List<User>> findByUserStatus(UserStatus userStatus);
}
