package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.*;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "Legacy Logs", description = "Legacy logging endpoints for weight, water, and steps")
public class LogController {
    @Autowired
    private WeightLogService weightLogService;

    @Autowired
    private WaterLogService waterLogService;

    @Autowired
    private StepsLogService stepsLogService;

    // Weight Logs
    @Operation(summary = "Get weight logs by user ID", description = "Retrieves all weight logs for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved weight logs",
            content = @Content(schema = @Schema(implementation = WeightLog.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/{userId}/weight-logs")
    public ResponseEntity<Map<String, List<WeightLog>>> getWeightLogs(
        @Parameter(description = "User ID", required = true)
        @PathVariable Long userId) {
        List<WeightLog> logs = weightLogService.getWeightLogsByUserId(userId);
        return ResponseEntity.ok(Map.of("data", logs));
    }

    @Operation(summary = "Add weight log", description = "Creates a new weight log entry for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created weight log",
            content = @Content(schema = @Schema(implementation = WeightLog.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid weight log data")
    })
    @PostMapping("/{userId}/weight-logs")
    public ResponseEntity<WeightLog> addWeightLog(
        @Parameter(description = "User ID", required = true)
        @PathVariable Long userId, 
        @RequestBody WeightLog weightLog) {
        System.out.println("Received weight log: " + weightLog);
        System.out.println("weightLog.weight = " + weightLog.getWeight());
        System.out.println("weightLog.targetWeight = " + weightLog.getTargetWeight());
        System.out.println("weightLog.timestamp = " + weightLog.getTimestamp());
        System.out.println("weightLog.unit = " + weightLog.getUnit());
        weightLog.setUserId(userId);
        return ResponseEntity.ok(weightLogService.addWeightLog(weightLog));
    }

    // Water Logs
    @Operation(summary = "Get water logs by user ID", description = "Retrieves all water logs for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved water logs",
            content = @Content(schema = @Schema(implementation = WaterLog.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/{userId}/water-logs")
    public ResponseEntity<Map<String, List<WaterLog>>> getWaterLogs(
        @Parameter(description = "User ID", required = true)
        @PathVariable Long userId) {
        List<WaterLog> logs = waterLogService.getWaterLogsByUserId(userId);
        return ResponseEntity.ok(Map.of("data", logs));
    }

    @Operation(summary = "Add water log", description = "Creates a new water log entry for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created water log",
            content = @Content(schema = @Schema(implementation = WaterLog.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid water log data")
    })
    @PostMapping("/{userId}/water-logs")
    public ResponseEntity<WaterLog> addWaterLog(
        @Parameter(description = "User ID", required = true)
        @PathVariable Long userId, 
        @RequestBody WaterLog waterLog) {
        waterLog.setUserId(userId);
        return ResponseEntity.ok(waterLogService.addWaterLog(waterLog));
    }

    @Operation(summary = "Reset water log", description = "Resets water log for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully reset water log"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @PostMapping("/{userId}/water-logs/reset")
    public ResponseEntity<Void> resetWaterLog(
        @Parameter(description = "User ID", required = true)
        @PathVariable Long userId) {
        waterLogService.resetWaterLog(userId);
        return ResponseEntity.ok().build();
    }

    // Steps Logs
    @Operation(summary = "Get steps logs by user ID", description = "Retrieves all steps logs for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved steps logs",
            content = @Content(schema = @Schema(implementation = StepsLog.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @GetMapping("/{userId}/steps-logs")
    public ResponseEntity<Map<String, List<StepsLog>>> getStepsLogs(
        @Parameter(description = "User ID", required = true)
        @PathVariable Long userId) {
        List<StepsLog> logs = stepsLogService.getStepsLogsByUserId(userId);
        return ResponseEntity.ok(Map.of("data", logs));
    }

    @Operation(summary = "Add steps log", description = "Creates a new steps log entry for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created steps log",
            content = @Content(schema = @Schema(implementation = StepsLog.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "400", description = "Invalid steps log data")
    })
    @PostMapping("/{userId}/steps-logs")
    public ResponseEntity<StepsLog> addStepsLog(
        @Parameter(description = "User ID", required = true)
        @PathVariable Long userId, 
        @RequestBody StepsLog stepsLog) {
        stepsLog.setUserId(userId);
        return ResponseEntity.ok(stepsLogService.addStepsLog(stepsLog));
    }

    @Operation(summary = "Reset steps log", description = "Resets steps log for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully reset steps log"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @PostMapping("/{userId}/steps-logs/reset")
    public ResponseEntity<Void> resetStepsLog(
        @Parameter(description = "User ID", required = true)
        @PathVariable Long userId) {
        stepsLogService.resetStepsLog(userId);
        return ResponseEntity.ok().build();
    }
} 