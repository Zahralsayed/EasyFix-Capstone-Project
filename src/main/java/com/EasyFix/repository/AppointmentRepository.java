package com.EasyFix.repository;

import com.EasyFix.model.Appointment;
import com.EasyFix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByCustomerIdOrderByStartTimeDesc(Long customerId);
    List<Appointment> findByProviderIdOrderByStartTimeDesc(Long providerId);

    @Query("SELECT DISTINCT a.customer FROM Appointment a WHERE a.provider.id = :providerId")
    List<User> findDistinctCustomersByProviderId(@Param("providerId") Long providerId);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.provider.id = :providerId " +
            "AND a.status = 'SCHEDULED' " +
            "AND (:start < a.endTime AND :end > a.startTime)")
    boolean hasOverlappingAppointment(@Param("providerId") Long providerId,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);
}
