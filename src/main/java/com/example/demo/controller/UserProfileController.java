package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.dto.ErrorResponse;
import com.example.demo.model.UserProfile;
import com.example.demo.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
public class UserProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @GetMapping
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        try {
            // User user = userService.getUserFromToken(token); // JWT removed, implement user retrieval as needed
            return ResponseEntity.ok(userService.getUserFromToken(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to get profile", e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateUserProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UserProfileUpdateRequest request) {
        try {
            // User user = userService.getUserFromToken(token); // JWT removed, implement user retrieval as needed
            User user = userService.getUserFromToken(token);
            user.setName(request.getName());
            user.setGender(request.getGender());
            user.setDateOfBirth(request.getDateOfBirth() != null ? LocalDateTime.parse(request.getDateOfBirth()) : null);
            user.setHeight(request.getHeight());
            user.setHeightUnit(request.getHeightUnit());
            user.setWeightUnit(request.getWeightUnit());
            
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to update profile", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/profile")
    public ResponseEntity<UserProfile> getUserProfile(
            @PathVariable Long userId,
            @RequestParam(required = false) String date) {
        LocalDateTime profileDate = date != null ? LocalDateTime.parse(date) : LocalDateTime.now();
        return ResponseEntity.ok(userProfileService.getUserProfile(userId, profileDate));
    }

    @PostMapping("/user/{userId}/profile")
    public ResponseEntity<UserProfile> createUserProfile(
            @PathVariable Long userId,
            @RequestBody UserProfile userProfile) {
        return ResponseEntity.ok(userProfileService.saveUserProfile(userProfile));
    }

    @PostMapping
    public ResponseEntity<?> createProfile(@RequestBody UserProfile profile) {
        try {
            if (profile.getDateOfBirth() != null) {
                LocalDateTime dob = LocalDateTime.parse(profile.getDateOfBirth().toString(), formatter);
                profile.setDateOfBirth(dob);
            }
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
    private String dateOfBirth;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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