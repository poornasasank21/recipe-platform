package com.recipe.recipeservice.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table
public class Cuisine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private boolean isEnabled;
    private String imageUrl;

    @OneToMany(mappedBy = "cuisine")
    @JsonIgnore
    private List<Recipe> recipes;
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

}