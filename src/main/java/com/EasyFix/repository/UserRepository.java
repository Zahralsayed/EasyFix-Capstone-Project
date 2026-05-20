package com.EasyFix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.EasyFix.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String token);
    Optional<User> findByResetToken(String resetToken);

}
