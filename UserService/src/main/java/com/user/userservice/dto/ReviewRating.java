package com.user.userservice.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRating {

    private Long id;
    private Long userId;
    private Long recipeId;
    private Integer rating;
    private String description;
    private String createdAt;
    private String email;
    private String image;

}
