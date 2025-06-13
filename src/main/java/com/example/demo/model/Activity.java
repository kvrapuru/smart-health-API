package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private Double caloriesBurnedPerHour;
    private String category; // e.g., "Cardio", "Strength", "Flexibility"
    private String intensity; // e.g., "Low", "Medium", "High"

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getCaloriesBurnedPerHour() { return caloriesBurnedPerHour; }
    public void setCaloriesBurnedPerHour(Double caloriesBurnedPerHour) { this.caloriesBurnedPerHour = caloriesBurnedPerHour; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getIntensity() { return intensity; }
    public void setIntensity(String intensity) { this.intensity = intensity; }
} 