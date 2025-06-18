package com.example.demo.controller;

import com.example.demo.model.CycleData;
import com.example.demo.model.User;
import com.example.demo.repository.CycleDataRepository;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserService;
import com.example.demo.dto.ErrorResponse;
import com.example.demo.dto.CycleDataRequest;
import com.example.demo.dto.CycleDataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/cycle-data")
@CrossOrigin(origins = "*")
@Tag(name = "Cycle Tracker", description = "Menstrual cycle tracking APIs")
public class CycleDataController {

    private static final Logger logger = LoggerFactory.getLogger(CycleDataController.class);

    @Autowired
    private CycleDataRepository cycleDataRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Operation(summary = "Create new cycle data", description = "Creates a new cycle data record for the authenticated user - accepts frontend format with lastPeriodDate, cycleLength, periodLength")
    @PostMapping
    public ResponseEntity<?> createCycleData(@RequestBody String requestBody) {
        try {
            logger.info("=== CREATE CYCLE DATA REQUEST ===");
            logger.info("Received raw request body: {}", requestBody);
            
            CycleDataRequest cycleDataRequest = null;
            
            // Try to parse the request body
            try {
                JsonNode jsonNode = objectMapper.readTree(requestBody);
                logger.info("Successfully parsed JSON: {}", jsonNode);
                
                // Parse as CycleDataRequest (frontend format)
                cycleDataRequest = objectMapper.treeToValue(jsonNode, CycleDataRequest.class);
                logger.info("Parsed as CycleDataRequest: {}", cycleDataRequest);
                
            } catch (Exception e) {
                logger.error("Failed to parse JSON: {}", e.getMessage());
                logger.error("JSON parsing exception details:", e);
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid JSON format", "Failed to parse request body: " + e.getMessage()));
            }
            
            // Check if cycleDataRequest is null
            if (cycleDataRequest == null) {
                logger.error("CycleDataRequest is null after parsing");
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Validation error", "Request data is required"));
            }
            
            // Validate required fields
            logger.info("=== VALIDATION ===");
            if (cycleDataRequest.getLastPeriodDate() == null) {
                logger.error("Validation failed: lastPeriodDate is null");
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Validation error", "Last period date is required"));
            }
            
            if (cycleDataRequest.getCycleLength() == null) {
                logger.error("Validation failed: cycleLength is null");
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Validation error", "Cycle length is required"));
            }
            
            logger.info("Validation passed - lastPeriodDate: {}, cycleLength: {}", 
                cycleDataRequest.getLastPeriodDate(), cycleDataRequest.getCycleLength());
            
            // Convert to CycleData
            CycleData cycleData = cycleDataRequest.toCycleData();
            logger.info("Converted to CycleData: {}", cycleData);
            
            // Log all fields of the cycleData object
            logger.info("=== CYCLE DATA FIELDS ===");
            logger.info("ID: {}", cycleData.getId());
            logger.info("User: {}", cycleData.getUser());
            logger.info("Start Date: {} (calculated from lastPeriodDate + cycleLength)", cycleData.getStartDate());
            logger.info("End Date: {} (calculated from startDate + periodLength - 1)", cycleData.getEndDate());
            logger.info("Phase: {}", cycleData.getPhase());
            logger.info("Symptoms: {}", cycleData.getSymptoms());
            logger.info("Mood: {}", cycleData.getMood());
            logger.info("Notes: {}", cycleData.getNotes());
            
            logger.info("=== AUTHENTICATION ===");
            User currentUser = userService.getCurrentUser();
            logger.info("Current user retrieved: {}", currentUser);
            logger.info("Current user ID: {}", currentUser != null ? currentUser.getId() : "null");
            logger.info("Current user username: {}", currentUser != null ? currentUser.getUsername() : "null");
            
            // Validate calculated dates
            if (cycleData.getStartDate() == null) {
                logger.error("Validation failed: Calculated start date is null");
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Validation error", "Failed to calculate start date from last period date and cycle length"));
            }
            
            if (cycleData.getEndDate() != null && cycleData.getStartDate().isAfter(cycleData.getEndDate())) {
                logger.error("Validation failed: Calculated start date {} is after end date {}", 
                    cycleData.getStartDate(), cycleData.getEndDate());
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Validation error", "Calculated start date cannot be after end date"));
            }
            logger.info("Date validation passed");
            
            // Set the user to current user
            logger.info("=== SETTING USER ===");
            logger.info("Setting user from {} to {}", cycleData.getUser(), currentUser);
            cycleData.setUser(currentUser);
            logger.info("User set successfully. CycleData user is now: {}", cycleData.getUser());
            
            // Save to database
            logger.info("=== SAVING TO DATABASE ===");
            logger.info("About to save cycle data: {}", cycleData);
            CycleData savedCycleData = cycleDataRepository.save(cycleData);
            logger.info("Successfully saved cycle data with ID: {}", savedCycleData.getId());
            logger.info("Saved cycle data: {}", savedCycleData);
            
            logger.info("=== RESPONSE ===");
            logger.info("Returning 201 CREATED response for user: {}", currentUser.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCycleData);
        } catch (Exception e) {
            logger.error("=== EXCEPTION IN CREATE CYCLE DATA ===");
            logger.error("Exception type: {}", e.getClass().getName());
            logger.error("Exception message: {}", e.getMessage());
            logger.error("Stack trace:", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to create cycle data", e.getMessage()));
        }
    }

    @Operation(summary = "Get cycle data by ID", description = "Retrieves a specific cycle data record by ID for the authenticated user")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCycleDataById(@PathVariable Long id) {
        try {
            User currentUser = userService.getCurrentUser();
            
            Optional<CycleData> cycleData = cycleDataRepository.findById(id);
            if (cycleData.isEmpty()) {
                logger.warn("Cycle data not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            CycleData data = cycleData.get();
            if (!data.getUser().getId().equals(currentUser.getId())) {
                logger.warn("User {} attempted to access cycle data {} belonging to user {}", 
                    currentUser.getUsername(), id, data.getUser().getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Access denied", "You can only access your own cycle data"));
            }
            
            logger.info("Retrieved cycle data {} for user: {}", id, currentUser.getUsername());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            logger.error("Failed to retrieve cycle data with ID: {}", id, e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to retrieve cycle data", e.getMessage()));
        }
    }

    @Operation(summary = "Get all cycle data for current user", description = "Retrieves all cycle data for the authenticated user with pagination")
    @GetMapping
    public ResponseEntity<?> getAllCycleData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            User currentUser = userService.getCurrentUser();
            
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<CycleData> cycleDataPage = cycleDataRepository.findByUser(currentUser, pageable);
            
            logger.info("Retrieved {} cycle data records for user: {}", 
                cycleDataPage.getTotalElements(), currentUser.getUsername());
            
            return ResponseEntity.ok(cycleDataPage);
        } catch (Exception e) {
            logger.error("Failed to retrieve cycle data", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to retrieve cycle data", e.getMessage()));
        }
    }

    @Operation(summary = "Get cycle data by date range", description = "Retrieves cycle data for the authenticated user within a specific date range")
    @GetMapping("/between")
    public ResponseEntity<?> getCycleDataByDateRange(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User currentUser = userService.getCurrentUser();
            
            if (start.isAfter(end)) {
                logger.warn("Invalid date range: start {} is after end {}", start, end);
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid date range", "Start date must be before or equal to end date"));
            }
            
            Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());
            Page<CycleData> cycleDataPage = cycleDataRepository.findByUserAndStartDateBetween(
                currentUser, start, end, pageable);
            
            logger.info("Retrieved {} cycle data records for user {} between {} and {}", 
                cycleDataPage.getTotalElements(), currentUser.getUsername(), start, end);
            
            return ResponseEntity.ok(cycleDataPage);
        } catch (Exception e) {
            logger.error("Failed to retrieve cycle data by date range", e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to retrieve cycle data", e.getMessage()));
        }
    }

    @Operation(summary = "Update cycle data", description = "Updates an existing cycle data record for the authenticated user - accepts frontend format")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCycleData(@PathVariable Long id, @RequestBody String requestBody) {
        try {
            logger.info("=== UPDATE CYCLE DATA REQUEST ===");
            logger.info("Received update request for ID: {}", id);
            logger.info("Raw request body: {}", requestBody);
            
            User currentUser = userService.getCurrentUser();
            
            Optional<CycleData> existingCycleData = cycleDataRepository.findById(id);
            if (existingCycleData.isEmpty()) {
                logger.warn("Cycle data not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            CycleData existing = existingCycleData.get();
            if (!existing.getUser().getId().equals(currentUser.getId())) {
                logger.warn("User {} attempted to update cycle data {} belonging to user {}", 
                    currentUser.getUsername(), id, existing.getUser().getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Access denied", "You can only update your own cycle data"));
            }
            
            // Parse the request body as CycleDataRequest
            CycleDataRequest cycleDataRequest = null;
            try {
                JsonNode jsonNode = objectMapper.readTree(requestBody);
                cycleDataRequest = objectMapper.treeToValue(jsonNode, CycleDataRequest.class);
                logger.info("Parsed update request: {}", cycleDataRequest);
            } catch (Exception e) {
                logger.error("Failed to parse update request JSON: {}", e.getMessage());
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid JSON format", "Failed to parse request body"));
            }
            
            // Update basic fields
            if (cycleDataRequest.getPhase() != null) {
                existing.setPhase(cycleDataRequest.getPhase());
            }
            if (cycleDataRequest.getSymptoms() != null) {
                existing.setSymptoms(cycleDataRequest.getSymptoms());
            }
            if (cycleDataRequest.getMood() != null) {
                existing.setMood(cycleDataRequest.getMood());
            }
            if (cycleDataRequest.getNotes() != null) {
                existing.setNotes(cycleDataRequest.getNotes());
            }
            
            // Update cycle parameters
            if (cycleDataRequest.getCycleLength() != null) {
                existing.setCycleLength(cycleDataRequest.getCycleLength());
            }
            if (cycleDataRequest.getPeriodLength() != null) {
                existing.setPeriodLength(cycleDataRequest.getPeriodLength());
            }
            
            // Recalculate dates if lastPeriodDate or cycle parameters changed
            if (cycleDataRequest.getLastPeriodDate() != null && cycleDataRequest.getCycleLength() != null) {
                try {
                    LocalDate lastPeriod = LocalDate.parse(cycleDataRequest.getLastPeriodDate());
                    LocalDate nextPeriodStart = lastPeriod.plusDays(cycleDataRequest.getCycleLength());
                    existing.setStartDate(nextPeriodStart);
                    
                    if (cycleDataRequest.getPeriodLength() != null) {
                        LocalDate nextPeriodEnd = nextPeriodStart.plusDays(cycleDataRequest.getPeriodLength() - 1);
                        existing.setEndDate(nextPeriodEnd);
                    }
                    
                    logger.info("Recalculated dates - startDate: {}, endDate: {}", 
                        existing.getStartDate(), existing.getEndDate());
                } catch (Exception e) {
                    logger.error("Failed to parse lastPeriodDate: {}", e.getMessage());
                    return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Invalid date format", "Failed to parse lastPeriodDate"));
                }
            }
            
            // Validate dates
            if (existing.getStartDate() != null && existing.getEndDate() != null) {
                if (existing.getStartDate().isAfter(existing.getEndDate())) {
                    return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Validation error", "Start date cannot be after end date"));
                }
            }
            
            CycleData updatedCycleData = cycleDataRepository.save(existing);
            
            logger.info("Updated cycle data {} for user: {}", id, currentUser.getUsername());
            
            // Return in frontend format
            CycleDataResponse response = CycleDataResponse.fromCycleData(
                updatedCycleData, 
                updatedCycleData.getCycleLength(), 
                updatedCycleData.getPeriodLength());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to update cycle data with ID: {}", id, e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to update cycle data", e.getMessage()));
        }
    }

    @Operation(summary = "Delete cycle data", description = "Deletes a cycle data record for the authenticated user")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCycleData(@PathVariable Long id) {
        try {
            User currentUser = userService.getCurrentUser();
            
            Optional<CycleData> cycleData = cycleDataRepository.findById(id);
            if (cycleData.isEmpty()) {
                logger.warn("Cycle data not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            CycleData data = cycleData.get();
            if (!data.getUser().getId().equals(currentUser.getId())) {
                logger.warn("User {} attempted to delete cycle data {} belonging to user {}", 
                    currentUser.getUsername(), id, data.getUser().getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Access denied", "You can only delete your own cycle data"));
            }
            
            cycleDataRepository.delete(data);
            
            logger.info("Deleted cycle data {} for user: {}", id, currentUser.getUsername());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to delete cycle data with ID: {}", id, e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to delete cycle data", e.getMessage()));
        }
    }

    @Operation(summary = "Get cycle data by user ID (path param)", description = "Retrieves cycle data for a specific user ID using path parameter")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCycleDataByUserIdPath(@PathVariable Long userId) {
        try {
            User currentUser = userService.getCurrentUser();
            
            // Security check: user can only access their own data or admin can access any data
            if (!currentUser.getId().equals(userId)) {
                // Check if current user has admin role
                boolean isAdmin = currentUser.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
                
                if (!isAdmin) {
                    logger.warn("User {} attempted to access cycle data for user {}", 
                        currentUser.getUsername(), userId);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Access denied", "You can only access your own cycle data"));
                }
            }
            
            // Find the target user
            Optional<User> targetUser = userService.findById(userId);
            if (targetUser.isEmpty()) {
                logger.warn("User not found with ID: {}", userId);
                return ResponseEntity.notFound().build();
            }
            
            // Get all cycle data for the user (no pagination for simplicity)
            List<CycleData> cycleDataList = cycleDataRepository.findByUserOrderByStartDateDesc(targetUser.get());
            
            // Convert to frontend response format
            List<CycleDataResponse> responseList = cycleDataList.stream()
                .map(cycleData -> CycleDataResponse.fromCycleData(
                    cycleData, 
                    cycleData.getCycleLength(), 
                    cycleData.getPeriodLength()))
                .collect(java.util.stream.Collectors.toList());
            
            // Create response wrapper
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("data", responseList);
            
            logger.info("Retrieved {} cycle data records for user ID: {} by user: {}", 
                cycleDataList.size(), userId, currentUser.getUsername());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to retrieve cycle data for user ID: {}", userId, e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to retrieve cycle data", e.getMessage()));
        }
    }

    @Operation(summary = "Get cycle data by user ID", description = "Retrieves cycle data for a specific user ID (admin or same user only)")
    @GetMapping("/user")
    public ResponseEntity<?> getCycleDataByUserId(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            User currentUser = userService.getCurrentUser();
            
            // Security check: user can only access their own data or admin can access any data
            if (!currentUser.getId().equals(userId)) {
                // Check if current user has admin role
                boolean isAdmin = currentUser.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
                
                if (!isAdmin) {
                    logger.warn("User {} attempted to access cycle data for user {}", 
                        currentUser.getUsername(), userId);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Access denied", "You can only access your own cycle data"));
                }
            }
            
            // Find the target user
            Optional<User> targetUser = userService.findById(userId);
            if (targetUser.isEmpty()) {
                logger.warn("User not found with ID: {}", userId);
                return ResponseEntity.notFound().build();
            }
            
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<CycleData> cycleDataPage = cycleDataRepository.findByUser(targetUser.get(), pageable);
            
            // Convert to frontend response format
            List<CycleDataResponse> responseList = cycleDataPage.getContent().stream()
                .map(cycleData -> CycleDataResponse.fromCycleData(
                    cycleData, 
                    cycleData.getCycleLength(), 
                    cycleData.getPeriodLength()))
                .collect(java.util.stream.Collectors.toList());
            
            // Create response wrapper with pagination info
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("data", responseList);
            response.put("pageable", cycleDataPage.getPageable());
            response.put("totalElements", cycleDataPage.getTotalElements());
            response.put("totalPages", cycleDataPage.getTotalPages());
            response.put("last", cycleDataPage.isLast());
            response.put("first", cycleDataPage.isFirst());
            
            logger.info("Retrieved {} cycle data records for user ID: {} by user: {}", 
                cycleDataPage.getTotalElements(), userId, currentUser.getUsername());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to retrieve cycle data for user ID: {}", userId, e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to retrieve cycle data", e.getMessage()));
        }
    }
} 