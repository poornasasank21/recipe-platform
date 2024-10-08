package com.user.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class UserRegistrationDTO {
    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is mandatory")
    private String password;

    private LocalDate date;
    private Boolean enabled;

    @NotBlank(message = "Country is mandatory")
    private String country;

    @NotBlank(message = "Region is mandatory")
    private String region;

    @NotNull(message = "File should be present")
    private MultipartFile file;
}
