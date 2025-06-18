package com.example.demo.service;

import com.example.demo.model.UserGoal;
import com.example.demo.repository.UserGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Tag(name = "User Goal Service", description = "Service for managing user goals")
public class UserGoalService {
    private static final Logger logger = LoggerFactory.getLogger(UserGoalService.class);

    @Autowired
    private UserGoalRepository userGoalRepository;

    @Operation(summary = "Get user goals", description = "Retrieves all goals for a user")
    public List<UserGoal> getUserGoals(Long userId) {
        return userGoalRepository.findByUserId(userId);
    }

    public List<UserGoal> getUserGoals(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) {
            return getUserGoals(userId);
        }
        return userGoalRepository.findByUserIdAndStartDateBetween(userId, startDate, endDate);
    }

    public List<UserGoal> getActiveUserGoals(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) {
            return userGoalRepository.findByUserIdAndStatus(userId, "ACTIVE");
        }
        return userGoalRepository.findByUserIdAndStatusAndStartDateBetween(userId, "ACTIVE", startDate, endDate);
    }

    public UserGoal getGoal(Long goalId) {
        return userGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
    }

    @Operation(summary = "Create user goal", description = "Creates a new goal for a user")
    public UserGoal createUserGoal(UserGoal goal) {
        goal.setCreatedAt(LocalDateTime.now());
        goal.setUpdatedAt(LocalDateTime.now());
        return userGoalRepository.save(goal);
    }

    @Operation(summary = "Update user goal", description = "Updates an existing goal")
    public UserGoal updateUserGoal(UserGoal goal) {
        goal.setUpdatedAt(LocalDateTime.now());
        return userGoalRepository.save(goal);
    }

    @Operation(summary = "Delete user goal", description = "Deletes a goal by its ID")
    public void deleteUserGoal(Long id) {
        userGoalRepository.deleteById(id);
    }

    public UserGoal saveUserGoal(UserGoal userGoal) {
        return userGoalRepository.save(userGoal);
    }
} 