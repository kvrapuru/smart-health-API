package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        logger.info("Finding user by ID: {}", id);
        Optional<User> user = userRepository.findById(id);
        logger.info("Found user: {}", user);
        return user;
    }

    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    public User updateUser(User user) {
        logger.info("Updating user in database: {}", user);
        User savedUser = userRepository.save(user);
        logger.info("Successfully saved user: {}", savedUser);
        return savedUser;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            logger.info("Retrieved current user: {}", user);
            return user;
        }
        logger.error("No authenticated user found");
        throw new RuntimeException("No authenticated user found");
    }
} 