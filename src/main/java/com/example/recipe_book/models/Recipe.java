package com.example.recipe_book.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document("recipes")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Recipe {
    @Id
    @Indexed(direction = IndexDirection.ASCENDING, unique = true, dropDups = true)
    private String id;
    private String description;
    private String ownerId;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<String> files;

    public Recipe(@JsonProperty("description") String description,
                  @JsonProperty("ownerId") String ownerId,
                  @JsonProperty("ingredients") ArrayList<Ingredient> ingredients) {
        this.description = description;
        this.ownerId = ownerId;
        this.ingredients = ingredients;
    }

	public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

	public ArrayList<String> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<String> filesCodes) {
        this.files = filesCodes;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", ingredients=" + ingredients.toString() +
                ", files=" + files +
                '}';
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class Ingredient {
        private final String name;
        private final Integer ammount;
        private final String unit;

        public Ingredient(@JsonProperty("name") String name,
                          @JsonProperty("ammount") Integer ammount,
                          @JsonProperty("unit") String unit) {
            this.name = name;
            this.ammount = ammount;
            this.unit = unit;
        }

        @Override
        public String toString() {
            return "Ingredient{" +
                    "name='" + name + '\'' +
                    ", ammount=" + ammount +
                    ", unit='" + unit + '\'' +
                    '}';
        }
    }
}
