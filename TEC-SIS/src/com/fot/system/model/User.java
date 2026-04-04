package com.fot.system.model;

import java.util.Date;

public abstract class User {

    private int id;
    private String firstName;
    private String lastName;
    private String role;
    private Date dob;
    private String email;
    private String phone;
    private String address;
    private int departmentId;
    private String passwordHash;
    private String status;

    public User() {}

    public User(
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
            String status
    ) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.dob = dob;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.departmentId = departmentId;
        this.passwordHash = passwordHash;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName(){
        return firstName + " " + lastName;
    }
    public String getRole() {
        return role;
    }

    public void setRole(String role) {  //TODO make as protected
        this.role = role;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

}