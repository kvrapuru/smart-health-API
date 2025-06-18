package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "exercise_log")
public class ExerciseLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    @com.fasterxml.jackson.annotation.JsonProperty("exerciseType")
    private String exerciseType;

    @Column(nullable = false)
    @com.fasterxml.jackson.annotation.JsonProperty("duration")
    private Integer duration;

    @Column(nullable = false)
    @com.fasterxml.jackson.annotation.JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @Column(nullable = false)
    @com.fasterxml.jackson.annotation.JsonProperty("caloriesBurned")
    private Integer caloriesBurned;
} 