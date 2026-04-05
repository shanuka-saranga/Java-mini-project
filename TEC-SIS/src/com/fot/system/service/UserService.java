package com.fot.system.service;

import com.fot.system.config.AppConfig;
import com.fot.system.model.User;
import com.fot.system.repository.UserRepository;
import java.util.List;

public class UserService {

    private final UserRepository userRepository;



    public UserService() {
        this.userRepository = new UserRepository();
    }

    public User login(String email, String password) {

        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        if (password == null || password.isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        User user = userRepository.findByEmail(email);
        System.out.println("user data"+user.getEmail());
        if (user == null) {
            System.out.println("User user not found!");
            return null;
        }
        if (!user.getPasswordHash().equals(password)) {
            return null;
        }

        if (!AppConfig.STATUS_ACTIVE.equalsIgnoreCase(user.getStatus())) {
            System.out.println("user status is = "+ user.getStatus());
            throw new RuntimeException("User account is blocked");
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

    public User getUserById(int id) {
        return userRepository.findById(id);
    }


    //update
    public boolean updateUserProfile(com.fot.system.model.User user) {
        return userRepository.updateUserProfile(user);
    }

}