package com.EasyFix.model;

import jakarta.persistence.*;

@Entity
@Table(name = "provider_portfolio")
public class PortfolioImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ProviderProfile provider;

    private String workImage;
    private String description;
}
