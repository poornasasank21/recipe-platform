package com.user.userservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

;

@Data
@NoArgsConstructor
public class Cuisine {
    private Long id;
    private String name;
    private boolean isEnabled = true;
}
