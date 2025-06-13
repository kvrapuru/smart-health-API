package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public User getUserFromToken(String token) {
        // For now, we'll just return a mock user since we removed JWT
        // In a real application, you would validate the token and return the actual user
        return userRepository.findById(1L).orElseThrow(() -> new RuntimeException("User not found"));
    }
} 