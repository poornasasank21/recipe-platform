package com.user.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor

public class CuisineDTO {
    private Long id;
    private String name;
    private boolean isEnabled;
    private String imageUrl;
}

