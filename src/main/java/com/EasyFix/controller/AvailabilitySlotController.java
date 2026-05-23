package com.EasyFix.controller;

import com.EasyFix.service.AvailabilitySlotService;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class AvailabilitySlotController {

    private final AvailabilitySlotService availabilityService;

    public AvailabilitySlotController(AvailabilitySlotService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/available")
    public ResponseEntity<List<LocalDateTime>> getAvailableSlots(
            @RequestParam Long providerId,
            @RequestParam String date) {

        LocalDate requestedDate = LocalDate.parse(date);
        List<LocalDateTime> freeTimes = availabilityService.getAvailableTimesForDate(providerId, requestedDate);
        return ResponseEntity.ok(freeTimes);
    }
}
