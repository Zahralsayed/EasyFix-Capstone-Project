package com.EasyFix.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "provider_profiles")
public class ProviderProfile {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @OneToOne
        @JoinColumn(name = "user_id")
        private User user;

        @ManyToOne
        @JoinColumn(name = "category_id")
        private ServiceCategory category;

        private String businessName;
        private String bio;
        private Double hourlyRate;
        private Integer yearsExperience;

        @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
        private List<PortfolioImage> portfolio;
}
