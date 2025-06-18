package com.example.demo.service;

import com.example.demo.model.StepsLog;
import com.example.demo.repository.StepsLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StepsLogService {
    @Autowired
    private StepsLogRepository stepsLogRepository;

    public List<StepsLog> getStepsLogsByUserId(Long userId) {
        return stepsLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public StepsLog addStepsLog(StepsLog stepsLog) {
        return stepsLogRepository.save(stepsLog);
    }

    public void resetStepsLog(Long userId) {
        // Implementation for resetting steps log
        // This could involve deleting all logs for the day or setting them to zero
    }
} 