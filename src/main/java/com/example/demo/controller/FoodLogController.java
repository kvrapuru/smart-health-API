package com.example.demo.controller;

import com.example.demo.model.FoodLog;
import com.example.demo.model.User;
import com.example.demo.repository.FoodLogRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/food-logs")
public class FoodLogController {

    @Autowired
    private FoodLogRepository foodLogRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<FoodLog> getAllFoodLogs() {
        return foodLogRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodLog> getFoodLogById(@PathVariable Long id) {
        return foodLogRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FoodLog>> getFoodLogsByUser(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(foodLogRepository.findByUser(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/between")
    public ResponseEntity<List<FoodLog>> getFoodLogsByUserAndDateRange(
            @PathVariable Long userId,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(foodLogRepository.findByUserAndTimestampBetween(user, start, end)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FoodLog> createFoodLog(@RequestBody FoodLog foodLog) {
        if (foodLog.getUser() == null || foodLog.getUser().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return userRepository.findById(foodLog.getUser().getId())
                .map(user -> {
                    foodLog.setUser(user);
                    return ResponseEntity.ok(foodLogRepository.save(foodLog));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodLog> updateFoodLog(@PathVariable Long id, @RequestBody FoodLog foodLogDetails) {
        return foodLogRepository.findById(id)
                .map(existingFoodLog -> {
                    existingFoodLog.setFood(foodLogDetails.getFood());
                    existingFoodLog.setTimestamp(foodLogDetails.getTimestamp());
                    existingFoodLog.setQuantity(foodLogDetails.getQuantity());
                    existingFoodLog.setMealType(foodLogDetails.getMealType());
                    existingFoodLog.setNotes(foodLogDetails.getNotes());
                    return ResponseEntity.ok(foodLogRepository.save(existingFoodLog));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFoodLog(@PathVariable Long id) {
        return foodLogRepository.findById(id)
                .map(foodLog -> {
                    foodLogRepository.delete(foodLog);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 