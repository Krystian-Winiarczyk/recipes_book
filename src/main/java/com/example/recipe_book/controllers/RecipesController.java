package com.example.recipe_book.controllers;

import com.example.recipe_book.models.Recipe;
import com.example.recipe_book.repositories.RecipesRepository;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/recipe")
public class RecipesController {
    @Autowired
    RecipesRepository recipesRepository;

    /**
     *  It should return all existed recipes in database
     *  @return Iterable<Recipe>
     */
    @GetMapping("/getRecipes")
    public @ResponseBody
    Iterable<Recipe> getRecipes() {
        return recipesRepository.findAll();
    }

    /**
     *  @Param String id
     *  @return Optional<Recipe>
     */
    @GetMapping("/{recipeId}")
    public Optional<Recipe> getRecipeById(@PathVariable("recipeId") String id) {
        System.out.println(id);
        return recipesRepository.findById(id);
    }

    /**
     *  @Param String ownerId
     *  @return Optional<Recipe>
     */
    @GetMapping("/user/{ownerId}")
    public Iterable<Recipe> getRecipeByOwnerId(@PathVariable("ownerId") String id) {
        System.out.println(((List<Recipe>) recipesRepository.findAllByOwnerId(id)).size());
        return recipesRepository.findAllByOwnerId(id);
    }

    /**
     *  @Param MultipartFile[] files
     *  @Param String unconvertedRecipe (recipe object converted to string)
     *  @return Optional<Recipe>
     */
    @PostMapping(value = "/addRecipes",
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addRecipes(@RequestParam(value = "files", required = false) MultipartFile[] files, @RequestParam("recipe") String unconvertedRecipe) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
            Recipe recipe = mapper.readValue(unconvertedRecipe, Recipe.class);

            ArrayList<String> fileCodes = new ArrayList<>();
            for (MultipartFile file: files) {
                Binary binary = new Binary(BsonBinarySubType.BINARY, file.getBytes());
                fileCodes.add(Base64.getEncoder().encodeToString(binary.getData()));
            };
            recipe.setFiles(fileCodes);

            recipesRepository.insert(recipe);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

}
