package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.dto.ErrorResponse;
import com.example.demo.model.UserProfile;
import com.example.demo.service.UserProfileService;
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
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
@Tag(name = "User Profile", description = "User profile management APIs")
public class UserProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Operation(summary = "Get current user profile", description = "Retrieves the profile of the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile",
            content = @Content(schema = @Schema(implementation = UserProfile.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "400", description = "Error retrieving profile",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            User user = userService.getCurrentUser();
            UserProfile profile = userProfileService.getUserProfile(user.getId(), LocalDateTime.now());
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to get profile", e.getMessage()));
        }
    }

    @Operation(summary = "Update user profile", description = "Updates the profile of the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated user profile",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "400", description = "Error updating profile",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping
    public ResponseEntity<?> updateUserProfile(@RequestBody UserProfileUpdateRequest request) {
        try {
            User user = userService.getCurrentUser();
            user.setName(request.getName());
            user.setGender(request.getGender());
            user.setHeight(request.getHeight());
            user.setHeightUnit(request.getHeightUnit());
            user.setWeightUnit(request.getWeightUnit());
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to update profile", e.getMessage()));
        }
    }

    @Operation(summary = "Get user profile by user ID", description = "Retrieves the profile of a specific user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile",
            content = @Content(schema = @Schema(implementation = UserProfile.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}/profile")
    public ResponseEntity<UserProfile> getUserProfile(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Date for profile (ISO format)")
            @RequestParam(required = false) String date) {
        LocalDateTime profileDate = date != null ? LocalDateTime.parse(date) : LocalDateTime.now();
        return ResponseEntity.ok(userProfileService.getUserProfile(userId, profileDate));
    }

    @Operation(summary = "Create user profile", description = "Creates a new profile for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created user profile",
            content = @Content(schema = @Schema(implementation = UserProfile.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid profile data")
    })
    @PostMapping("/user/{userId}/profile")
    public ResponseEntity<UserProfile> createUserProfile(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @RequestBody UserProfile userProfile) {
        return ResponseEntity.ok(userProfileService.saveUserProfile(userProfile));
    }

    @Operation(summary = "Create profile", description = "Creates a new profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created profile",
            content = @Content(schema = @Schema(implementation = UserProfile.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid profile data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<?> createProfile(@RequestBody UserProfile profile) {
        try {
            UserProfile savedProfile = userProfileService.createProfile(profile);
            return ResponseEntity.ok(savedProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Bad Request", e.getMessage()));
        }
    }
}

class UserProfileUpdateRequest {
    private String name;
    private String gender;
    private Double height;
    private String heightUnit;
    private String weightUnit;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getHeightUnit() {
        return heightUnit;
    }

    public void setHeightUnit(String heightUnit) {
        this.heightUnit = heightUnit;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }
} 