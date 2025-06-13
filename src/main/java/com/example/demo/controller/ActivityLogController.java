package com.example.demo.controller;

import com.example.demo.model.ActivityLog;
import com.example.demo.model.User;
import com.example.demo.repository.ActivityLogRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<ActivityLog> getAllActivityLogs() {
        return activityLogRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityLog> getActivityLogById(@PathVariable Long id) {
        return activityLogRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityLog>> getActivityLogsByUser(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(activityLogRepository.findByUser(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/between")
    public ResponseEntity<List<ActivityLog>> getActivityLogsByUserAndDateRange(
            @PathVariable Long userId,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(activityLogRepository.findByUserAndStartTimeBetween(user, start, end)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ActivityLog> createActivityLog(@RequestBody ActivityLog activityLog) {
        if (activityLog.getUser() == null || activityLog.getUser().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return userRepository.findById(activityLog.getUser().getId())
                .map(user -> {
                    activityLog.setUser(user);
                    return ResponseEntity.ok(activityLogRepository.save(activityLog));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityLog> updateActivityLog(@PathVariable Long id, @RequestBody ActivityLog activityLogDetails) {
        return activityLogRepository.findById(id)
                .map(existingActivityLog -> {
                    existingActivityLog.setActivity(activityLogDetails.getActivity());
                    existingActivityLog.setStartTime(activityLogDetails.getStartTime());
                    existingActivityLog.setEndTime(activityLogDetails.getEndTime());
                    existingActivityLog.setDuration(activityLogDetails.getDuration());
                    existingActivityLog.setCaloriesBurned(activityLogDetails.getCaloriesBurned());
                    existingActivityLog.setNotes(activityLogDetails.getNotes());
                    return ResponseEntity.ok(activityLogRepository.save(existingActivityLog));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivityLog(@PathVariable Long id) {
        return activityLogRepository.findById(id)
                .map(activityLog -> {
                    activityLogRepository.delete(activityLog);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 