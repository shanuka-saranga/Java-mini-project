package com.fot.system.model;

import java.util.Date;

public class Student extends User {
    private String registrationNo;
    private int registrationYear;
    private String studentType;

    public Student() {}

    public Student(
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
            String regNo,
            int regYear,
            String type
    ) {
        super(id, firstName, lastName, role, dob, email, phone, address, departmentId, passwordHash, status);
        this.registrationNo = regNo;
        this.registrationYear = regYear;
        this.studentType = type;
    }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

    public int getRegistrationYear() { return registrationYear; }
    public void setRegistrationYear(int registrationYear) { this.registrationYear = registrationYear; }

    public String getStudentType() { return studentType; }
    public void setStudentType(String studentType) { this.studentType = studentType; }

}