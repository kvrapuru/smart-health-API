package com.example.demo.repository;

import com.example.demo.model.FoodLog;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface FoodLogRepository extends JpaRepository<FoodLog, Long> {
    List<FoodLog> findByUser(User user);
    List<FoodLog> findByUserAndTimestampBetween(User user, LocalDateTime start, LocalDateTime end);
} 