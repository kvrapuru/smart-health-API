package com.example.demo.service;

import com.example.demo.model.WaterLog;
import com.example.demo.repository.WaterLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WaterLogService {
    @Autowired
    private WaterLogRepository waterLogRepository;

    public List<WaterLog> getWaterLogsByUserId(Long userId) {
        return waterLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public List<WaterLog> getWaterLogsByUserIdAndDate(Long userId, LocalDateTime start, LocalDateTime end) {
        return waterLogRepository.findByUserIdAndTimestampBetweenOrderByTimestampDesc(userId, start, end);
    }

    public WaterLog addWaterLog(WaterLog waterLog) {
        return waterLogRepository.save(waterLog);
    }

    public void resetWaterLog(Long userId) {
        // Implementation for resetting water log
        // This could involve deleting all logs for the day or setting them to zero
    }
} 