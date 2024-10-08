package com.user.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.user.userservice.entity.Country;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CountryListDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private String status;
    private List<Country> countryList;
}
