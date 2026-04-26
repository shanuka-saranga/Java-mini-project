package com.fot.system.model.dto;


/**
 * represent summarized attendance counters per student
 * @author poornika
 */
public class StudentAttendanceSummaryRow {
    private String registrationNo;
    private String studentName;
    private int presentCount;
    private int medicalCount;
    private int absentCount;
    private double attendancePercentage;
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

    public int getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(int presentCount) {
        this.presentCount = presentCount;
    }

    public int getMedicalCount() {
        return medicalCount;
    }

    public void setMedicalCount(int medicalCount) {
        this.medicalCount = medicalCount;
    }

    public int getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(int absentCount) {
        this.absentCount = absentCount;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }
}
