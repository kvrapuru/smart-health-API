package com.example.demo.controller;

import com.example.demo.model.FoodItem;
import com.example.demo.service.GeminiNlpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Food Entry", description = "Food intake tracking and nutritional analysis endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class FoodEntryController {

    @Autowired
    private GeminiNlpService nlpService;

    @Operation(summary = "Test endpoint", description = "Simple test endpoint to verify the food intake backend is running")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Backend is running",
            content = @Content(schema = @Schema(implementation = String.class), 
                examples = @ExampleObject(value = "\"Food Intake Backend is running!\""))),
        @ApiResponse(responseCode = "403", description = "Access denied - authentication required")
    })
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Food Intake Backend is running!");
    }

    @Operation(summary = "Get mock food items", description = "Returns sample food items for testing purposes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mock food items retrieved successfully",
            content = @Content(schema = @Schema(implementation = FoodItem.class, type = "array"))),
        @ApiResponse(responseCode = "403", description = "Access denied - authentication required")
    })
    @GetMapping("/test-mock")
    public ResponseEntity<List<FoodItem>> testMock() {
        List<FoodItem> items = new ArrayList<>();
        FoodItem item1 = new FoodItem();
        item1.setName("banana");
        item1.setQuantity(2);
        item1.setUnit("piece");
        item1.setCalories(210);
        item1.setProtein(2.6);
        item1.setCarbohydrates(54);
        item1.setFat(0.8);
        item1.setFiber(6.2);
        item1.setSugar(28);
        item1.setSodium(2);
        items.add(item1);
        
        FoodItem item2 = new FoodItem();
        item2.setName("chicken rice");
        item2.setQuantity(200);
        item2.setUnit("grams");
        item2.setCalories(260);
        item2.setProtein(8);
        item2.setCarbohydrates(45);
        item2.setFat(4);
        item2.setFiber(2);
        item2.setSugar(1);
        item2.setSodium(400);
        items.add(item2);
        
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Debug API key", description = "Check if the Gemini API key is properly configured")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API key status retrieved",
            content = @Content(schema = @Schema(implementation = String.class),
                examples = @ExampleObject(value = "\"API Key configured: Yes\""))),
        @ApiResponse(responseCode = "403", description = "Access denied - authentication required")
    })
    @GetMapping("/debug-api-key")
    public ResponseEntity<String> debugApiKey() {
        return ResponseEntity.ok("API Key configured: " + (nlpService.getApiKey() != null ? "Yes" : "No"));
    }

    @Operation(summary = "Test Gemini API connection", description = "Test the connection to the Gemini AI API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Gemini API test completed",
            content = @Content(schema = @Schema(implementation = String.class),
                examples = @ExampleObject(value = "\"Gemini API Test: Success: Hello\""))),
        @ApiResponse(responseCode = "403", description = "Access denied - authentication required")
    })
    @GetMapping("/test-gemini")
    public ResponseEntity<String> testGemini() {
        try {
            String result = nlpService.testGeminiConnection();
            return ResponseEntity.ok("Gemini API Test: " + result);
        } catch (Exception e) {
            return ResponseEntity.ok("Gemini API Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Get raw Gemini response", description = "Get the raw response from Gemini AI for debugging purposes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Raw Gemini response retrieved",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "403", description = "Access denied - authentication required")
    })
    @GetMapping("/debug-gemini-response")
    public ResponseEntity<String> debugGeminiResponse() {
        try {
            String result = nlpService.getRawGeminiResponse("I ate 1 apple");
            return ResponseEntity.ok("Raw Gemini Response: " + result);
        } catch (Exception e) {
            return ResponseEntity.ok("Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Parse food entry", description = "Parse natural language food input and extract nutritional information using AI")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Food items parsed successfully",
            content = @Content(schema = @Schema(implementation = FoodItem.class, type = "array"),
                examples = @ExampleObject(value = "[{\"name\": \"banana\", \"quantity\": 2.0, \"unit\": \"pieces\", \"calories\": 210.0, \"protein\": 2.6, \"carbohydrates\": 54.0, \"fat\": 0.8, \"fiber\": 6.2, \"sugar\": 28.0, \"sodium\": 2.0}]"))),
        @ApiResponse(responseCode = "400", description = "Invalid input or parsing error"),
        @ApiResponse(responseCode = "403", description = "Access denied - authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error or AI service unavailable")
    })
    @PostMapping("/food-entry")
    public ResponseEntity<?> parseFoodEntry(
        @Parameter(description = "Food input request", required = true,
            content = @Content(schema = @Schema(implementation = FoodEntryRequest.class),
                examples = @ExampleObject(value = "{\"input\": \"I ate 2 bananas and 200 grams of chicken rice\"}")))
        @RequestBody Map<String, String> request) {
        String input = request.get("input");
        List<FoodItem> foodItems = nlpService.parseInputWithGemini(input);
        return ResponseEntity.ok(foodItems);
    }

    // Schema for request documentation
    @Schema(description = "Food entry request")
    public static class FoodEntryRequest {
        @Schema(description = "Natural language description of food consumed", 
                example = "I ate 2 bananas and 200 grams of chicken rice", required = true)
        private String input;

        public String getInput() { return input; }
        public void setInput(String input) { this.input = input; }
    }
}
