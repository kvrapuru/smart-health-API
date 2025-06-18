package com.example.demo.controller;

import com.example.demo.model.ActivityLog;
import com.example.demo.model.Activity;
import com.example.demo.model.User;
import com.example.demo.repository.ActivityLogRepository;
import com.example.demo.repository.ActivityRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ActivityLogsController {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    // Endpoint to match frontend expectation: GET /api/activityLogs?userId=1&date=2025-06-18
    @GetMapping("/activityLogs")
    public ResponseEntity<List<Map<String, Object>>> getActivityLogs(
            @RequestParam Long userId,
            @RequestParam(required = false) String date) {
        
        try {
            // Find the user
            User user = userRepository.findById(userId)
                    .orElse(null);
            
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            List<ActivityLog> activityLogs;
            
            if (date != null && !date.isEmpty()) {
                // Filter by specific date
                LocalDate targetDate = LocalDate.parse(date);
                LocalDateTime startOfDay = targetDate.atStartOfDay();
                LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);
                
                activityLogs = activityLogRepository.findByUserAndStartTimeBetween(user, startOfDay, endOfDay);
            } else {
                // Get all activity logs for the user
                activityLogs = activityLogRepository.findByUser(user);
            }

            // Convert to frontend response format
            List<Map<String, Object>> response = activityLogs.stream()
                    .map(log -> {
                        Map<String, Object> activityData = new HashMap<>();
                        activityData.put("id", log.getId().toString());
                        activityData.put("userId", log.getUser().getId().toString());
                        activityData.put("activityName", log.getActivity() != null ? log.getActivity().getName() : "Unknown");
                        activityData.put("duration", log.getDuration());
                        activityData.put("caloriesBurned", log.getCaloriesBurned());
                        activityData.put("date", log.getStartTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                        activityData.put("timestamp", log.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME));
                        return activityData;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // POST endpoint for adding activity logs
    @PostMapping("/activityLogs")
    public ResponseEntity<Map<String, Object>> addActivityLog(@RequestBody Map<String, Object> activityData) {
        try {
            // Extract data from request
            Long userId = Long.parseLong(activityData.get("userId").toString());
            String activityName = activityData.get("activityName").toString();
            Double duration = Double.parseDouble(activityData.get("duration").toString());
            Double caloriesBurned = Double.parseDouble(activityData.get("caloriesBurned").toString());
            String date = activityData.get("date").toString();
            String timestamp = activityData.get("timestamp").toString();

            // Find the user
            User user = userRepository.findById(userId)
                    .orElse(null);
            
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // Find or create the Activity entity
            Activity activity = activityRepository.findByName(activityName);
            if (activity == null) {
                // Create a new Activity if it doesn't exist
                activity = new Activity();
                activity.setName(activityName);
                activity.setDescription("Activity created from frontend");
                activity.setCaloriesBurnedPerHour(caloriesBurned * 60 / duration); // Calculate per hour
                activity.setCategory("General");
                activity.setIntensity("moderate");
                activity = activityRepository.save(activity);
            }

            // Create new activity log
            ActivityLog activityLog = new ActivityLog();
            activityLog.setUser(user);
            activityLog.setActivity(activity); // Link to the Activity entity
            activityLog.setDuration(duration);
            activityLog.setCaloriesBurned(caloriesBurned);
            activityLog.setStartTime(LocalDateTime.parse(timestamp.replace("Z", "")));
            activityLog.setEndTime(LocalDateTime.parse(timestamp.replace("Z", "")).plusMinutes(duration.longValue()));
            activityLog.setNotes("Added via API");

            // Save the activity log
            ActivityLog savedLog = activityLogRepository.save(activityLog);

            // Return in frontend format with data wrapper
            Map<String, Object> data = new HashMap<>();
            data.put("id", savedLog.getId().toString());
            data.put("userId", savedLog.getUser().getId().toString());
            data.put("activityName", savedLog.getActivity().getName());
            data.put("duration", savedLog.getDuration());
            data.put("caloriesBurned", savedLog.getCaloriesBurned());
            data.put("date", savedLog.getStartTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            data.put("timestamp", savedLog.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME));

            Map<String, Object> response = new HashMap<>();
            response.put("data", data);
            response.put("message", "Activity logged successfully");

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 