package com.example.demo.service;

import com.example.demo.model.UserProfile;
import com.example.demo.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserProfileService {
    @Autowired
    private UserProfileRepository userProfileRepository;

    public UserProfile getUserProfile(Long userId, LocalDateTime date) {
        return userProfileRepository.findByUserIdAndCreatedAtBeforeOrderByCreatedAtDesc(userId, date)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public UserProfile saveUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    public UserProfile createProfile(UserProfile profile) {
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());
        return userProfileRepository.save(profile);
    }
} 