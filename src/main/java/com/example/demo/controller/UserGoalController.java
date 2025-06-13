package com.example.demo.controller;

import com.example.demo.dto.ErrorResponse;
import com.example.demo.model.User;
import com.example.demo.model.UserGoal;
import com.example.demo.service.UserGoalService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/goals")
public class UserGoalController {

    @Autowired
    private UserGoalService userGoalService;

    @Autowired
    private UserService userService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @GetMapping
    public ResponseEntity<?> getUserGoals(@RequestHeader("Authorization") String token) {
        try {
            User user = userService.getUserFromToken(token);
            List<UserGoal> goals = userGoalService.getUserGoals(user.getId());
            return ResponseEntity.ok(goals);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to get goals", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createGoal(@RequestBody UserGoal goal) {
        try {
            if (goal.getStartDate() != null) {
                goal.setStartDate(LocalDateTime.parse(goal.getStartDate().toString(), formatter));
            }
            if (goal.getEndDate() != null) {
                goal.setEndDate(LocalDateTime.parse(goal.getEndDate().toString(), formatter));
            }
            UserGoal savedGoal = userGoalService.createGoal(goal);
            return ResponseEntity.ok(savedGoal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Bad Request", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGoal(@PathVariable Long id, @RequestBody UserGoal goal) {
        try {
            if (goal.getStartDate() != null) {
                goal.setStartDate(LocalDateTime.parse(goal.getStartDate().toString(), formatter));
            }
            if (goal.getEndDate() != null) {
                goal.setEndDate(LocalDateTime.parse(goal.getEndDate().toString(), formatter));
            }
            UserGoal updatedGoal = userGoalService.updateGoal(id, goal);
            return ResponseEntity.ok(updatedGoal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Bad Request", e.getMessage()));
        }
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<?> deleteUserGoal(
            @RequestHeader("Authorization") String token,
            @PathVariable Long goalId) {
        try {
            User user = userService.getUserFromToken(token);
            UserGoal goal = userGoalService.getGoal(goalId);
            
            if (!goal.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(new ErrorResponse("Forbidden", "You don't have permission to delete this goal"));
            }

            userGoalService.deleteGoal(goalId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to delete goal", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/goals")
    public ResponseEntity<List<UserGoal>> getUserGoals(
            @PathVariable Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;
        return ResponseEntity.ok(userGoalService.getUserGoals(userId, start, end));
    }

    @GetMapping("/user/{userId}/goals/active")
    public ResponseEntity<List<UserGoal>> getActiveUserGoals(
            @PathVariable Long userId,
            @RequestParam(required = false) String startDate,
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