package com.user.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate date;
    private Boolean enabled;
    private String country;
}
