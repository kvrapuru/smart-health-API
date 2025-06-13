package com.example.demo.service;

import com.example.demo.model.UserGoal;
import com.example.demo.repository.UserGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserGoalService {

    @Autowired
    private UserGoalRepository userGoalRepository;

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

    public UserGoal createGoal(UserGoal goal) {
        return userGoalRepository.save(goal);
    }

    public UserGoal updateGoal(Long id, UserGoal goal) {
        UserGoal existingGoal = getGoal(id);
        if (existingGoal == null) {
            throw new RuntimeException("Goal not found");
        }
        
        // Update fields
        existingGoal.setGoalType(goal.getGoalType());
        existingGoal.setTargetValue(goal.getTargetValue());
        existingGoal.setStartDate(goal.getStartDate());
        existingGoal.setEndDate(goal.getEndDate());
        existingGoal.setStatus(goal.getStatus());
        existingGoal.setDescription(goal.getDescription());
        
        return userGoalRepository.save(existingGoal);
    }

    public void deleteGoal(Long goalId) {
        userGoalRepository.deleteById(goalId);
    }

    public UserGoal saveUserGoal(UserGoal userGoal) {
        return userGoalRepository.save(userGoal);
    }
} 