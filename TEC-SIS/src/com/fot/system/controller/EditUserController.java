package com.fot.system.controller;

import com.fot.system.config.AppConfig;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.UserService;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Year;
import java.util.Set;
import java.util.regex.Pattern;

public class EditUserController {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{9}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z\\s'.-]{1,49}$");
    private static final Pattern REG_NO_PATTERN = Pattern.compile("^[A-Za-z]{2}/\\d{4}/\\d{3}$");
    private static final Pattern STAFF_CODE_PATTERN = Pattern.compile("^[A-Za-z0-9-]{3,20}$");
    private static final Set<String> VALID_ROLES = Set.of(
            AppConfig.ROLE_ADMIN,
            AppConfig.ROLE_DEAN,
            AppConfig.ROLE_LECTURER,
            AppConfig.ROLE_TO,
            AppConfig.ROLE_STUDENT
    );
    private static final Set<String> VALID_STATUSES = Set.of(
            AppConfig.STATUS_ACTIVE,
            AppConfig.STATUS_BLOCKED,
            "SUSPENDED"
    );
    private static final Set<String> VALID_STUDENT_TYPES = Set.of("PROPER", "REPEAT", "BATCH_MISSED");

    private final UserService userService;

    public EditUserController() {
        this.userService = new UserService();
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

        validateRole(request.getRole());
        validateName(request.getFirstName(), "First name is invalid.");
        validateName(request.getLastName(), "Last name is invalid.");
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());
        validatePhone(request.getPhone());
        validateAddress(request.getAddress());
        validateProfilePicturePath(request.getProfilePicturePath());
        validateStatus(request.getStatus());

        if (userService.emailExistsExcludingUserId(request.getEmail(), request.getUserId())) {
            throw new RuntimeException("Email already exists.");
        }

        if (userService.phoneExistsExcludingUserId(request.getPhone(), request.getUserId())) {
            throw new RuntimeException("Phone number already exists.");
        }

        if (request.isStudentRole()) {
            requireValue(request.getRegistrationNo(), "Registration number is required.");
            requireValue(request.getRegistrationYear(), "Registration year is required.");
            requireValue(request.getStudentType(), "Student type is required.");
            validateRegistrationNo(request.getRegistrationNo());
            validateStudentType(request.getStudentType());

            if (userService.registrationNoExistsExcludingUserId(request.getRegistrationNo(), request.getUserId())) {
                throw new RuntimeException("Registration number already exists.");
            }
        } else {
            requireValue(request.getStaffCode(), "Staff code is required.");
            validateStaffCode(request.getStaffCode());
            validateDesignation(request.getDesignation());

            if (userService.staffCodeExistsExcludingUserId(request.getStaffCode(), request.getUserId())) {
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
            int parsed = Integer.parseInt(departmentId.trim());
            if (parsed <= 0) {
                throw new RuntimeException("Department must be valid.");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Department must be valid.");
        }
    }

    private Date parseDob(String dob) {
        try {
            Date parsed = Date.valueOf(dob.trim());
            LocalDate dobDate = parsed.toLocalDate();
            if (dobDate.isAfter(LocalDate.now())) {
                throw new RuntimeException("DOB cannot be a future date.");
            }
            return parsed;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("DOB must be in yyyy-mm-dd format.");
        }
    }

    private int parseRegistrationYear(String registrationYear) {
        try {
            int year = Integer.parseInt(registrationYear.trim());
            int currentYear = Year.now().getValue();
            if (year < 1900 || year > currentYear + 1) {
                throw new RuntimeException("Registration year must be valid.");
            }
            return year;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Registration year must be a valid year.");
        }
    }

    private void validateRole(String role) {
        String normalized = role.trim().toUpperCase();
        if (!VALID_ROLES.contains(normalized)) {
            throw new RuntimeException("Invalid user role.");
        }
    }

    private void validateStatus(String status) {
        String normalized = status.trim().toUpperCase();
        if (!VALID_STATUSES.contains(normalized)) {
            throw new RuntimeException("Invalid user status.");
        }
    }

    private void validateEmail(String email) {
        String normalized = email.trim();
        if (!EMAIL_PATTERN.matcher(normalized).matches() || normalized.length() > 100) {
            throw new RuntimeException("Email format is invalid.");
        }
    }

    private void validatePhone(String phone) {
        String normalized = phone.trim();
        if (!PHONE_PATTERN.matcher(normalized).matches()) {
            throw new RuntimeException("Phone number must be 10 digits and start with 0.");
        }
    }

    private void validateName(String name, String message) {
        String normalized = name.trim();
        if (!NAME_PATTERN.matcher(normalized).matches()) {
            throw new RuntimeException(message);
        }
    }

    private void validatePassword(String password) {
        String normalized = password.trim();
        if (normalized.length() < 4 || normalized.length() > 255) {
            throw new RuntimeException("Password must be between 4 and 255 characters.");
        }
    }

    private void validateAddress(String address) {
        if (address == null) {
            return;
        }
        if (address.trim().length() > 150) {
            throw new RuntimeException("Address must be 150 characters or less.");
        }
    }

    private void validateProfilePicturePath(String profilePicturePath) {
        if (profilePicturePath == null || profilePicturePath.trim().isEmpty()) {
            return;
        }
        if (profilePicturePath.trim().length() > 500) {
            throw new RuntimeException("Profile picture path is too long.");
        }
    }

    private void validateRegistrationNo(String registrationNo) {
        String normalized = registrationNo.trim();
        if (!REG_NO_PATTERN.matcher(normalized).matches()) {
            throw new RuntimeException("Registration number format is invalid.");
        }
    }

    private void validateStudentType(String studentType) {
        String normalized = studentType.trim().toUpperCase();
        if (!VALID_STUDENT_TYPES.contains(normalized)) {
            throw new RuntimeException("Invalid student type.");
        }
    }

    private void validateStaffCode(String staffCode) {
        String normalized = staffCode.trim();
        if (!STAFF_CODE_PATTERN.matcher(normalized).matches()) {
            throw new RuntimeException("Staff code format is invalid.");
        }
    }

    private void validateDesignation(String designation) {
        if (designation == null) {
            return;
        }
        if (designation.trim().length() > 50) {
            throw new RuntimeException("Designation must be 50 characters or less.");
        }
    }
}
