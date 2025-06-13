package com.example.demo.repository;

import com.example.demo.model.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {
    List<UserGoal> findByUserId(Long userId);
    List<UserGoal> findByUserIdAndStartDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    List<UserGoal> findByUserIdAndStatus(Long userId, String status);
    List<UserGoal> findByUserIdAndStatusAndStartDateBetween(Long userId, String status, LocalDateTime startDate, LocalDateTime endDate);
} 