package com.fot.system.model;

import com.fot.system.config.AppConfig;

public class EditUserRequest {
    private final int userId;
    private final String role;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private final String phone;
    private final String address;
    private final String dob;
    private final String departmentId;
    private final String status;
    private final String registrationNo;
    private final String registrationYear;
    private final String studentType;
    private final String staffCode;
    private final String designation;

    public EditUserRequest(
            int userId,
            String role,
            String firstName,
            String lastName,
            String email,
            String password,
            String phone,
            String address,
            String dob,
            String departmentId,
            String status,
            String registrationNo,
            String registrationYear,
            String studentType,
            String staffCode,
            String designation
    ) {
        this.userId = userId;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.dob = dob;
        this.departmentId = departmentId;
        this.status = status;
        this.registrationNo = registrationNo;
        this.registrationYear = registrationYear;
        this.studentType = studentType;
        this.staffCode = staffCode;
        this.designation = designation;
    }

    public int getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getDob() {
        return dob;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public String getStatus() {
        return status;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public String getRegistrationYear() {
        return registrationYear;
    }

    public String getStudentType() {
        return studentType;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public String getDesignation() {
        return designation;
    }

    public boolean isStudentRole() {
        return AppConfig.ROLE_STUDENT.equalsIgnoreCase(role);
    }
}
