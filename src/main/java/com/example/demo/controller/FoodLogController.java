package com.example.demo.controller;

import com.example.demo.model.FoodLog;
import com.example.demo.model.User;
import com.example.demo.repository.FoodLogRepository;
import com.example.demo.repository.UserRepository;
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

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/food-logs")
@CrossOrigin(origins = "*")
@Tag(name = "Food Logs (Legacy)", description = "Legacy food logging APIs")
public class FoodLogController {

    @Autowired
    private FoodLogRepository foodLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Get all food logs", description = "Retrieves all food logs from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all food logs",
            content = @Content(schema = @Schema(implementation = FoodLog.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping
    public List<FoodLog> getAllFoodLogs() {
        return foodLogRepository.findAll();
    }

    @Operation(summary = "Get food log by ID", description = "Retrieves a specific food log by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved food log",
            content = @Content(schema = @Schema(implementation = FoodLog.class))),
        @ApiResponse(responseCode = "404", description = "Food log not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FoodLog> getFoodLogById(
        @Parameter(description = "Food log ID", required = true)
        @PathVariable Long id) {
        return foodLogRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get food logs by user", description = "Retrieves all food logs for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved food logs",
            content = @Content(schema = @Schema(implementation = FoodLog.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FoodLog>> getFoodLogsByUser(
        @Parameter(description = "User ID", required = true)
        @PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(foodLogRepository.findByUser(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get food logs by user and date range", description = "Retrieves food logs for a user within a specific date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved food logs",
            content = @Content(schema = @Schema(implementation = FoodLog.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/user/{userId}/between")
    public ResponseEntity<List<FoodLog>> getFoodLogsByUserAndDateRange(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Start date and time", required = true)
            @RequestParam LocalDateTime start,
            @Parameter(description = "End date and time", required = true)
            @RequestParam LocalDateTime end) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(foodLogRepository.findByUserAndTimestampBetween(user, start, end)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create food log", description = "Creates a new food log entry")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created food log",
            content = @Content(schema = @Schema(implementation = FoodLog.class))),
        @ApiResponse(responseCode = "400", description = "Invalid food log data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @PostMapping
    public ResponseEntity<FoodLog> createFoodLog(@RequestBody FoodLog foodLog) {
        if (foodLog.getUser() == null || foodLog.getUser().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return userRepository.findById(foodLog.getUser().getId())
                .map(user -> {
                    foodLog.setUser(user);
                    return ResponseEntity.ok(foodLogRepository.save(foodLog));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update food log", description = "Updates an existing food log entry")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated food log",
            content = @Content(schema = @Schema(implementation = FoodLog.class))),
        @ApiResponse(responseCode = "404", description = "Food log not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid food log data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FoodLog> updateFoodLog(
        @Parameter(description = "Food log ID", required = true)
        @PathVariable Long id, 
        @RequestBody FoodLog foodLogDetails) {
        return foodLogRepository.findById(id)
                .map(existingFoodLog -> {
                    existingFoodLog.setFood(foodLogDetails.getFood());
                    existingFoodLog.setTimestamp(foodLogDetails.getTimestamp());
                    existingFoodLog.setQuantity(foodLogDetails.getQuantity());
                    existingFoodLog.setMealType(foodLogDetails.getMealType());
                    existingFoodLog.setNotes(foodLogDetails.getNotes());
                    return ResponseEntity.ok(foodLogRepository.save(existingFoodLog));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete food log", description = "Deletes a food log entry")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted food log"),
        @ApiResponse(responseCode = "404", description = "Food log not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFoodLog(
        @Parameter(description = "Food log ID", required = true)
        @PathVariable Long id) {
        return foodLogRepository.findById(id)
                .map(foodLog -> {
                    foodLogRepository.delete(foodLog);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 