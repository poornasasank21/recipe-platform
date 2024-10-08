package com.user.userservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryDTO {
    private Long id;
    @Column(name = "name", nullable = false)
    @NotBlank
    private String name;
    @JsonProperty("isEnabled")
    private boolean isEnabled;

}
