package com.user.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserDTO {
        private String firstName;
        private String lastName;
        private String email;
        private Boolean enabled;
        private CountryDTO country;
        private String region;
}
