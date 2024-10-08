package com.user.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuccessResponse {
    private String timestamp;
    private String status;
    private String message;
}