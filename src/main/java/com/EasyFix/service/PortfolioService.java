package com.EasyFix.service;

import com.EasyFix.model.PortfolioImage;
import com.EasyFix.model.User;
import com.EasyFix.repository.PortfolioImageRepository;
import com.EasyFix.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class PortfolioService {

    private final PortfolioImageRepository portfolioRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir:uploads/portfolio/}")
    private String uploadDir;

    public PortfolioService(PortfolioImageRepository portfolioRepository, UserRepository userRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
    }

    public PortfolioImage uploadWorkImage(String email, MultipartFile file, String description) throws IOException {
        User provider = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Provider account not found."));

        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = path.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        PortfolioImage portfolioImage = new PortfolioImage();
        portfolioImage.setProvider(provider);
        portfolioImage.setWorkImage(fileName);
        portfolioImage.setDescription(description);

        return portfolioRepository.save(portfolioImage);
    }

    public void deletePortfolioImage(Long imageId) {
        if (!portfolioRepository.existsById(imageId)) {
            throw new RuntimeException("Portfolio image not found with ID: " + imageId);
        }
        portfolioRepository.deleteById(imageId);
    }
}