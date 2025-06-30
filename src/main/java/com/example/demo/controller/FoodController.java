package com.example.demo.controller;

import com.example.demo.model.Food;
import com.example.demo.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = "*")
@Tag(name = "Food Management", description = "Food database management APIs")
public class FoodController {

    @Autowired
    private FoodRepository foodRepository;

    @Operation(summary = "Get all foods", description = "Retrieves a list of all foods in the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all foods",
            content = @Content(schema = @Schema(implementation = Food.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping
    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    @Operation(summary = "Get food by ID", description = "Retrieves a specific food by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved food",
            content = @Content(schema = @Schema(implementation = Food.class))),
        @ApiResponse(responseCode = "404", description = "Food not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Food> getFoodById(
        @Parameter(description = "Food ID", required = true)
        @PathVariable Long id) {
        return foodRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create food", description = "Creates a new food entry in the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created food",
            content = @Content(schema = @Schema(implementation = Food.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid food data")
    })
    @PostMapping
    public Food createFood(@RequestBody Food food) {
        return foodRepository.save(food);
    }

    @Operation(summary = "Update food", description = "Updates an existing food entry")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated food",
            content = @Content(schema = @Schema(implementation = Food.class))),
        @ApiResponse(responseCode = "404", description = "Food not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid food data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Food> updateFood(
        @Parameter(description = "Food ID", required = true)
        @PathVariable Long id, 
        @RequestBody Food foodDetails) {
        return foodRepository.findById(id)
                .map(existingFood -> {
                    existingFood.setName(foodDetails.getName());
                    existingFood.setCalories(foodDetails.getCalories());
                    existingFood.setProtein(foodDetails.getProtein());
                    existingFood.setCarbs(foodDetails.getCarbs());
                    existingFood.setFat(foodDetails.getFat());
                    existingFood.setServingSize(foodDetails.getServingSize());
                    existingFood.setServingUnit(foodDetails.getServingUnit());
                    return ResponseEntity.ok(foodRepository.save(existingFood));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete food", description = "Deletes a food entry from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted food"),
        @ApiResponse(responseCode = "404", description = "Food not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFood(
        @Parameter(description = "Food ID", required = true)
        @PathVariable Long id) {
        return foodRepository.findById(id)
                .map(food -> {
                    foodRepository.delete(food);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 