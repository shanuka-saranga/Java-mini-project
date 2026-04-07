package com.fot.system.controller;

import com.fot.system.model.AddUserRequest;
import com.fot.system.model.User;
import com.fot.system.repository.UserRepository;
import com.fot.system.service.UserService;
import com.fot.system.view.dashboard.admin.manageUsers.AddNewUserPanel;

import javax.swing.*;
import java.sql.Date;

public class AddUserController {

    private final AddNewUserPanel view;
    private final UserService userService;
    private final UserRepository userRepository;
    private final Runnable onSuccessAction;

    public AddUserController(AddNewUserPanel view, Runnable onSuccessAction) {
        this.view = view;
        this.userService = new UserService();
        this.userRepository = new UserRepository();
        this.onSuccessAction = onSuccessAction;
        this.view.setOnSaveAction(this::handleSaveUser);
    }

    private void handleSaveUser() {
        try {
            AddUserRequest request = view.buildRequest();
            validateAddUserRequest(request);
            User user = userService.addUser(request);

            JOptionPane.showMessageDialog(
                    view,
                    user.getRole() + " user added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            view.resetForm();
            if (onSuccessAction != null) {
                onSuccessAction.run();
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    view,
                    ex.getMessage(),
                    "Add User Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void validateAddUserRequest(AddUserRequest request) {
        if (request == null) {
            throw new RuntimeException("User request cannot be null.");
        }

        requireValue(request.getRole(), "User role is required.");
        requireValue(request.getFirstName(), "First name is required.");
        requireValue(request.getLastName(), "Last name is required.");
        requireValue(request.getEmail(), "Email is required.");
        requireValue(request.getPassword(), "Password is required.");
        requireValue(request.getPhone(), "Phone is required.");
        requireValue(request.getDob(), "DOB is required.");
        requireValue(request.getDepartmentId(), "Department ID is required.");
        requireValue(request.getStatus(), "Status is required.");

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already exists.");
        }

        if (request.isStudentRole()) {
            requireValue(request.getRegistrationNo(), "Registration number is required.");
            requireValue(request.getRegistrationYear(), "Registration year is required.");
            requireValue(request.getStudentType(), "Student type is required.");

            if (userRepository.existsByRegistrationNo(request.getRegistrationNo())) {
                throw new RuntimeException("Registration number already exists.");
            }
        } else {
            requireValue(request.getStaffCode(), "Staff code is required.");

            if (userRepository.existsByStaffCode(request.getStaffCode())) {
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
            throw new RuntimeException("Department ID must be a valid number.");
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
