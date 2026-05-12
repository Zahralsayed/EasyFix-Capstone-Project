package com.EasyFix.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "availability_slots")
public class AvailabilitySlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ProviderProfile provider;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isBooked = false;
}
