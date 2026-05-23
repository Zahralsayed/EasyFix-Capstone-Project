package com.EasyFix.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponseDTO {
    private Long reviewId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    private AppointmentDetails appointment;

    @Data
    public static class AppointmentDetails {
        private Long id;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status;
        private String problemDescription;
        private Double totalPrice;
    }}
