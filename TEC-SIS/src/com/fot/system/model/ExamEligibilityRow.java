package com.fot.system.model;

public class ExamEligibilityRow {
    private String registrationNo;
    private String studentName;
    private int registrationYear;
    private double attendancePercentage;
    private double caAverage;
    private boolean attendanceEligible;
    private boolean caEligible;
    private boolean eligible;

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

    public boolean isAttendanceEligible() {
        return attendanceEligible;
    }

    public void setAttendanceEligible(boolean attendanceEligible) {
        this.attendanceEligible = attendanceEligible;
    }

    public boolean isCaEligible() {
        return caEligible;
    }

    public void setCaEligible(boolean caEligible) {
        this.caEligible = caEligible;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }
}
