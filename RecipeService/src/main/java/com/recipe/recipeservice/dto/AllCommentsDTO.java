package com.recipe.recipeservice.dto;
import com.recipe.recipeservice.entity.ReviewRating;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllCommentsDTO {
    private List<ReviewRating> reviews;
}
