package com.EasyFix.service;

import com.EasyFix.model.AvailabilitySlot;
import com.EasyFix.repository.AvailabilitySlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AvailabilitySlotService {
    private final AvailabilitySlotRepository slotRepository;

    public AvailabilitySlotService(AvailabilitySlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    public List<LocalDateTime> getAvailableTimesForDate(Long providerId, LocalDate date) {
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

            if (!isBusy) {
                availableSlots.add(currentSlotStart);
            }

            currentSlotStart = currentSlotStart.plusHours(2);
        }

        return availableSlots;
    }
}
