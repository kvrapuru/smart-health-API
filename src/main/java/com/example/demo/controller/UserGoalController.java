package com.example.demo.controller;

import com.example.demo.dto.ErrorResponse;
import com.example.demo.model.User;
import com.example.demo.model.UserGoal;
import com.example.demo.service.UserGoalService;
import com.example.demo.service.UserService;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/goals")
@Tag(name = "User Goals", description = "User goals management APIs")
public class UserGoalController {

    @Autowired
    private UserGoalService userGoalService;

    @Autowired
    private UserService userService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Operation(summary = "Get user goals", description = "Retrieves all goals for the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user goals",
            content = @Content(schema = @Schema(implementation = UserGoal.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping
    public ResponseEntity<List<UserGoal>> getUserGoals() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(userGoalService.getUserGoals(user.getId()));
    }

    @Operation(summary = "Create user goal", description = "Creates a new goal for the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created user goal",
            content = @Content(schema = @Schema(implementation = UserGoal.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid goal data")
    })
    @PostMapping
    public ResponseEntity<?> createUserGoal(@RequestBody UserGoal goal) {
        try {
            User user = userService.getCurrentUser();
            goal.setUserId(user.getId());
            return ResponseEntity.ok(userGoalService.createUserGoal(goal));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to create goal", e.getMessage()));
        }
    }

    @Operation(summary = "Update user goal", description = "Updates an existing goal for the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated user goal",
            content = @Content(schema = @Schema(implementation = UserGoal.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid goal data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserGoal(@PathVariable Long id, @RequestBody UserGoal goal) {
        try {
            goal.setId(id);
            return ResponseEntity.ok(userGoalService.updateUserGoal(goal));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to update goal", e.getMessage()));
        }
    }

    @Operation(summary = "Delete user goal", description = "Deletes a goal for the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted user goal"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid goal id")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserGoal(@PathVariable Long id) {
        try {
            userGoalService.deleteUserGoal(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to delete goal", e.getMessage()));
        }
    }

    @Operation(summary = "Get user goals by user ID", description = "Retrieves all goals for a specific user with optional date range filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user goals",
            content = @Content(schema = @Schema(implementation = UserGoal.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/user/{userId}/goals")
    public ResponseEntity<List<UserGoal>> getUserGoals(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Start date (ISO format)")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "End date (ISO format)")
            @RequestParam(required = false) String endDate) {
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;
        return ResponseEntity.ok(userGoalService.getUserGoals(userId, start, end));
    }

    @Operation(summary = "Get active user goals by user ID", description = "Retrieves active goals for a specific user with optional date range filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active user goals",
            content = @Content(schema = @Schema(implementation = UserGoal.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/user/{userId}/goals/active")
    public ResponseEntity<List<UserGoal>> getActiveUserGoals(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Start date (ISO format)")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "End date (ISO format)")
            @RequestParam(required = false) String endDate) {
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;
        return ResponseEntity.ok(userGoalService.getActiveUserGoals(userId, start, end));
    }
}

class UserGoalRequest {
    private String goalType;
    private Double targetValue;
    private String startDate;
    private String endDate;
    private String description;
    private String status;

    // Getters and Setters
    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public Double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Double targetValue) {
        this.targetValue = targetValue;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 