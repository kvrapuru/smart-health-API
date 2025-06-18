package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.demo.model.CycleData;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;

public class CycleDataResponse {
    @JsonProperty("id")
    private String id;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("lastPeriodDate")
    private String lastPeriodDate;

    @JsonProperty("cycleLength")
    private Integer cycleLength;

    @JsonProperty("periodLength")
    private Integer periodLength;

    @JsonProperty("currentDay")
    private Integer currentDay;

    @JsonProperty("phase")
    private String phase;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("updatedAt")
    private String updatedAt;

    // Constructor
    public CycleDataResponse() {}

    public CycleDataResponse(CycleData cycleData) {
        this.id = cycleData.getId().toString();
        this.userId = cycleData.getUser().getId().toString();
        this.phase = cycleData.getPhase();
    }

    // Static method to convert from CycleData
    public static CycleDataResponse fromCycleData(CycleData cycleData, Integer cycleLength, Integer periodLength) {
        CycleDataResponse response = new CycleDataResponse();
        
        // Set basic fields
        response.setId(cycleData.getId().toString());
        response.setUserId(cycleData.getUser().getId().toString());
        response.setPhase(cycleData.getPhase());
        
        // Use provided cycleLength and periodLength, or fall back to defaults
        Integer actualCycleLength = cycleLength != null ? cycleLength : 28; // Default 28 days
        Integer actualPeriodLength = periodLength != null ? periodLength : 5; // Default 5 days
        
        response.setCycleLength(actualCycleLength);
        response.setPeriodLength(actualPeriodLength);
        
        // Calculate lastPeriodDate from startDate and cycleLength
        if (cycleData.getStartDate() != null && actualCycleLength != null) {
            LocalDate lastPeriod = cycleData.getStartDate().minusDays(actualCycleLength);
            response.setLastPeriodDate(lastPeriod.format(DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            response.setLastPeriodDate(null);
        }
        
        // Calculate current day in cycle
        if (actualCycleLength != null && response.getLastPeriodDate() != null) {
            try {
                LocalDate lastPeriod = LocalDate.parse(response.getLastPeriodDate());
                LocalDate today = LocalDate.now();
                
                // Calculate days since last period
                long daysSinceLastPeriod = ChronoUnit.DAYS.between(lastPeriod, today);
                
                if (daysSinceLastPeriod >= 0) {
                    // Calculate current day in cycle (1-based, where 1 is the first day of period)
                    int currentDay = (int) (daysSinceLastPeriod % actualCycleLength) + 1;
                    response.setCurrentDay(currentDay);
                } else {
                    // If last period is in the future, set to 0 or null
                    response.setCurrentDay(0);
                }
            } catch (Exception e) {
                // If there's any parsing error, set to 0
                response.setCurrentDay(0);
            }
        } else {
            response.setCurrentDay(0);
        }
        
        // Set timestamps (you might need to add these fields to CycleData model)
        // For now, using current timestamp
        response.setCreatedAt(java.time.Instant.now().toString());
        response.setUpdatedAt(java.time.Instant.now().toString());
        
        return response;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastPeriodDate() {
        return lastPeriodDate;
    }

    public void setLastPeriodDate(String lastPeriodDate) {
        this.lastPeriodDate = lastPeriodDate;
    }

    public Integer getCycleLength() {
        return cycleLength;
    }

    public void setCycleLength(Integer cycleLength) {
        this.cycleLength = cycleLength;
    }

    public Integer getPeriodLength() {
        return periodLength;
    }

    public void setPeriodLength(Integer periodLength) {
        this.periodLength = periodLength;
    }

    public Integer getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(Integer currentDay) {
        this.currentDay = currentDay;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
} 