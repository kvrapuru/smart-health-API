package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "steps_log")
public class StepsLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer steps;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public void setUserId(Long userId) {
        this.userId = userId;
    }
} 