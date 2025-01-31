package com.sopra.eaplanner.auth.models.userlogin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {
    Optional<UserLogin> findByUsername(String username);

    Boolean existsByUsername(String username);
}
