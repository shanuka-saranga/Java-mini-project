package com.fot.system.model;

public class StudentAttendanceUpdate {
    private final String registrationNo;
    private final String attendanceStatus;

    public StudentAttendanceUpdate(String registrationNo, String attendanceStatus) {
        this.registrationNo = registrationNo;
        this.attendanceStatus = attendanceStatus;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }
}
