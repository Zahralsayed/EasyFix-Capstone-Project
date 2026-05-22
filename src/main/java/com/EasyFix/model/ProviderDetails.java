package com.EasyFix.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDetails {
    private String businessName;
    private String bio;

    private Double hourlyRate;
    private Integer yearsOfExperience;
}
