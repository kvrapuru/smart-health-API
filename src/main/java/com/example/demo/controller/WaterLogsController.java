package com.example.demo.controller;

import com.example.demo.model.WaterLog;
import com.example.demo.model.User;
import com.example.demo.service.WaterLogService;
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
public class WaterLogsController {
    @Autowired
    private WaterLogService waterLogService;

    @Autowired
    private UserRepository userRepository;

    // GET /api/waterLogs?userId={userId}&date={YYYY-MM-DD}
    @GetMapping("/waterLogs")
    public ResponseEntity<List<Map<String, Object>>> getWaterLogs(
            @RequestParam Long userId,
            @RequestParam(required = false) String date) {
        try {
            List<WaterLog> logs;
            if (date != null && !date.isEmpty()) {
                LocalDate targetDate = LocalDate.parse(date);
                LocalDateTime startOfDay = targetDate.atStartOfDay();
                LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);
                logs = waterLogService.getWaterLogsByUserIdAndDate(userId, startOfDay, endOfDay);
            } else {
                logs = waterLogService.getWaterLogsByUserId(userId);
            }
            List<Map<String, Object>> response = logs.stream().map(log -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", log.getId().toString());
                map.put("userId", log.getUserId().toString());
                map.put("amount", log.getAmount());
                map.put("date", log.getTimestamp().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                map.put("timestamp", log.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));
                return map;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // POST endpoint for adding water logs
    @PostMapping("/waterLogs")
    public ResponseEntity<Map<String, Object>> addWaterLog(@RequestBody Map<String, Object> waterData) {
        try {
            // Extract data from request
            Long userId = Long.parseLong(waterData.get("userId").toString());
            Double amount = Double.parseDouble(waterData.get("amount").toString());
            String date = waterData.get("date").toString();
            String timestamp = waterData.get("timestamp").toString();

            // Find the user
            User user = userRepository.findById(userId)
                    .orElse(null);
            
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // Create new water log
            WaterLog waterLog = new WaterLog();
            waterLog.setUserId(userId);
            waterLog.setAmount(amount);
            waterLog.setTimestamp(LocalDateTime.parse(timestamp.replace("Z", "")));

            // Save the water log
            WaterLog savedLog = waterLogService.addWaterLog(waterLog);

            // Return in frontend format with data wrapper
            Map<String, Object> data = new HashMap<>();
            data.put("id", savedLog.getId().toString());
            data.put("userId", savedLog.getUserId());
            data.put("amount", savedLog.getAmount());
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