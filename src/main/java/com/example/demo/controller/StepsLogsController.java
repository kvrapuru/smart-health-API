package com.example.demo.controller;

import com.example.demo.model.StepsLog;
import com.example.demo.model.User;
import com.example.demo.service.StepsLogService;
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
public class StepsLogsController {
    @Autowired
    private StepsLogService stepsLogService;

    @Autowired
    private UserRepository userRepository;

    // GET /api/stepsLogs?userId={userId}&date={YYYY-MM-DD}
    @GetMapping("/stepsLogs")
    public ResponseEntity<List<Map<String, Object>>> getStepsLogs(
            @RequestParam Long userId,
            @RequestParam(required = false) String date) {
        try {
            List<StepsLog> logs;
            if (date != null && !date.isEmpty()) {
                LocalDate targetDate = LocalDate.parse(date);
                LocalDateTime startOfDay = targetDate.atStartOfDay();
                LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);
                logs = stepsLogService.getStepsLogsByUserIdAndDate(userId, startOfDay, endOfDay);
            } else {
                logs = stepsLogService.getStepsLogsByUserId(userId);
            }
            List<Map<String, Object>> response = logs.stream().map(log -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", log.getId().toString());
                map.put("userId", log.getUserId().toString());
                map.put("steps", log.getSteps());
                map.put("date", log.getTimestamp().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                map.put("timestamp", log.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));
                return map;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // POST endpoint for adding steps logs
    @PostMapping("/stepsLogs")
    public ResponseEntity<Map<String, Object>> addStepsLog(@RequestBody Map<String, Object> stepsData) {
        try {
            // Extract data from request
            Long userId = Long.parseLong(stepsData.get("userId").toString());
            Integer steps = Integer.parseInt(stepsData.get("steps").toString());
            String date = stepsData.get("date").toString();
            String timestamp = stepsData.get("timestamp").toString();

            // Find the user
            User user = userRepository.findById(userId)
                    .orElse(null);
            
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // Create new steps log
            StepsLog stepsLog = new StepsLog();
            stepsLog.setUserId(userId);
            stepsLog.setSteps(steps);
            stepsLog.setTimestamp(LocalDateTime.parse(timestamp.replace("Z", "")));

            // Save the steps log
            StepsLog savedLog = stepsLogService.addStepsLog(stepsLog);

            // Return in frontend format with data wrapper
            Map<String, Object> data = new HashMap<>();
            data.put("id", savedLog.getId().toString());
            data.put("userId", savedLog.getUserId());
            data.put("steps", savedLog.getSteps());
            data.put("date", savedLog.getTimestamp().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            data.put("timestamp", savedLog.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));

            Map<String, Object> response = new HashMap<>();
            response.put("data", data);

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 