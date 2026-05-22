package com.EasyFix.model;

import jakarta.persistence.*;

@Entity
@Table(name = "provider_portfolio")
public class PortfolioImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private User provider;

    private String workImage;
    private String description;
}
