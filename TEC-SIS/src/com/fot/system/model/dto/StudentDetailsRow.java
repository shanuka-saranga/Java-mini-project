package com.fot.system.model.dto;

import com.fot.system.model.entity.*;

public class StudentDetailsRow {
    // ශිෂ්‍ය තොරතුරු (Personal & System Details)
    private String regNo;
    private int registrationYear;
    private String studentType;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;

    // GPA තොරතුරු
    private double sgpa;
    private double cgpa;

    public StudentDetailsRow() {}

    // Getters and Setters
    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public int getRegistrationYear() { return registrationYear; }
    public void setRegistrationYear(int registrationYear) { this.registrationYear = registrationYear; }

    public String getStudentType() { return studentType; }
    public void setStudentType(String studentType) { this.studentType = studentType; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getSgpa() { return sgpa; }
    public void setSgpa(double sgpa) { this.sgpa = sgpa; }

    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
    @Override
    public String toString() {
        String fullName = getFullName();
        // සියලුම දත්ත (RegNo, Name, Year, Type, Email, Phone, Address, GPA) පිළිවෙළට
        return String.format("| %-12s | %-20s | %-5d | %-8s | %-25s | %-12s | %-15s | %5.2f | %5.2f |",
                regNo,
                (fullName.length() > 20 ? fullName.substring(0, 17) + "..." : fullName),
                registrationYear,
                studentType,
                (email != null && email.length() > 25 ? email.substring(0, 22) + "..." : email),
                phone,
                (address != null && address.length() > 15 ? address.substring(0, 12) + "..." : address),
                sgpa,
                cgpa);
    }

    public static String getTableHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("+--------------+----------------------+-------+----------+---------------------------+--------------+-----------------+-------+-------+\n");
        sb.append("| Reg No       | Student Name         | Year  | Type     | Email                     | Phone        | Address         | SGPA  | CGPA  |\n");
        sb.append("+--------------+----------------------+-------+----------+---------------------------+--------------+-----------------+-------+-------+");
        return sb.toString();
    }

    public static String getTableFooter() {
        return "+--------------+----------------------+-------+----------+---------------------------+--------------+-----------------+-------+-------+";
    }
}