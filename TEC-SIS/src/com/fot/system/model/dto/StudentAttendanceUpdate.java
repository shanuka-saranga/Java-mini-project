package com.fot.system.model.dto;


/**
 * carry one student attendance status update command
 * @author poornika
 */
public class StudentAttendanceUpdate {
    private final String registrationNo;
    private final String attendanceStatus;

    /**
     * create immutable attendance update payload
     * @param registrationNo student registration number
     * @param attendanceStatus attendance status
     * @author poornika
     */
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
