package com.example.demo.service;

import com.example.demo.model.WeightLog;
import com.example.demo.repository.WeightLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WeightLogService {
    @Autowired
    private WeightLogRepository weightLogRepository;

    public List<WeightLog> getWeightLogsByUserId(Long userId) {
        return weightLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public WeightLog addWeightLog(WeightLog weightLog) {
        return weightLogRepository.save(weightLog);
    }
} 