package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "weight_log")
public class WeightLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    @com.fasterxml.jackson.annotation.JsonProperty("weight")
    private Double weight;

    @Column(name = "target_weight")
    @com.fasterxml.jackson.annotation.JsonProperty("targetWeight")
    private Double targetWeight;

    @Column(nullable = false)
    @com.fasterxml.jackson.annotation.JsonProperty("timestamp")
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Column(nullable = false)
    @com.fasterxml.jackson.annotation.JsonProperty("unit")
    private String unit = "KG";

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getTargetWeight() {
        return targetWeight;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getUnit() {
        return unit;
    }
} 