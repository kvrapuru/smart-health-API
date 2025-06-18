package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.example.demo.model.CycleData;
import jakarta.validation.Valid;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CycleDataRequest {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("userId")
    private Long userId;
    
    @JsonProperty("lastPeriodDate")
    private String lastPeriodDate;
    
    @JsonProperty("cycleLength")
    private Integer cycleLength;
    
    @JsonProperty("periodLength")
    private Integer periodLength;
    
    @JsonProperty("phase")
    private String phase;
    
    @JsonProperty("symptoms")
    private String symptoms;
    
    @JsonProperty("mood")
    private String mood;
    
    @JsonProperty("notes")
    private String notes;
    
    // Constructor
    public CycleDataRequest() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
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
    
    public String getPhase() {
        return phase;
    }
    
    public void setPhase(String phase) {
        this.phase = phase;
    }
    
    public String getSymptoms() {
        return symptoms;
    }
    
    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
    
    public String getMood() {
        return mood;
    }
    
    public void setMood(String mood) {
        this.mood = mood;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // Method to convert to CycleData
    public CycleData toCycleData() {
        CycleData cycleData = new CycleData();
        
        // Set basic fields
        cycleData.setPhase(this.phase);
        cycleData.setSymptoms(this.symptoms);
        cycleData.setMood(this.mood);
        cycleData.setNotes(this.notes);
        
        // Set cycle parameters
        cycleData.setCycleLength(this.cycleLength);
        cycleData.setPeriodLength(this.periodLength);
        
        // Calculate start and end dates based on lastPeriodDate and cycleLength
        if (this.lastPeriodDate != null && this.cycleLength != null) {
            LocalDate lastPeriod = LocalDate.parse(this.lastPeriodDate);
            
            // Calculate next period start date
            LocalDate nextPeriodStart = lastPeriod.plusDays(this.cycleLength);
            cycleData.setStartDate(nextPeriodStart);
            
            // Calculate end date based on period length
            if (this.periodLength != null) {
                LocalDate nextPeriodEnd = nextPeriodStart.plusDays(this.periodLength - 1);
                cycleData.setEndDate(nextPeriodEnd);
            }
        }
        
        return cycleData;
    }
    
    @Override
    public String toString() {
        return "CycleDataRequest{" +
                "id=" + id +
                ", userId=" + userId +
                ", lastPeriodDate='" + lastPeriodDate + '\'' +
                ", cycleLength=" + cycleLength +
                ", periodLength=" + periodLength +
                ", phase='" + phase + '\'' +
                ", symptoms='" + symptoms + '\'' +
                ", mood='" + mood + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
} 