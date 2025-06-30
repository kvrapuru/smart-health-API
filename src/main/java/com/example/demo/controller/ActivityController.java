package com.example.demo.controller;

import com.example.demo.model.Activity;
import com.example.demo.repository.ActivityRepository;
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
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")
@Tag(name = "Activity Management", description = "Activity database management APIs")
public class ActivityController {

    @Autowired
    private ActivityRepository activityRepository;

    @Operation(summary = "Get all activities", description = "Retrieves a list of all activities in the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all activities",
            content = @Content(schema = @Schema(implementation = Activity.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    @Operation(summary = "Get activity by ID", description = "Retrieves a specific activity by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved activity",
            content = @Content(schema = @Schema(implementation = Activity.class))),
        @ApiResponse(responseCode = "404", description = "Activity not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivityById(
        @Parameter(description = "Activity ID", required = true)
        @PathVariable Long id) {
        return activityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create activity", description = "Creates a new activity entry in the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created activity",
            content = @Content(schema = @Schema(implementation = Activity.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid activity data")
    })
    @PostMapping
    public Activity createActivity(@RequestBody Activity activity) {
        return activityRepository.save(activity);
    }

    @Operation(summary = "Update activity", description = "Updates an existing activity entry")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated activity",
            content = @Content(schema = @Schema(implementation = Activity.class))),
        @ApiResponse(responseCode = "404", description = "Activity not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid activity data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(
        @Parameter(description = "Activity ID", required = true)
        @PathVariable Long id, 
        @RequestBody Activity activityDetails) {
        return activityRepository.findById(id)
                .map(existingActivity -> {
                    existingActivity.setName(activityDetails.getName());
                    existingActivity.setDescription(activityDetails.getDescription());
                    existingActivity.setCaloriesBurnedPerHour(activityDetails.getCaloriesBurnedPerHour());
                    existingActivity.setCategory(activityDetails.getCategory());
                    existingActivity.setIntensity(activityDetails.getIntensity());
                    return ResponseEntity.ok(activityRepository.save(existingActivity));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete activity", description = "Deletes an activity entry from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted activity"),
        @ApiResponse(responseCode = "404", description = "Activity not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivity(
        @Parameter(description = "Activity ID", required = true)
        @PathVariable Long id) {
        return activityRepository.findById(id)
                .map(activity -> {
                    activityRepository.delete(activity);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 