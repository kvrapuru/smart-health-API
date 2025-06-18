package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
public class CycleData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @JsonProperty("startDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @JsonProperty("endDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    @JsonProperty("phase")
    @Size(max = 50, message = "Phase must not exceed 50 characters")
    private String phase; // e.g., "Menstrual", "Follicular", "Ovulation", "Luteal"
    
    @JsonProperty("symptoms")
    @Size(max = 500, message = "Symptoms must not exceed 500 characters")
    private String symptoms;
    
    @JsonProperty("mood")
    @Size(max = 100, message = "Mood must not exceed 100 characters")
    private String mood;
    
    @JsonProperty("notes")
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
    
    @JsonProperty("cycleLength")
    private Integer cycleLength;
    
    @JsonProperty("periodLength")
    private Integer periodLength;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }

    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Integer getCycleLength() { return cycleLength; }
    public void setCycleLength(Integer cycleLength) { this.cycleLength = cycleLength; }
    
    public Integer getPeriodLength() { return periodLength; }
    public void setPeriodLength(Integer periodLength) { this.periodLength = periodLength; }
} 