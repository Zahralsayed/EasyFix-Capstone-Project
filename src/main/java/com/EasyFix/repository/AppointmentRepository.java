package com.EasyFix.repository;

import com.EasyFix.model.Appointment;
import com.EasyFix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT DISTINCT a.customer FROM Appointment a WHERE a.slot.provider.id = :providerId")
    List<User> findDistinctCustomersByProviderId(@Param("providerId") Long providerId);
}
