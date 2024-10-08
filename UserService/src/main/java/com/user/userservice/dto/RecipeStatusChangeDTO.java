package com.user.userservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecipeStatusChangeDTO {
    private Long id;
    @JsonProperty("user")
    private Long userId;
    private String email;
    private String name;
    private Category category;
    private Cuisine cuisine;
    private Status status;
}
