package com.EasyFix.repository;

import com.EasyFix.model.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot,Long> {
    List<AvailabilitySlot> findByProviderIdAndStartTimeBetween(Long providerId, LocalDateTime start, LocalDateTime end);
}
