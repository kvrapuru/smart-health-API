package com.example.demo.controller;

import com.example.demo.model.CycleData;
import com.example.demo.model.User;
import com.example.demo.repository.CycleDataRepository;
import com.example.demo.service.UserService;
import com.example.demo.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Cycle Tracker (Legacy)", description = "Legacy cycle tracking APIs for backward compatibility")
public class CycleDataLegacyController {

    private static final Logger logger = LoggerFactory.getLogger(CycleDataLegacyController.class);

    @Autowired
    private CycleDataRepository cycleDataRepository;

    @Autowired
    private UserService userService;

    @Operation(summary = "Get cycle data by userId (legacy)", description = "Legacy endpoint for backward compatibility")
    @GetMapping(path = "/cycleData", produces = "application/json")
    public ResponseEntity<?> getCycleDataByUserId(@RequestParam Long userId) {
        try {
            logger.info("Legacy endpoint called for userId: {}", userId);
            
            // Find the user first
            Optional<User> userOptional = userService.findById(userId);
            if (userOptional.isEmpty()) {
                logger.warn("User not found with ID: {}", userId);
                return ResponseEntity.notFound().build();
            }
            
            User user = userOptional.get();
            List<CycleData> cycleDataList = cycleDataRepository.findByUser(user);
            
            logger.info("Retrieved {} cycle data records for user: {}", cycleDataList.size(), userId);
            return ResponseEntity.ok(cycleDataList);
        } catch (Exception e) {
            logger.error("Failed to retrieve cycle data for userId: {}", userId, e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to retrieve cycle data", e.getMessage()));
        }
    }
} 