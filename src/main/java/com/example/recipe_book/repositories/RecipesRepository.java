package com.example.recipe_book.repositories;

import com.example.recipe_book.models.Recipe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipesRepository extends MongoRepository<Recipe, String> {
    Optional<Recipe> findById(String id);
    Iterable<Recipe> findAllByOwnerId(String ownerId);
}
