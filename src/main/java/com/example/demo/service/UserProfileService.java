package com.example.demo.service;

import com.example.demo.model.UserProfile;
import com.example.demo.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;

@Service
@Tag(name = "User Profile Service", description = "Service for managing user profiles")
public class UserProfileService {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Operation(summary = "Get user profile by ID and date", description = "Retrieves a user profile by its ID and date")
    public UserProfile getUserProfile(Long userId, LocalDateTime date) {
        return userProfileRepository.findByUserIdAndCreatedAtBeforeOrderByCreatedAtDesc(userId, date)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
    }

    @Operation(summary = "Create or update user profile", description = "Creates a new user profile or updates an existing one")
    public UserProfile saveUserProfile(UserProfile userProfile) {
        userProfile.setCreatedAt(LocalDateTime.now());
        userProfile.setUpdatedAt(LocalDateTime.now());
        return userProfileRepository.save(userProfile);
    }

    @Operation(summary = "Delete user profile", description = "Deletes a user profile by its ID")
    public void deleteUserProfile(Long id) {
        userProfileRepository.deleteById(id);
    }

    @Operation(summary = "Create new profile", description = "Creates a new user profile")
    public UserProfile createProfile(UserProfile profile) {
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());
        return userProfileRepository.save(profile);
    }
} 