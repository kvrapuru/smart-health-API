package com.example.demo.controller;

import com.example.demo.model.CycleData;
import com.example.demo.model.User;
import com.example.demo.repository.CycleDataRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cycle-data")
public class CycleDataController {

    @Autowired
    private CycleDataRepository cycleDataRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<CycleData> getAllCycleData() {
        return cycleDataRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CycleData> getCycleDataById(@PathVariable Long id) {
        return cycleDataRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CycleData>> getCycleDataByUser(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(cycleDataRepository.findByUser(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/between")
    public ResponseEntity<List<CycleData>> getCycleDataByUserAndDateRange(
            @PathVariable Long userId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(cycleDataRepository.findByUserAndStartDateBetween(user, start, end)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CycleData> createCycleData(@RequestBody CycleData cycleData) {
        if (cycleData.getUser() == null || cycleData.getUser().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        return userRepository.findById(cycleData.getUser().getId())
                .map(user -> {
                    cycleData.setUser(user);
                    return ResponseEntity.ok(cycleDataRepository.save(cycleData));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CycleData> updateCycleData(@PathVariable Long id, @RequestBody CycleData cycleDataDetails) {
        return cycleDataRepository.findById(id)
                .map(existingCycleData -> {
                    existingCycleData.setStartDate(cycleDataDetails.getStartDate());
                    existingCycleData.setEndDate(cycleDataDetails.getEndDate());
                    existingCycleData.setPhase(cycleDataDetails.getPhase());
                    existingCycleData.setSymptoms(cycleDataDetails.getSymptoms());
                    existingCycleData.setMood(cycleDataDetails.getMood());
                    existingCycleData.setNotes(cycleDataDetails.getNotes());
                    return ResponseEntity.ok(cycleDataRepository.save(existingCycleData));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCycleData(@PathVariable Long id) {
        return cycleDataRepository.findById(id)
                .map(cycleData -> {
                    cycleDataRepository.delete(cycleData);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 