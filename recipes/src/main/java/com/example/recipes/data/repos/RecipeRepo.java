package com.example.recipes.data.repos;

import java.util.List;

import com.example.recipes.data.entity.Recipe;

import org.springframework.data.repository.CrudRepository;

public interface RecipeRepo  extends CrudRepository<Recipe,Long> {
    List<Recipe> findByName(String name);
    Boolean existsByName(String name);
    Recipe findFirstByNameContaining(String name);
    List<Recipe> findByNameContaining(String name);
}
