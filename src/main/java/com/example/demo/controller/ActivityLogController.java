package com.example.demo.controller;

import com.example.demo.model.ActivityLog;
import com.example.demo.model.User;
import com.example.demo.repository.ActivityLogRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
@CrossOrigin(origins = "*")
@Tag(name = "Activity Logs (Legacy)", description = "Legacy activity logging APIs")
public class ActivityLogController {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Get all activity logs", description = "Retrieves all activity logs from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all activity logs",
            content = @Content(schema = @Schema(implementation = ActivityLog.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping
    public List<ActivityLog> getAllActivityLogs() {
        return activityLogRepository.findAll();
    }

    @Operation(summary = "Get activity log by ID", description = "Retrieves a specific activity log by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved activity log",
            content = @Content(schema = @Schema(implementation = ActivityLog.class))),
        @ApiResponse(responseCode = "404", description = "Activity log not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ActivityLog> getActivityLogById(
        @Parameter(description = "Activity log ID", required = true)
        @PathVariable Long id) {
        return activityLogRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get activity logs by user", description = "Retrieves all activity logs for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved activity logs",
            content = @Content(schema = @Schema(implementation = ActivityLog.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityLog>> getActivityLogsByUser(
        @Parameter(description = "User ID", required = true)
        @PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(activityLogRepository.findByUser(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get activity logs by user and date range", description = "Retrieves activity logs for a user within a specific date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved activity logs",
            content = @Content(schema = @Schema(implementation = ActivityLog.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/user/{userId}/between")
    public ResponseEntity<List<ActivityLog>> getActivityLogsByUserAndDateRange(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Start date and time", required = true)
            @RequestParam LocalDateTime start,
            @Parameter(description = "End date and time", required = true)
            @RequestParam LocalDateTime end) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(activityLogRepository.findByUserAndStartTimeBetween(user, start, end)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create activity log", description = "Creates a new activity log entry")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created activity log",
            content = @Content(schema = @Schema(implementation = ActivityLog.class))),
        @ApiResponse(responseCode = "400", description = "Invalid activity log data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
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

    @Operation(summary = "Update activity log", description = "Updates an existing activity log entry")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated activity log",
            content = @Content(schema = @Schema(implementation = ActivityLog.class))),
        @ApiResponse(responseCode = "404", description = "Activity log not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid activity log data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ActivityLog> updateActivityLog(
        @Parameter(description = "Activity log ID", required = true)
        @PathVariable Long id, 
        @RequestBody ActivityLog activityLogDetails) {
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

    @Operation(summary = "Delete activity log", description = "Deletes an activity log entry")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted activity log"),
        @ApiResponse(responseCode = "404", description = "Activity log not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivityLog(
        @Parameter(description = "Activity log ID", required = true)
        @PathVariable Long id) {
        return activityLogRepository.findById(id)
                .map(activityLog -> {
                    activityLogRepository.delete(activityLog);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 