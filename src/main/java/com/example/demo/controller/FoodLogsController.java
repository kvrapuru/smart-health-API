package com.example.demo.controller;

import com.example.demo.model.FoodLog;
import com.example.demo.model.Food;
import com.example.demo.model.User;
import com.example.demo.repository.FoodLogRepository;
import com.example.demo.repository.FoodRepository;
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
public class FoodLogsController {

    @Autowired
    private FoodLogRepository foodLogRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private UserRepository userRepository;

    // Endpoint to match frontend expectation: GET /api/foodLogs?userId=1&date=2025-06-18
    @GetMapping("/foodLogs")
    public ResponseEntity<List<Map<String, Object>>> getFoodLogs(
            @RequestParam Long userId,
            @RequestParam(required = false) String date) {
        
        try {
            // Find the user
            User user = userRepository.findById(userId)
                    .orElse(null);
            
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            List<FoodLog> foodLogs;
            
            if (date != null && !date.isEmpty()) {
                // Filter by specific date
                LocalDate targetDate = LocalDate.parse(date);
                LocalDateTime startOfDay = targetDate.atStartOfDay();
                LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);
                
                foodLogs = foodLogRepository.findByUserAndTimestampBetween(user, startOfDay, endOfDay);
            } else {
                // Get all food logs for the user
                foodLogs = foodLogRepository.findByUser(user);
            }

            // Convert to frontend response format
            List<Map<String, Object>> response = foodLogs.stream()
                    .map(log -> {
                        Map<String, Object> foodData = new HashMap<>();
                        foodData.put("id", log.getId().toString());
                        foodData.put("userId", log.getUser().getId().toString());
                        foodData.put("foodId", log.getFood() != null ? log.getFood().getId().toString() : null);
                        foodData.put("foodName", log.getFood() != null ? log.getFood().getName() : "Unknown");
                        foodData.put("quantity", log.getQuantity());
                        foodData.put("calories", log.getFood() != null ? log.getFood().getCalories() * log.getQuantity() / 100 : 0);
                        foodData.put("protein", log.getFood() != null ? log.getFood().getProtein() * log.getQuantity() / 100 : 0);
                        foodData.put("carbs", log.getFood() != null ? log.getFood().getCarbs() * log.getQuantity() / 100 : 0);
                        foodData.put("fat", log.getFood() != null ? log.getFood().getFat() * log.getQuantity() / 100 : 0);
                        foodData.put("fiber", 0.0); // Default value since Food model doesn't have fiber
                        foodData.put("sodium", 0.0); // Default value since Food model doesn't have sodium
                        foodData.put("sugar", 0.0); // Default value since Food model doesn't have sugar
                        foodData.put("mealType", log.getMealType());
                        foodData.put("date", log.getTimestamp().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                        foodData.put("timestamp", log.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));
                        foodData.put("createdAt", log.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));
                        foodData.put("updatedAt", log.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));
                        return foodData;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // POST endpoint for adding food logs
    @PostMapping("/foodLogs")
    public ResponseEntity<Map<String, Object>> addFoodLog(@RequestBody Map<String, Object> foodData) {
        try {
            // Extract data from request
            Long userId = Long.parseLong(foodData.get("userId").toString());
            Long foodId = Long.parseLong(foodData.get("foodId").toString());
            String mealType = foodData.get("mealType").toString();
            Double quantity = Double.parseDouble(foodData.get("quantity").toString());
            String date = foodData.get("date").toString();

            // Find the user
            User user = userRepository.findById(userId)
                    .orElse(null);
            
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // Find the food
            Food food = foodRepository.findById(foodId)
                    .orElse(null);
            
            if (food == null) {
                return ResponseEntity.notFound().build();
            }

            // Create timestamp from date
            LocalDateTime timestamp = LocalDate.parse(date).atStartOfDay().plusHours(10).plusMinutes(30); // Default to 10:30 AM

            // Create new food log
            FoodLog foodLog = new FoodLog();
            foodLog.setUser(user);
            foodLog.setFood(food);
            foodLog.setQuantity(quantity);
            foodLog.setMealType(mealType);
            foodLog.setTimestamp(timestamp);
            foodLog.setNotes("Added via API");

            // Save the food log
            FoodLog savedLog = foodLogRepository.save(foodLog);

            // Calculate nutritional values based on quantity (assuming per 100g)
            Double calories = food.getCalories() * quantity / 100;
            Double protein = food.getProtein() * quantity / 100;
            Double carbs = food.getCarbs() * quantity / 100;
            Double fat = food.getFat() * quantity / 100;

            // Return in frontend format with data wrapper
            Map<String, Object> data = new HashMap<>();
            data.put("id", savedLog.getId());
            data.put("userId", savedLog.getUser().getId());
            data.put("foodId", savedLog.getFood().getId());
            data.put("foodName", savedLog.getFood().getName());
            data.put("date", savedLog.getTimestamp().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            data.put("mealType", savedLog.getMealType());
            data.put("quantity", savedLog.getQuantity());
            data.put("calories", calories);
            data.put("protein", protein);
            data.put("carbs", carbs);
            data.put("fat", fat);
            data.put("fiber", 0.0); // Default value
            data.put("sodium", 0.0); // Default value
            data.put("sugar", 0.0); // Default value
            data.put("timestamp", savedLog.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));
            data.put("createdAt", savedLog.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));
            data.put("updatedAt", savedLog.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));

            Map<String, Object> response = new HashMap<>();
            response.put("data", data);

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 