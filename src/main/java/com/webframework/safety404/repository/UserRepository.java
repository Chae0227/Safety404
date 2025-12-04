package com.webframework.safety404.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webframework.safety404.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);
}
