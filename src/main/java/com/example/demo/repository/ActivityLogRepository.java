package com.example.demo.repository;

import com.example.demo.model.ActivityLog;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByUser(User user);
    List<ActivityLog> findByUserAndStartTimeBetween(User user, LocalDateTime start, LocalDateTime end);
} 