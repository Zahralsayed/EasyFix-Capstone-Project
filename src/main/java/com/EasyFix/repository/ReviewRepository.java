package com.EasyFix.repository;

import com.EasyFix.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {

    List<Review> findByProviderIdOrderByCreatedAtDesc(Long providerId);
    boolean existsByAppointmentId(Long appointmentId);
}
