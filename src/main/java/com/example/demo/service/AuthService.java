package com.example.demo.service;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    public User register(RegisterRequest request) {
        logger.info("Processing registration for username: {}", request.getUsername());
        
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            logger.warn("Registration failed: Username already exists - {}", request.getUsername());
            throw new RuntimeException("Username already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        
        try {
            user = userRepository.save(user);
            logger.info("User registered successfully: {}", user.getUsername());
            return user;
        } catch (Exception e) {
            logger.error("Error saving user during registration: {}", request.getUsername(), e);
            throw new RuntimeException("Failed to save user: " + e.getMessage());
        }
    }

    public User login(String username) {
        logger.info("Processing login for username: {}", username);
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Login failed: Username not found - {}", username);
                    return new RuntimeException("Username not found");
                });
    }
} 