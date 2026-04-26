package com.fot.system.model.dto;

import com.fot.system.model.entity.*;

public class StudentGradeRow {
    private String registrationNo;
    private String studentName;
    private int registrationYear;
    private double attendancePercentage;
    private double caAverage;
    private double endExamAverage;
    private Double finalMark;
    private String grade;

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getRegistrationYear() {
        return registrationYear;
    }

    public void setRegistrationYear(int registrationYear) {
        this.registrationYear = registrationYear;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public double getCaAverage() {
        return caAverage;
    }

    public void setCaAverage(double caAverage) {
        this.caAverage = caAverage;
    }

    public double getEndExamAverage() {
        return endExamAverage;
    }

    public void setEndExamAverage(double endExamAverage) {
        this.endExamAverage = endExamAverage;
    }

    public Double getFinalMark() {
        return finalMark;
    }

    public void setFinalMark(Double finalMark) {
        this.finalMark = finalMark;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
