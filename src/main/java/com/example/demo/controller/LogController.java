package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class LogController {
    @Autowired
    private WeightLogService weightLogService;

    @Autowired
    private WaterLogService waterLogService;

    @Autowired
    private StepsLogService stepsLogService;

    // Weight Logs
    @GetMapping("/{userId}/weight-logs")
    public ResponseEntity<Map<String, List<WeightLog>>> getWeightLogs(@PathVariable Long userId) {
        List<WeightLog> logs = weightLogService.getWeightLogsByUserId(userId);
        return ResponseEntity.ok(Map.of("data", logs));
    }

    // New endpoint for /api/users/weightLogs?userId=...
    @GetMapping("/weightLogs")
    public ResponseEntity<Map<String, List<WeightLog>>> getWeightLogsByQueryParam(@RequestParam Long userId) {
        List<WeightLog> logs = weightLogService.getWeightLogsByUserId(userId);
        return ResponseEntity.ok(Map.of("data", logs));
    }

    @PostMapping("/{userId}/weight-logs")
    public ResponseEntity<WeightLog> addWeightLog(@PathVariable Long userId, @RequestBody WeightLog weightLog) {
        System.out.println("Received weight log: " + weightLog);
        System.out.println("weightLog.weight = " + weightLog.getWeight());
        System.out.println("weightLog.targetWeight = " + weightLog.getTargetWeight());
        System.out.println("weightLog.timestamp = " + weightLog.getTimestamp());
        System.out.println("weightLog.unit = " + weightLog.getUnit());
        weightLog.setUserId(userId);
        return ResponseEntity.ok(weightLogService.addWeightLog(weightLog));
    }

    // Water Logs
    @GetMapping("/{userId}/water-logs")
    public ResponseEntity<Map<String, List<WaterLog>>> getWaterLogs(@PathVariable Long userId) {
        List<WaterLog> logs = waterLogService.getWaterLogsByUserId(userId);
        return ResponseEntity.ok(Map.of("data", logs));
    }

    @PostMapping("/{userId}/water-logs")
    public ResponseEntity<WaterLog> addWaterLog(@PathVariable Long userId, @RequestBody WaterLog waterLog) {
        waterLog.setUserId(userId);
        return ResponseEntity.ok(waterLogService.addWaterLog(waterLog));
    }

    @PostMapping("/{userId}/water-logs/reset")
    public ResponseEntity<Void> resetWaterLog(@PathVariable Long userId) {
        waterLogService.resetWaterLog(userId);
        return ResponseEntity.ok().build();
    }

    // Steps Logs
    @GetMapping("/{userId}/steps-logs")
    public ResponseEntity<Map<String, List<StepsLog>>> getStepsLogs(@PathVariable Long userId) {
        List<StepsLog> logs = stepsLogService.getStepsLogsByUserId(userId);
        return ResponseEntity.ok(Map.of("data", logs));
    }

    @PostMapping("/{userId}/steps-logs")
    public ResponseEntity<StepsLog> addStepsLog(@PathVariable Long userId, @RequestBody StepsLog stepsLog) {
        stepsLog.setUserId(userId);
        return ResponseEntity.ok(stepsLogService.addStepsLog(stepsLog));
    }

    @PostMapping("/{userId}/steps-logs/reset")
    public ResponseEntity<Void> resetStepsLog(@PathVariable Long userId) {
        stepsLogService.resetStepsLog(userId);
        return ResponseEntity.ok().build();
    }
} 