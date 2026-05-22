package com.EasyFix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.EasyFix.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String token);
    Optional<User> findByResetToken(String resetToken);

    @Query(value = "SELECT u.* FROM users u INNER JOIN provider_customers pc ON u.id = pc.customer_id WHERE pc.provider_id = :providerId", nativeQuery = true)
    List<User> findCustomersByProviderIdNative(@Param("providerId") Long providerId);
}
