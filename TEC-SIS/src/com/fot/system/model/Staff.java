package com.fot.system.model;

import java.util.Date;

public class Staff extends User {
    private String staffCode;
    private String designation;

    public Staff() {}

    public Staff(
            int id,
            String firstName,
            String lastName,
            String role,
            Date dob,
            String email,
            String phone,
            String address,
            int departmentId,
            String passwordHash,
            String status,
            String staffCode,
            String designation
    ) {
        super(id, firstName, lastName, role, dob, email, phone, address, departmentId, passwordHash, status);
        this.staffCode = staffCode;
        this.designation = designation;
    }

    public String getStaffCode() { return staffCode; }
    public void setStaffCode(String staffCode) { this.staffCode = staffCode; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    @Override
    public String toString() {
        return "Staff{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", role='" + getRole() + '\'' +
                ", dob=" + getDob() +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", departmentId=" + getDepartmentId() +
                ", staffCode='" + staffCode + '\'' +
                ", designation='" + designation + '\'' +
                '}';
    }

}