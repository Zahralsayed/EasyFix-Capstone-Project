package com.EasyFix.controller;

import com.EasyFix.dto.UserUpdateRequest;
import com.EasyFix.enums.UserStatus;
import com.EasyFix.model.ProviderDetails;
import com.EasyFix.model.User;
import com.EasyFix.model.request.LoginRequest;
import com.EasyFix.service.UserService;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("auth/users")
public class UserController {
    @Autowired
    private UserService userService;
    private ServletContext servletContext;

    public UserController(UserService userService, ServletContext servletContext) {
        this.userService = userService;
        this.servletContext = servletContext;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        System.out.println("Calling registerUser ==> ");
        try {
            userService.register(user);
            return ResponseEntity.ok("Registration successful! Please check your email to verify.");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error",e.getMessage()));
        }
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("token") String token) {
        boolean isVerified  = userService.verifyUser(token);

        if (isVerified) {
            return "Account Verified Successfully";
        } else {
            return "Invalid or Expired Token";
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Calling login ==> ");
        return userService.login(loginRequest);
    }

//    @PostMapping("/change-password")
//    public String change(@RequestParam Long userId, @RequestParam String oldPassword, @RequestParam String newPassword) {
//        userService.changePassword(userId, oldPassword, newPassword);
//        return "Password updated successfully!";
//    }

    @PostMapping("/change-password")
    public String change(@RequestParam String email,
                         @RequestParam String oldPassword,
                         @RequestParam String newPassword) {
        userService.changePassword(email, oldPassword, newPassword);
        return "Password updated successfully!";
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        userService.forgotPassword(email);
        return ResponseEntity.ok("Reset link has been sent.");
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                @RequestParam String newPassword) {
        try {
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password has been reset successfully. You can now login.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending-providers")
    public ResponseEntity<List<User>> getPendingProviders() {
        return ResponseEntity.ok(userService.getPendingProviders());
    }

    @PreAuthorize("hasRole('PROVIDER')")
    @PutMapping("/provider/onboarding")
    public ResponseEntity<?> onboardProvider(
            @RequestBody ProviderDetails providerDetails,
            @RequestParam Long categoryId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String providerEmail = userDetails.getUsername();
            User updatedUser = userService.updateProviderDetails(providerEmail, providerDetails, categoryId);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/approve-provider/{id}")
    public ResponseEntity<?> approveProviderProfile(@PathVariable Long id) {
        try {
            User approvedUser = userService.approveProvider(id);
            return ResponseEntity.ok("Provider '" + approvedUser.getUsername() + "' is now approved and active!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            String filename = userService.uploadImage(principal.getName(), file);

            String fileDownloadUri = MvcUriComponentsBuilder
                    .fromMethodName(UserController.class, "serveFile", filename)
                    .build().toUriString();
            return ResponseEntity.ok(Map.of(
                    "message", "Image uploaded successfully",
                    "imageUrl", fileDownloadUri
            ));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = userService.loadAsResource(filename);

        String contentType = "image/jpeg/png"; // Default
        try {
            contentType = servletContext.getMimeType(file.getFile().getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("Could not determine file type.");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }


    @PutMapping(value = "/update-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(@ModelAttribute UserUpdateRequest request,
                                           Principal principal) throws IOException {
        User updated = userService.updateFullProfile(principal.getName(), request);
        return ResponseEntity.ok(updated);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/delete/{Id}")
    public ResponseEntity<?> delete(@PathVariable("Id") Long Id) {
        try {
            String message = userService.deleteUser(Id);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
