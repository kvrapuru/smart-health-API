package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "water_log")
public class WaterLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    @com.fasterxml.jackson.annotation.JsonProperty("amount")
    private Double amount;

    @Column(nullable = false)
    @com.fasterxml.jackson.annotation.JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @Column(nullable = false)
    @com.fasterxml.jackson.annotation.JsonProperty("unit")
    private String unit = "ML";

    public void setUserId(Long userId) {
        this.userId = userId;
    }
} 