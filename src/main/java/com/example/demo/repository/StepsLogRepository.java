package com.example.demo.repository;

import com.example.demo.model.StepsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface StepsLogRepository extends JpaRepository<StepsLog, Long> {
    List<StepsLog> findByUserIdOrderByTimestampDesc(Long userId);
    List<StepsLog> findByUserIdAndTimestampBetweenOrderByTimestampDesc(Long userId, LocalDateTime start, LocalDateTime end);
} 