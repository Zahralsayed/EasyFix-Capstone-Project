package com.EasyFix.service;

import com.EasyFix.dto.UserUpdateRequest;
import com.EasyFix.enums.Role;
import com.EasyFix.enums.UserStatus;
import com.EasyFix.model.ProviderDetails;
import com.EasyFix.model.request.LoginRequest;
import com.EasyFix.repository.AppointmentRepository;
import com.EasyFix.repository.ServiceCategoryRepository;
import com.EasyFix.repository.UserRepository;
import com.EasyFix.security.JWTUtils;
import com.EasyFix.security.MyUserDetails;
import com.EasyFix.security.MyUserDetailsService;
import jakarta.servlet.ServletContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.EasyFix.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {
    private final Path root = Paths.get("src/main/profile-pics");
    private final UserRepository userRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final ServletContext servletContext;
//    private final AppointmentRepository appointmentRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService userDetailsService;

    @Autowired
    public UserService(UserRepository userRepository, ServiceCategoryRepository categoryRepository, ServletContext servletContext,
                       EmailService emailService,
                       @Lazy PasswordEncoder passwordEncoder,
                       JWTUtils jwtUtils,
                       @Lazy AuthenticationManager authenticationManager,
                       @Lazy MyUserDetailsService myUserDetailsService) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.servletContext = servletContext;
//        this.appointmentRepository = appointmentRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = myUserDetailsService;
//        this.tokenService = tokenService;

        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage folder", e);
        }
    }

    @Transactional
    public User register(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email is already registered!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Generate the unique token
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerified(false);
        user.setStatus(UserStatus.PENDING_VERIFICATION);

        User savedUser = userRepository.save(user);

        // Send the email
        this.emailService.sendVerificationLink(savedUser.getEmail(), savedUser.getUsername(), token);

        return savedUser;
    }

    @Transactional
    public boolean verifyUser(String token) {
        return userRepository.findByVerificationToken(token)
                .map(user -> {
                    user.setVerified(true);
                    user.setStatus(UserStatus.ACTIVE);
                    user.setVerificationToken(null); // Clear, "one-time" use
                    userRepository.save(user);
                    return true;
                }).orElse(false);
    }

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        System.out.println("Service Calling loginUser ==> ");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.email());
            MyUserDetails myUser = (MyUserDetails) userDetails;

            User user = myUser.getUser();

            if (user.getStatus() == UserStatus.INACTIVE) {
                throw new RuntimeException("This account has been deactivated. Please contact support.");
            }

            if (myUser.getUser().getStatus() == UserStatus.PENDING_VERIFICATION) {
                String token = UUID.randomUUID().toString();

                userRepository.save(user);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "error", "Your account is inactive. Please verify your email.",
                                "verificationUrl", "http://localhost:8080/auth/users/verify?token=" + token
                        ));
            }

            String jwt = jwtUtils.generateToken(userDetails);

            return ResponseEntity.ok(
                    Map.of(
                            "email", userDetails.getUsername(),
                            "username", myUser.getUser().getUsername(),
                            "roles", userDetails.getAuthorities(),
                            "token", jwt
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password!"));
        }
    }

    public void changePassword(String email, String oldPassword ,String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus().equals(UserStatus.ACTIVE)) {
            if (!passwordEncoder.matches(oldPassword, user.getPassword())){
                throw new RuntimeException("The old password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new RuntimeException("You Can't Change Password For " + user.getStatus()+ " Account.");
        }
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        emailService.sendResetLink(user.getEmail(), token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset link."));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }

    public String uploadImage(String email, MultipartFile file) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }
        String filename = user.getUsername() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();


        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, this.root.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        }
        user.setProfilePicture(filename);
        userRepository.save(user);

        return filename;
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

    @Transactional
    public User updateFullProfile(String email, UserUpdateRequest request) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null && !request.getAddress().isBlank()) {
            user.setAddress(request.getAddress());
        }

        MultipartFile file = request.getFile();
        if (file != null && !file.isEmpty()) {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            if (user.getProfilePicture() != null) {
                try {
                    Path oldPath = root.resolve(user.getProfilePicture());
                    Files.deleteIfExists(oldPath);
                } catch (IOException e) {
                    System.err.println("Could not delete old image file: " + e.getMessage());
                }
            }

            String newFilename = user.getId() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), this.root.resolve(newFilename), StandardCopyOption.REPLACE_EXISTING);

            user.setProfilePicture(newFilename);
        }

        return userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllCustomers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.CUSTOMER)
                .toList();
    }

    public List<User> getPendingProviders(){
        return userRepository.findByStatus(UserStatus.PENDING_APPROVAL);
    }

    @Transactional
    public User updateProviderDetails(String email, ProviderDetails newDetails, Long categoryId) {
        User user = userRepository.findByEmail(email).orElseThrow();

        if (user.getRole() != Role.PROVIDER) {
            throw new RuntimeException("Only provider roles can onboard details.");
        }

        user.setCategory(categoryRepository.findById(categoryId).orElseThrow());
        user.setProviderDetails(newDetails);

        user.setStatus(UserStatus.PENDING_APPROVAL);

        return userRepository.save(user);
    }

    @Transactional
    public User approveProvider(Long providerId) {
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider account not found."));

        if (provider.getRole() != Role.PROVIDER) {
            throw new RuntimeException("Account is not a maintenance professional provider.");
        }

        provider.setStatus(UserStatus.ACTIVE);
        return userRepository.save(provider);
    }




//    public List<User> getCustomersForProviderViaStreams(Long providerId) {
//        return appointmentRepository.findAll().stream()
//                .filter(appointment -> appointment.getProvider().getId().equals(providerId))
//                .map(Booking::getCustomer)
//                .distinct()
//                .collect(Collectors.toList());
//    }

    @Transactional
    public String deleteUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            return "The user was already deleted or does not exist.";
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User " + userId + " not found."));

        if (user.getStatus() == UserStatus.INACTIVE) {
            return "User account is already deactivated.";
        }

        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        if (user.getRole() == Role.ADMIN) {
            return "Admin account deactivated.";
        } else {
            return "User account has been successfully deactivated (soft deleted).";
        }
    }
}
