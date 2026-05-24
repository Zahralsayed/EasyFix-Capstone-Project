package com.EasyFix.repository;

import com.EasyFix.model.PortfolioImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioImageRepository extends JpaRepository<PortfolioImage,Long> {
    List<PortfolioImage> findByProviderId(Long providerId);
}
