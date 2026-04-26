package com.fot.system.model.dto;


/**
 * Carries one student attendance status update command.
 * @author methum
 */
public class StudentAttendanceUpdate {
    private final String registrationNo;
    private final String attendanceStatus;

    /**
     * Creates an immutable attendance update payload.
     * @param registrationNo student registration number
     * @param attendanceStatus attendance status
     * @author methum
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
