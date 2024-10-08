package com.user.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CuisineResponse {
    private List<CuisineDTO> cuisines;
    private String message;
    private LocalDateTime timestamp;
}