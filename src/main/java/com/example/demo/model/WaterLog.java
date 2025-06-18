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

    // Manual getters since Lombok @Data isn't working
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
} 