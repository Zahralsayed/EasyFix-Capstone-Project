package com.EasyFix.model;

import com.EasyFix.enums.AppointmentStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @OneToOne
    @JoinColumn(name="slot_id")
    private AvailabilitySlot slot;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private String problemDescription;
    private Double totalPrice;
}
