package com.fot.system.controller;

import com.fot.system.config.AppConfig;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.UserService;
import com.fot.system.view.dashboard.admin.manageUsers.AddNewUserPanel;

import javax.swing.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Year;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * validate and process add-user requests from admin manage users panel
 * @author janith
 */
public class AddUserController {
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

    private final AddNewUserPanel view;
    private final UserService userService;
    private final Runnable onSuccessAction;

    /**
     * initialize add user controller and bind save action
     * @param view add user panel
     * @param onSuccessAction callback after successful save
     * @author janith
     */
    public AddUserController(AddNewUserPanel view, Runnable onSuccessAction) {
        this.view = view;
        this.userService = new UserService();
        this.onSuccessAction = onSuccessAction;
        this.view.setOnSaveAction(this::handleSaveUser);
    }

    /**
     * collect form request, validate and save user
     * @author janith
     */
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

    /**
     * validate add-user request payload
     * @param request user input payload
     * @author janith
     */
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

        validateRole(request.getRole());
        validateName(request.getFirstName(), "First name is invalid.");
        validateName(request.getLastName(), "Last name is invalid.");
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());
        validatePhone(request.getPhone());
        validateAddress(request.getAddress());
        validateStatus(request.getStatus());

        if (userService.emailExists(request.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }

        if (userService.phoneExists(request.getPhone())) {
            throw new RuntimeException("Phone number already exists.");
        }

        if (request.isStudentRole()) {
            requireValue(request.getRegistrationNo(), "Registration number is required.");
            requireValue(request.getRegistrationYear(), "Registration year is required.");
            requireValue(request.getStudentType(), "Student type is required.");
            validateRegistrationNo(request.getRegistrationNo());
            validateStudentType(request.getStudentType());

            if (userService.registrationNoExists(request.getRegistrationNo())) {
                throw new RuntimeException("Registration number already exists.");
            }
        } else {
            requireValue(request.getStaffCode(), "Staff code is required.");
            validateStaffCode(request.getStaffCode());
            validateDesignation(request.getDesignation());

            if (userService.staffCodeExists(request.getStaffCode())) {
                throw new RuntimeException("Staff code already exists.");
            }
        }

        parseDepartmentId(request.getDepartmentId());
        parseDob(request.getDob());

        if (request.isStudentRole()) {
            parseRegistrationYear(request.getRegistrationYear());
        }
    }

    /**
     * check mandatory string value
     * @param value value to check
     * @param message validation error message
     * @author janith
     */
    private void requireValue(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
    }

    /**
     * parse and validate department id
     * @param departmentId department id value
     * @author janith
     */
    private int parseDepartmentId(String departmentId) {
        try {
            int parsed = Integer.parseInt(departmentId.trim());
            if (parsed <= 0) {
                throw new RuntimeException("Department ID must be a valid number.");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Department ID must be a valid number.");
        }
    }

    /**
     * parse and validate date of birth
     * @param dob date-of-birth value
     * @author janith
     */
    private Date parseDob(String dob) {
        try {
            Date parsed = Date.valueOf(dob.trim());
            if (parsed.toLocalDate().isAfter(LocalDate.now())) {
                throw new RuntimeException("DOB cannot be a future date.");
            }
            return parsed;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("DOB must be in yyyy-mm-dd format.");
        }
    }

    /**
     * parse and validate student registration year
     * @param registrationYear registration year value
     * @author janith
     */
    private int parseRegistrationYear(String registrationYear) {
        try {
            int year = Integer.parseInt(registrationYear.trim());
            int maxYear = Year.now().getValue() + 1;
            if (year < 1900 || year > maxYear) {
                throw new RuntimeException("Registration year must be a valid year.");
            }
            return year;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Registration year must be a valid year.");
        }
    }

    /**
     * validate role value
     * @param role role value
     * @author janith
     */
    private void validateRole(String role) {
        String normalized = normalize(role).toUpperCase();
        if (!VALID_ROLES.contains(normalized)) {
            throw new RuntimeException("Invalid user role.");
        }
    }

    /**
     * validate status value
     * @param status status value
     * @author janith
     */
    private void validateStatus(String status) {
        String normalized = normalize(status).toUpperCase();
        if (!VALID_STATUSES.contains(normalized)) {
            throw new RuntimeException("Invalid user status.");
        }
    }

    /**
     * validate email format
     * @param email email value
     * @author janith
     */
    private void validateEmail(String email) {
        String normalized = normalize(email);
        if (!EMAIL_PATTERN.matcher(normalized).matches() || normalized.length() > 100) {
            throw new RuntimeException("Email format is invalid.");
        }
    }

    /**
     * validate phone format
     * @param phone phone value
     * @author janith
     */
    private void validatePhone(String phone) {
        String normalized = normalize(phone);
        if (!PHONE_PATTERN.matcher(normalized).matches()) {
            throw new RuntimeException("Phone number must be 10 digits and start with 0.");
        }
    }

    /**
     * validate first/last name format
     * @param name name value
     * @param message error message
     * @author janith
     */
    private void validateName(String name, String message) {
        String normalized = normalize(name);
        if (!NAME_PATTERN.matcher(normalized).matches()) {
            throw new RuntimeException(message);
        }
    }

    /**
     * validate password length
     * @param password password value
     * @author janith
     */
    private void validatePassword(String password) {
        String normalized = normalize(password);
        if (normalized.length() < 4 || normalized.length() > 255) {
            throw new RuntimeException("Password must be between 4 and 255 characters.");
        }
    }

    /**
     * validate optional address length
     * @param address address value
     * @author janith
     */
    private void validateAddress(String address) {
        if (address == null) {
            return;
        }
        if (address.trim().length() > 150) {
            throw new RuntimeException("Address must be 150 characters or less.");
        }
    }

    /**
     * validate student registration number format
     * @param registrationNo registration number value
     * @author janith
     */
    private void validateRegistrationNo(String registrationNo) {
        String normalized = normalize(registrationNo);
        if (!REG_NO_PATTERN.matcher(normalized).matches()) {
            throw new RuntimeException("Registration number format is invalid.");
        }
    }

    /**
     * validate student type
     * @param studentType student type value
     * @author janith
     */
    private void validateStudentType(String studentType) {
        String normalized = normalize(studentType).toUpperCase();
        if (!VALID_STUDENT_TYPES.contains(normalized)) {
            throw new RuntimeException("Invalid student type.");
        }
    }

    /**
     * validate staff code format
     * @param staffCode staff code value
     * @author janith
     */
    private void validateStaffCode(String staffCode) {
        String normalized = normalize(staffCode);
        if (!STAFF_CODE_PATTERN.matcher(normalized).matches()) {
            throw new RuntimeException("Staff code format is invalid.");
        }
    }

    /**
     * validate optional designation length
     * @param designation designation value
     * @author janith
     */
    private void validateDesignation(String designation) {
        if (designation == null) {
            return;
        }
        if (designation.trim().length() > 50) {
            throw new RuntimeException("Designation must be 50 characters or less.");
        }
    }

    /**
     * normalize string input by trimming leading and trailing spaces
     * @param value raw input value
     * @author janith
     */
    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
