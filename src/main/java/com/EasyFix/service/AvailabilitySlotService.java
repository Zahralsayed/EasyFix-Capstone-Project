package com.EasyFix.service;

import com.EasyFix.model.AvailabilitySlot;
import com.EasyFix.model.User;
import com.EasyFix.repository.AppointmentRepository;
import com.EasyFix.repository.AvailabilitySlotRepository;
import com.EasyFix.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AvailabilitySlotService {
    private final AvailabilitySlotRepository slotRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public AvailabilitySlotService(AvailabilitySlotRepository slotRepository, AppointmentRepository appointmentRepository, UserRepository userRepository) {
        this.slotRepository = slotRepository;
        this.appointmentRepository= appointmentRepository;
        this.userRepository = userRepository;
    }

    public List<LocalDateTime> getAvailableTimesForDate(Long providerId, LocalDate date) {
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider account not found."));

        if (provider.getRole() != com.EasyFix.enums.Role.PROVIDER) {
            throw new RuntimeException("Access Denied: The requested account ID is not a registered service provider.");
        }

        List<LocalDateTime> availableSlots = new ArrayList<>();

        LocalDateTime startOfBusiness = date.atTime(LocalTime.of(8, 0));
        LocalDateTime endOfBusiness = date.atTime(LocalTime.of(20, 0));

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(LocalTime.MAX);
        List<AvailabilitySlot> busySlots = slotRepository
                .findByProviderIdAndStartTimeBetween(providerId, dayStart, dayEnd);

        LocalDateTime currentSlotStart = startOfBusiness;

        while (currentSlotStart.plusHours(2).isBefore(endOfBusiness) || currentSlotStart.plusHours(2).isEqual(endOfBusiness)) {

            LocalDateTime currentSlotEnd = currentSlotStart.plusHours(2);
            boolean isBusy = false;

            for (AvailabilitySlot busyBlock : busySlots) {
                if (currentSlotStart.isBefore(busyBlock.getEndTime()) && currentSlotEnd.isAfter(busyBlock.getStartTime())) {
                    isBusy = true;
                    break;
                }
            }

            boolean isBooked = appointmentRepository.hasOverlappingAppointment(providerId, currentSlotStart, currentSlotEnd);

            if (!isBusy && !isBooked) {
                availableSlots.add(currentSlotStart);
            }

            currentSlotStart = currentSlotStart.plusHours(2);
        }

        return availableSlots;
    }
}
