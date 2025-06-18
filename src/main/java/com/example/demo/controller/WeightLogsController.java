package com.example.demo.controller;

import com.example.demo.model.WeightLog;
import com.example.demo.model.User;
import com.example.demo.service.WeightLogService;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class WeightLogsController {
    @Autowired
    private WeightLogService weightLogService;

    @Autowired
    private UserRepository userRepository;

    // GET /api/weightLogs?userId={userId}&date={YYYY-MM-DD}
    @GetMapping("/weightLogs")
    public ResponseEntity<List<Map<String, Object>>> getWeightLogs(
            @RequestParam Long userId,
            @RequestParam(required = false) String date) {
        try {
            List<WeightLog> logs;
            if (date != null && !date.isEmpty()) {
                LocalDate targetDate = LocalDate.parse(date);
                LocalDateTime startOfDay = targetDate.atStartOfDay();
                LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);
                logs = weightLogService.getWeightLogsByUserIdAndDate(userId, startOfDay, endOfDay);
            } else {
                logs = weightLogService.getWeightLogsByUserId(userId);
            }
            
            List<Map<String, Object>> response = logs.stream()
                    .map(log -> {
                        Map<String, Object> weightData = new HashMap<>();
                        weightData.put("id", log.getId().toString());
                        weightData.put("userId", log.getUserId().toString());
                        weightData.put("weight", log.getWeight());
                        weightData.put("targetWeight", log.getTargetWeight());
                        weightData.put("date", log.getTimestamp().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                        weightData.put("timestamp", log.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));
                        weightData.put("unit", log.getUnit());
                        return weightData;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // POST endpoint for adding weight logs
    @PostMapping("/weightLogs")
    public ResponseEntity<Map<String, Object>> addWeightLog(@RequestBody Map<String, Object> weightData) {
        try {
            // Extract data from request
            Long userId = Long.parseLong(weightData.get("userId").toString());
            Double weight = Double.parseDouble(weightData.get("weight").toString());
            Double targetWeight = weightData.get("targetWeight") != null ? 
                Double.parseDouble(weightData.get("targetWeight").toString()) : null;
            String date = weightData.get("date").toString();
            String timestamp = weightData.get("timestamp").toString();

            // Find the user
            User user = userRepository.findById(userId)
                    .orElse(null);
            
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // Create new weight log
            WeightLog weightLog = new WeightLog();
            weightLog.setUserId(userId);
            weightLog.setWeight(weight);
            weightLog.setTargetWeight(targetWeight);
            weightLog.setTimestamp(LocalDateTime.parse(timestamp));
            weightLog.setUnit("KG"); // Default unit

            // Save the weight log
            WeightLog savedLog = weightLogService.addWeightLog(weightLog);

            // Return in frontend format with data wrapper
            Map<String, Object> data = new HashMap<>();
            data.put("id", savedLog.getId());
            data.put("userId", savedLog.getUserId());
            data.put("weight", savedLog.getWeight());
            data.put("targetWeight", savedLog.getTargetWeight());
            data.put("date", savedLog.getTimestamp().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            data.put("timestamp", savedLog.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));
            data.put("unit", savedLog.getUnit());

            Map<String, Object> response = new HashMap<>();
            response.put("data", data);

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 