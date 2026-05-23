package com.EasyFix.repository;

import com.EasyFix.enums.UserStatus;
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

    @Query("SELECT u FROM User u WHERE u.category.id = :catId AND u.role = 'PROVIDER' AND u.status = 'ACTIVE'")
    List<User> findActiveProvidersByCategory(@Param("catId") Long categoryId);

    List<User> findByStatus(UserStatus userStatus);
}
