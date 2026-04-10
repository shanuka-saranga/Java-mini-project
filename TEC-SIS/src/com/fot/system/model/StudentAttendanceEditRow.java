package com.fot.system.model;

public class StudentAttendanceEditRow {
    private String registrationNo;
    private String studentName;
    private String attendanceStatus;
    private String medicalApprovalStatus;

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

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public String getMedicalApprovalStatus() {
        return medicalApprovalStatus;
    }

    public void setMedicalApprovalStatus(String medicalApprovalStatus) {
        this.medicalApprovalStatus = medicalApprovalStatus;
    }
}
