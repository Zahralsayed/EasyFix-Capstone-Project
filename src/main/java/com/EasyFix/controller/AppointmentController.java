package com.EasyFix.controller;

import com.EasyFix.model.Appointment;
import com.EasyFix.model.User;
import com.EasyFix.repository.AppointmentRepository;
import com.EasyFix.repository.UserRepository;
import com.EasyFix.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository  userRepository;

    public AppointmentController(AppointmentService appointmentService, AppointmentRepository appointmentRepository, UserRepository userRepository) {
        this.appointmentService = appointmentService;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/book")
    public ResponseEntity<?> bookJob(
            @RequestParam Long providerId,
            @RequestParam String startTimeIso,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            LocalDateTime startTime = LocalDateTime.parse(startTimeIso);
            Appointment booking = appointmentService.createBooking(userDetails.getUsername(), providerId, startTime);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<Appointment>> getMyBookings(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        if (user.getRole() == com.EasyFix.enums.Role.PROVIDER) {
            return ResponseEntity.ok(appointmentRepository.findByProviderIdOrderByStartTimeDesc(user.getId()));
        } else {
            return ResponseEntity.ok(appointmentRepository.findByCustomerIdOrderByStartTimeDesc(user.getId()));
        }
    }

    @PreAuthorize("hasAuthority('PROVIDER')")
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> markAsComplete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Appointment completedJob = appointmentService.completeAppointment(id, userDetails.getUsername());
            return ResponseEntity.ok(completedJob);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
