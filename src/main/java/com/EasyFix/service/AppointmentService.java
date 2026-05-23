package com.EasyFix.service;

import com.EasyFix.enums.AppointmentStatus;
import com.EasyFix.model.Appointment;
import com.EasyFix.model.User;
import com.EasyFix.repository.AppointmentRepository;
import com.EasyFix.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final AppointmentPDFService pdfService;
    private final EmailService emailService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              UserRepository userRepository,
                              AppointmentPDFService pdfService,
                              EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.pdfService = pdfService;
        this.emailService = emailService;
    }

    @Transactional
    public Appointment createBooking(String customerEmail, Long providerId, LocalDateTime startTime) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("Customer account not found."));

        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider account not found."));

        if (provider.getRole() != com.EasyFix.enums.Role.PROVIDER) {
            throw new RuntimeException("Booking failed: The selected user account is not a registered service provider.");
        }

        LocalDateTime endTime = startTime.plusHours(2);
        if (appointmentRepository.hasOverlappingAppointment(providerId, startTime, endTime)) {
            throw new RuntimeException("This time slot has just been reserved or is unavailable.");
        }

        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setProvider(provider);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        if (provider.getProviderDetails() != null) {
            appointment.setTotalPrice(provider.getProviderDetails().getHourlyRate() * 2);
        } else {
            appointment.setTotalPrice(0.0);
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);

        try {
            byte[] pdfReceipt = pdfService.generateAppointmentPdf(savedAppointment);
            String pdfName = "EasyFix-Appointment-" + savedAppointment.getId() + ".pdf";

            String customerBody = "<h3>Hi " + customer.getUsername() + ",</h3>" +
                    "<p>Your service request has been confirmed!</p>";
            emailService.sendBookingEmailWithAttachment(customer.getEmail(), "EasyFix Booking Confirmed! #" + savedAppointment.getId(), customerBody, pdfReceipt, pdfName);

            String providerName = provider.getProviderDetails() != null ? provider.getProviderDetails().getBusinessName() : provider.getUsername();
            String providerBody = "<h3>Hi " + providerName + ",</h3>" +
                    "<p>You have been assigned a new work ticket request.</p>";
            emailService.sendBookingEmailWithAttachment(provider.getEmail(), "New EasyFix Assignment Dispatched! #" + savedAppointment.getId(), providerBody, pdfReceipt, pdfName);

        } catch (Exception e) {
            System.err.println("Booking finalized in DB, but email notification delivery failed: " + e.getMessage());
        }

        return savedAppointment;
    }

    @Transactional
    public Appointment completeAppointment(Long appointmentId, String providerEmail) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found."));

        if (!appointment.getProvider().getEmail().equals(providerEmail)) {
            throw new RuntimeException("Access Denied: You are not the assigned professional for this job.");
        }

        if (appointment.getStatus() != com.EasyFix.enums.AppointmentStatus.SCHEDULED) {
            throw new RuntimeException("Only currently SCHEDULED appointments can be marked as completed.");
        }

        appointment.setStatus(com.EasyFix.enums.AppointmentStatus.COMPLETED);
        return appointmentRepository.save(appointment);
    }

}
