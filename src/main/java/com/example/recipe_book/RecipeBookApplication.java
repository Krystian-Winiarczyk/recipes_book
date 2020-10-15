package com.example.recipe_book;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;


@SpringBootApplication
public class RecipeBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeBookApplication.class, args);
    }

}

@RestController
@RequestMapping("/api/v1/recipe")
class RecipesController {
    @Autowired
    RecipesRepository recipesRepository;

    /**
     *  It should return all existed recipes in database
     *  @return Iterable<Recipe>
     */
    @GetMapping("/getRecipes")
    public @ResponseBody Iterable<Recipe> getRecipes() {
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

@Repository
interface RecipesRepository extends MongoRepository<Recipe, String> {
    Optional<Recipe> findById(String id);
    Iterable<Recipe> findAllByOwnerId(String ownerId);
}

@Document("recipes")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class Recipe {
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
}

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class Ingredient {
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