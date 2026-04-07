package com.fot.system.service;

import com.fot.system.config.AppConfig;
import com.fot.system.model.AddUserRequest;
import com.fot.system.model.Staff;
import com.fot.system.model.Student;
import com.fot.system.model.User;
import com.fot.system.repository.UserRepository;

import java.sql.Date;
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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int id) {
        return userRepository.findById(id);
    }


    public User addUser(AddUserRequest request) {
        User user = createUserByRole(request);
        boolean saved = userRepository.save(user);

        if (!saved) {
            throw new RuntimeException("User registration failed.");
        }

        return user;
    }

    private User createUserByRole(AddUserRequest request) {
        if (request.isStudentRole()) {
            Student student = new Student();
            populateCommonFields(student, request);
            student.setRegistrationNo(request.getRegistrationNo());
            student.setRegistrationYear(parseRegistrationYear(request.getRegistrationYear()));
            student.setStudentType(request.getStudentType());
            return student;
        }

        Staff staff = new Staff();
        populateCommonFields(staff, request);
        staff.setStaffCode(request.getStaffCode());
        staff.setDesignation(request.getDesignation());
        return staff;
    }

    private void populateCommonFields(User user, AddUserRequest request) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setDob(parseDob(request.getDob()));
        user.setRole(request.getRole());
        user.setStatus(normalizeStatus(request.getStatus()));
        user.setDepartmentId(parseDepartmentId(request.getDepartmentId()));
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

    private String normalizeStatus(String status) {
        if ("SUSPENDED".equalsIgnoreCase(status)) {
            return "SUSPENDED";
        }
        if (AppConfig.STATUS_BLOCKED.equalsIgnoreCase(status)) {
            return AppConfig.STATUS_BLOCKED;
        }
        return AppConfig.STATUS_ACTIVE;
    }

}
