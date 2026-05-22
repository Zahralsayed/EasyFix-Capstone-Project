package com.EasyFix.dto;

//import jakarta.validation.constraints.Pattern;
//import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserUpdateRequest {

//    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

//    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Invalid phone number format")
    private String phoneNumber;

//    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    private MultipartFile file;
}
