package com.EasyFix.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "service_categories")
public class ServiceCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;
    private String iconUrl;

    @OneToMany(mappedBy = "category")
    private List<ProviderProfile> providers;
}
