package com.fot.system.service;

import com.fot.system.model.User;
import com.fot.system.repository.UserRepository;

import java.util.List;

public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    // 🔐 LOGIN LOGIC
    public User login(String email, String password) {

        // Validation
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        if (password == null || password.isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return null; // user not found
        }

        if (!user.getPasswordHash().equals(password)) {
            return null; // wrong password
        }

        if (!"Active".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("User account is inactive");
        }

        return user;
    }

    public boolean register(User user) {

        if (user == null) {
            throw new RuntimeException("User cannot be null");
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        User existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser != null) {
            throw new RuntimeException("Email already exists");
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}