package com.EasyFix.controller;

import com.EasyFix.model.PortfolioImage;
import com.EasyFix.repository.PortfolioImageRepository;
import com.EasyFix.service.PortfolioService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final PortfolioImageRepository portfolioRepository;

    public PortfolioController(PortfolioService portfolioService, PortfolioImageRepository portfolioRepository) {
        this.portfolioService = portfolioService;
        this.portfolioRepository = portfolioRepository;
    }

    @PreAuthorize("hasRole('PROVIDER')")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "description", required = false) String description,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            java.util.List<java.util.Map<String, Object>> responseList = new java.util.ArrayList<>();

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    PortfolioImage savedImage = portfolioService.uploadWorkImage(userDetails.getUsername(), file, description);

                    java.util.Map<String, Object> imageMap = new java.util.LinkedHashMap<>();
                    imageMap.put("imageId", savedImage.getId());
                    imageMap.put("workImage", savedImage.getWorkImage());
                    imageMap.put("description", savedImage.getDescription());

                    responseList.add(imageMap);
                }
            }

            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<com.EasyFix.dto.PortfolioResponseDTO>> getProviderPortfolio(@PathVariable Long providerId) {
        List<com.EasyFix.model.PortfolioImage> images = portfolioRepository.findByProviderId(providerId);

        List<com.EasyFix.dto.PortfolioResponseDTO> dtoList = images.stream().map(img -> {
            com.EasyFix.dto.PortfolioResponseDTO dto = new com.EasyFix.dto.PortfolioResponseDTO();
            dto.setId(img.getId());
            dto.setWorkImage(img.getWorkImage());
            dto.setDescription(img.getDescription());
            return dto;
        }).collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<?> removePortfolioImage(@PathVariable Long imageId) {
        try {
            portfolioService.deletePortfolioImage(imageId);
            return ResponseEntity.ok("Portfolio image deleted successfully by Admin.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
