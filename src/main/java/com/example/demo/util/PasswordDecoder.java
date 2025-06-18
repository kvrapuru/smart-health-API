package com.example.demo.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordDecoder {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = args[0];
        System.out.println("Hashed Password: " + hashedPassword);
        // Note: BCrypt is a one-way hash, so we cannot decode it.
        // This is just a utility to verify if a plain text password matches the hash.
        System.out.println("To verify a password, use encoder.matches(plainTextPassword, hashedPassword)");
    }
} 