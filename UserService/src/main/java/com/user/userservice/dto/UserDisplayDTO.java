package com.user.userservice.dto;

import com.user.userservice.entity.Country;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDisplayDTO {
    private Long id;
    @NotBlank(message = "First Name is mandatory")
    private String firstName;
    @NotBlank(message = "Last Name is mandatory")
    private String lastName;
    @NotBlank(message = "Country is mandatory")
    private Country country;
    @NotBlank(message = "Region is mandatory")
    private String region;
    @NotBlank(message = "Profile Image is mandatory")
    private String profileImageUrl;
    @NotBlank(message = "Password is mandatory")
    private String password;
    @NotBlank(message = "Email is mandatory")
    private String email;
}
