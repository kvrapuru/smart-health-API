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
            Object principal = authentication.getPrincipal();
            logger.info("Authentication principal type: {}", principal.getClass().getName());
            
            if (principal instanceof User) {
                User user = (User) principal;
                logger.info("Retrieved current user: {}", user);
                return user;
            } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                // Handle UserDetails from JWT authentication
                org.springframework.security.core.userdetails.UserDetails userDetails = 
                    (org.springframework.security.core.userdetails.UserDetails) principal;
                String username = userDetails.getUsername();
                logger.info("Retrieved username from UserDetails: {}", username);
                
                Optional<User> userOptional = userRepository.findByUsername(username);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    logger.info("Retrieved current user from repository: {}", user);
                    return user;
                } else {
                    logger.error("User not found in repository for username: {}", username);
                    throw new RuntimeException("User not found in repository");
                }
            } else {
                logger.error("Unexpected principal type: {}", principal.getClass().getName());
                throw new RuntimeException("Unexpected authentication principal type");
            }
        }
        logger.error("No authenticated user found");
        throw new RuntimeException("No authenticated user found");
    }
} 