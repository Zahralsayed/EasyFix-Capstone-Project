package com.EasyFix.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PublicReviewDTO {
    private Long id;
    private String problemDescription;
    private String customerName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}