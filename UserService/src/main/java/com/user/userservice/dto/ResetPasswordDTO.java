package com.user.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDTO {
    @NotBlank(message = "Email is mandatory")
    @Pattern(
            regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$",
            message = "Email should be valid and follow the format 'example@domain.com'"
    )
    private String email;
    @NotBlank(message = "New password is mandatory")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{5,}$",
            message = "Password must be at least 5 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character."
    )
    private String newPassword;
    @NotBlank(message = "Confirm password is mandatory")
    private String confirmPassword;
}
