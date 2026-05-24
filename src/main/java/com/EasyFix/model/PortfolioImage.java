package com.EasyFix.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "provider_portfolio")
@Data
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
