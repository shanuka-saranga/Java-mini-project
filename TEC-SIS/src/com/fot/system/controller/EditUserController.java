package com.fot.system.controller;

import com.fot.system.model.EditUserRequest;
import com.fot.system.model.User;
import com.fot.system.repository.UserRepository;
import com.fot.system.service.UserService;

import java.sql.Date;

public class EditUserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public EditUserController() {
        this.userService = new UserService();
        this.userRepository = new UserRepository();
    }

    public User updateUser(EditUserRequest request) {
        validateEditUserRequest(request);
        return userService.updateUser(request);
    }

    public void deleteUser(int userId) {
        if (userId <= 0) {
            throw new RuntimeException("Invalid user ID.");
        }

        boolean deleted = userService.deleteUser(userId);
        if (!deleted) {
            throw new RuntimeException("User delete failed.");
        }
    }

    private void validateEditUserRequest(EditUserRequest request) {
        if (request == null) {
            throw new RuntimeException("User request cannot be null.");
        }

        if (request.getUserId() <= 0) {
            throw new RuntimeException("Invalid user ID.");
        }

        requireValue(request.getRole(), "User role is required.");
        requireValue(request.getFirstName(), "First name is required.");
        requireValue(request.getLastName(), "Last name is required.");
        requireValue(request.getEmail(), "Email is required.");
        requireValue(request.getPassword(), "Password is required.");
        requireValue(request.getPhone(), "Phone is required.");
        requireValue(request.getDob(), "DOB is required.");
        requireValue(request.getDepartmentId(), "Department is required.");
        requireValue(request.getStatus(), "Status is required.");

        if (userRepository.existsByEmailExcludingUserId(request.getEmail(), request.getUserId())) {
            throw new RuntimeException("Email already exists.");
        }

        if (userRepository.existsByPhoneExcludingUserId(request.getPhone(), request.getUserId())) {
            throw new RuntimeException("Phone number already exists.");
        }

        if (request.isStudentRole()) {
            requireValue(request.getRegistrationNo(), "Registration number is required.");
            requireValue(request.getRegistrationYear(), "Registration year is required.");
            requireValue(request.getStudentType(), "Student type is required.");

            if (userRepository.existsByRegistrationNoExcludingUserId(request.getRegistrationNo(), request.getUserId())) {
                throw new RuntimeException("Registration number already exists.");
            }
        } else {
            requireValue(request.getStaffCode(), "Staff code is required.");

            if (userRepository.existsByStaffCodeExcludingUserId(request.getStaffCode(), request.getUserId())) {
                throw new RuntimeException("Staff code already exists.");
            }
        }

        parseDepartmentId(request.getDepartmentId());
        parseDob(request.getDob());

        if (request.isStudentRole()) {
            parseRegistrationYear(request.getRegistrationYear());
        }
    }

    private void requireValue(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
    }

    private int parseDepartmentId(String departmentId) {
        try {
            return Integer.parseInt(departmentId.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Department must be valid.");
        }
    }

    private Date parseDob(String dob) {
        try {
            return Date.valueOf(dob.trim());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("DOB must be in yyyy-mm-dd format.");
        }
    }

    private int parseRegistrationYear(String registrationYear) {
        try {
            return Integer.parseInt(registrationYear.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Registration year must be a valid year.");
        }
    }
}
