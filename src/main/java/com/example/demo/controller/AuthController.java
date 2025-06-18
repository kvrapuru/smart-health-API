package com.example.demo.controller;

import com.example.demo.dto.ErrorResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.example.demo.dto.LoginRequest;
import com.example.demo.service.JwtService;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.dto.UserResponse;
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "User registration, login, and authentication endpoints")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided username and name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or username already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            logger.info("Received registration request for username: {}", request.getUsername());
            User user = authService.register(request);
            String token = jwtService.generateToken(user);
            UserResponse userResponse = UserResponse.fromUser(user);
            LoginResponse response = new LoginResponse(token, userResponse, "Registration successful");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Registration failed for username: {}", request.getUsername(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Registration failed", e.getMessage()));
        }
    }

    @Operation(summary = "Login user", description = "Authenticates user with username and returns JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User authenticated successfully",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            logger.info("=== Login Request Debug ===");
            logger.info("Received login request");
            logger.info("LoginRequest object: {}", loginRequest);
            logger.info("Username from request: {}", loginRequest != null ? loginRequest.getUsername() : "null");
            
            if (loginRequest == null) {
                logger.error("LoginRequest is null");
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid request", "Request body is null"));
            }
            
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                logger.error("Username is null or empty");
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid request", "Username is required"));
            }
            
            User user = authService.login(loginRequest.getUsername());
            String token = jwtService.generateToken(user);
            logger.info("Login successful for user: {}", user.getUsername());
            
            // Create response with nested user data
            UserResponse userResponse = UserResponse.fromUser(user);
            LoginResponse response = new LoginResponse(token, userResponse, "Login successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login failed for username: {}", loginRequest != null ? loginRequest.getUsername() : "null", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Login failed", e.getMessage()));
        }
    }

    // Alias endpoint for /api/users/login to support client expectation
    @Operation(summary = "Login user (alias)", description = "Alias endpoint for login - same as /api/users/login")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User authenticated successfully",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(path = "/api/users/login")
    public ResponseEntity<?> loginAlias(@RequestBody LoginRequest request) {
        return login(request);
    }

    @Operation(summary = "Get current user", description = "Returns the current authenticated user's profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Current user retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            User user = userService.getCurrentUser();
            UserResponse response = UserResponse.fromUser(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get current user", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to get user", e.getMessage()));
        }
    }

    @Operation(summary = "Get user by ID", description = "Returns a specific user's profile by ID (user can only access their own data or admin can access any)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied - can only access own data"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User currentUser = userService.getCurrentUser();
            
            // Security check: user can only access their own data or admin can access any data
            if (!currentUser.getId().equals(id)) {
                // Check if current user has admin role
                boolean isAdmin = currentUser.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
                
                if (!isAdmin) {
                    logger.warn("User {} attempted to access data for user {}", 
                        currentUser.getUsername(), id);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Access denied", "You can only access your own data"));
                }
            }
            
            Optional<User> userOptional = userService.findById(id);
            if (userOptional.isEmpty()) {
                logger.warn("User not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            User user = userOptional.get();
            UserResponse response = UserResponse.fromUser(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to get user with ID: {}", id, e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to get user", e.getMessage()));
        }
    }

    @Operation(summary = "Update user", description = "Updates a user's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest userUpdate) {
        try {
            logger.info("=== PATCH Request Debug Info ===");
            logger.info("Received PATCH request for user ID: {}", id);
            
            // Log raw request body
            logger.info("Raw request body - userUpdate object: {}", userUpdate);
            logger.info("userUpdate class type: {}", userUpdate.getClass().getName());
            
            // Log all fields using reflection
            logger.info("=== Field-by-Field Debug ===");
            for (java.lang.reflect.Field field : userUpdate.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object value = field.get(userUpdate);
                    logger.info("Field: {} = {} (type: {})", 
                        field.getName(), 
                        value, 
                        value != null ? value.getClass().getName() : "null");
                } catch (Exception e) {
                    logger.error("Error accessing field {}: {}", field.getName(), e.getMessage());
                }
            }
            
            // Check if userUpdate is null or empty
            if (userUpdate == null) {
                logger.error("userUpdate object is null");
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid request", "Request body is null"));
            }
            
            // Check if all fields are null
            boolean allFieldsNull = true;
            for (java.lang.reflect.Field field : userUpdate.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    if (field.get(userUpdate) != null) {
                        allFieldsNull = false;
                        break;
                    }
                } catch (Exception e) {
                    logger.error("Error checking field {}: {}", field.getName(), e.getMessage());
                }
            }
            
            if (allFieldsNull) {
                logger.error("All fields in userUpdate are null");
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid request", "No fields to update"));
            }
            
            User existingUser = userService.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new RuntimeException("User not found");
                });
            
            logger.info("=== Existing User Fields ===");
            for (java.lang.reflect.Field field : existingUser.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object value = field.get(existingUser);
                    logger.info("Field: {} = {} (type: {})", 
                        field.getName(), 
                        value, 
                        value != null ? value.getClass().getName() : "null");
                } catch (Exception e) {
                    logger.error("Error accessing field {}: {}", field.getName(), e.getMessage());
                }
            }
            
            // Update only non-null fields
            if (userUpdate.getName() != null) {
                logger.info("Updating name from '{}' to '{}'", existingUser.getName(), userUpdate.getName());
                existingUser.setName(userUpdate.getName());
            }
            if (userUpdate.getGender() != null) {
                logger.info("Updating gender from '{}' to '{}'", existingUser.getGender(), userUpdate.getGender());
                existingUser.setGender(userUpdate.getGender());
            }
            if (userUpdate.getHeight() != null) {
                logger.info("Updating height from '{}' to '{}'", existingUser.getHeight(), userUpdate.getHeight());
                existingUser.setHeight(userUpdate.getHeight());
            }
            if (userUpdate.getWeight() != null) {
                logger.info("Updating weight from '{}' to '{}'", existingUser.getWeight(), userUpdate.getWeight());
                existingUser.setWeight(userUpdate.getWeight());
            }
            if (userUpdate.getHeightUnit() != null) {
                logger.info("Updating heightUnit from '{}' to '{}'", existingUser.getHeightUnit(), userUpdate.getHeightUnit());
                existingUser.setHeightUnit(userUpdate.getHeightUnit());
            }
            if (userUpdate.getWeightUnit() != null) {
                logger.info("Updating weightUnit from '{}' to '{}'", existingUser.getWeightUnit(), userUpdate.getWeightUnit());
                existingUser.setWeightUnit(userUpdate.getWeightUnit());
            }
            if (userUpdate.getDateOfBirth() != null) {
                logger.info("Updating dateOfBirth from '{}' to '{}'", existingUser.getDateOfBirth(), userUpdate.getDateOfBirth());
                // Convert LocalDate to LocalDateTime at start of day
                LocalDateTime dateTime = userUpdate.getDateOfBirth().atStartOfDay();
                existingUser.setDateOfBirth(dateTime);
            }
            if (userUpdate.getActivityLevel() != null) {
                logger.info("Updating activityLevel from '{}' to '{}'", existingUser.getActivityLevel(), userUpdate.getActivityLevel());
                existingUser.setActivityLevel(userUpdate.getActivityLevel());
            }
            if (userUpdate.getProfilePicture() != null) {
                logger.info("Updating profilePicture from '{}' to '{}'", existingUser.getProfilePicture(), userUpdate.getProfilePicture());
                existingUser.setProfilePicture(userUpdate.getProfilePicture());
            }

            // Health Details
            if (userUpdate.getFitnessGoals() != null) {
                try {
                    String fitnessGoalsJson = objectMapper.writeValueAsString(userUpdate.getFitnessGoals());
                    logger.info("Updating fitnessGoals from '{}' to '{}'", existingUser.getFitnessGoals(), fitnessGoalsJson);
                    existingUser.setFitnessGoals(fitnessGoalsJson);
                } catch (Exception e) {
                    logger.error("Failed to convert fitnessGoals to JSON: {}", e.getMessage());
                    return ResponseEntity.badRequest().body(new ErrorResponse("Invalid fitnessGoals format", "Failed to process fitness goals"));
                }
            }
            if (userUpdate.getMedicalConditions() != null) {
                logger.info("Updating medicalConditions from '{}' to '{}'", existingUser.getMedicalConditions(), userUpdate.getMedicalConditions());
                existingUser.setMedicalConditions(userUpdate.getMedicalConditions());
            }
            if (userUpdate.getAllergies() != null) {
                logger.info("Updating allergies from '{}' to '{}'", existingUser.getAllergies(), userUpdate.getAllergies());
                existingUser.setAllergies(userUpdate.getAllergies());
            }
            if (userUpdate.getMedications() != null) {
                logger.info("Updating medications from '{}' to '{}'", existingUser.getMedications(), userUpdate.getMedications());
                existingUser.setMedications(userUpdate.getMedications());
            }

            // Goals
            if (userUpdate.getCalorieGoal() != null) {
                logger.info("Updating calorieGoal from '{}' to '{}'", existingUser.getCalorieGoal(), userUpdate.getCalorieGoal());
                existingUser.setCalorieGoal(userUpdate.getCalorieGoal());
            }
            if (userUpdate.getWaterGoal() != null) {
                logger.info("Updating waterGoal from '{}' to '{}'", existingUser.getWaterGoal(), userUpdate.getWaterGoal());
                existingUser.setWaterGoal(userUpdate.getWaterGoal());
            }
            if (userUpdate.getStepsGoal() != null) {
                logger.info("Updating stepsGoal from '{}' to '{}'", existingUser.getStepsGoal(), userUpdate.getStepsGoal());
                existingUser.setStepsGoal(userUpdate.getStepsGoal());
            }
            if (userUpdate.getExerciseGoal() != null) {
                logger.info("Updating exerciseGoal from '{}' to '{}'", existingUser.getExerciseGoal(), userUpdate.getExerciseGoal());
                existingUser.setExerciseGoal(userUpdate.getExerciseGoal());
            }
            if (userUpdate.getWeightGoal() != null) {
                logger.info("Updating weightGoal from '{}' to '{}'", existingUser.getWeightGoal(), userUpdate.getWeightGoal());
                existingUser.setWeightGoal(userUpdate.getWeightGoal());
            }
            if (userUpdate.getSleepGoal() != null) {
                logger.info("Updating sleepGoal from '{}' to '{}'", existingUser.getSleepGoal(), userUpdate.getSleepGoal());
                existingUser.setSleepGoal(userUpdate.getSleepGoal());
            }
            if (userUpdate.getWeeklyWorkouts() != null) {
                logger.info("Updating weeklyWorkouts from '{}' to '{}'", existingUser.getWeeklyWorkouts(), userUpdate.getWeeklyWorkouts());
                existingUser.setWeeklyWorkouts(userUpdate.getWeeklyWorkouts());
            }
            if (userUpdate.getProteinGoal() != null) {
                logger.info("Updating proteinGoal from '{}' to '{}'", existingUser.getProteinGoal(), userUpdate.getProteinGoal());
                existingUser.setProteinGoal(userUpdate.getProteinGoal());
            }
            if (userUpdate.getCarbsGoal() != null) {
                logger.info("Updating carbsGoal from '{}' to '{}'", existingUser.getCarbsGoal(), userUpdate.getCarbsGoal());
                existingUser.setCarbsGoal(userUpdate.getCarbsGoal());
            }
            if (userUpdate.getFatGoal() != null) {
                logger.info("Updating fatGoal from '{}' to '{}'", existingUser.getFatGoal(), userUpdate.getFatGoal());
                existingUser.setFatGoal(userUpdate.getFatGoal());
            }
            if (userUpdate.getFiberGoal() != null) {
                logger.info("Updating fiberGoal from '{}' to '{}'", existingUser.getFiberGoal(), userUpdate.getFiberGoal());
                existingUser.setFiberGoal(userUpdate.getFiberGoal());
            }
            if (userUpdate.getSodiumLimit() != null) {
                logger.info("Updating sodiumLimit from '{}' to '{}'", existingUser.getSodiumLimit(), userUpdate.getSodiumLimit());
                existingUser.setSodiumLimit(userUpdate.getSodiumLimit());
            }
            if (userUpdate.getSugarLimit() != null) {
                logger.info("Updating sugarLimit from '{}' to '{}'", existingUser.getSugarLimit(), userUpdate.getSugarLimit());
                existingUser.setSugarLimit(userUpdate.getSugarLimit());
            }
            
            logger.info("=== Final Updated User Fields ===");
            for (java.lang.reflect.Field field : existingUser.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object value = field.get(existingUser);
                    logger.info("Field: {} = {} (type: {})", 
                        field.getName(), 
                        value, 
                        value != null ? value.getClass().getName() : "null");
                } catch (Exception e) {
                    logger.error("Error accessing field {}: {}", field.getName(), e.getMessage());
                }
            }
            
            User updatedUser = userService.updateUser(existingUser);
            logger.info("Successfully saved user: {}", updatedUser);
            
            // Convert to UserResponse and return
            UserResponse response = UserResponse.fromUser(updatedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to update user with id: {}", id, e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Update failed", e.getMessage()));
        }
    }
} 