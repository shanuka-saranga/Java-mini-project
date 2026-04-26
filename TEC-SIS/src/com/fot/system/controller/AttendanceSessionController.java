package com.fot.system.controller;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.AttendanceService;

import java.util.List;

/**
 * Coordinates attendance session actions between the attendance views and service layer.
 * @author methum
 */
public class AttendanceSessionController {

    private final AttendanceService attendanceService;

    /**
     * Initializes the attendance session controller.
     * @author methum
     */
    public AttendanceSessionController() {
        this.attendanceService = new AttendanceService();
    }

    /**
     * Creates an attendance session for the lecturer flow.
     * @param request add session payload
     * @param lecturerId lecturer user id
     * @author methum
     */
    public AttendanceSessionRow createSession(AddAttendanceSessionRequest request, int lecturerId) {
        if (request == null) {
            throw new RuntimeException("Attendance session request cannot be null.");
        }
        validateSessionRequest(request);
        if (lecturerId <= 0) {
            throw new RuntimeException("Invalid lecturer.");
        }
        return attendanceService.addSession(request, lecturerId);
    }

    /**
     * Creates an attendance session for the TO flow.
     * @param request add session payload
     * @author methum
     */
    public AttendanceSessionRow createSessionForTo(AddAttendanceSessionRequest request) {
        if (request == null) {
            throw new RuntimeException("Attendance session request cannot be null.");
        }
        validateSessionRequest(request);
        return attendanceService.addSessionForTo(request);
    }

    /**
     * Validates and saves attendance marks for one session.
     * @param sessionId session id
     * @param markedBy marker user id
     * @param updates attendance updates
     * @author methum
     */
    public void saveAttendance(int sessionId, int markedBy, List<StudentAttendanceUpdate> updates) {
        if (sessionId <= 0) {
            throw new RuntimeException("Invalid session.");
        }
        if (markedBy <= 0) {
            throw new RuntimeException("Invalid marker.");
        }
        if (updates == null || updates.isEmpty()) {
            throw new RuntimeException("Attendance rows are required.");
        }
        attendanceService.saveSessionAttendance(sessionId, markedBy, updates);
    }

    /**
     * Validates required string values in the add-session request.
     * @param request add-session request
     * @author methum
     */
    private void validateSessionRequest(AddAttendanceSessionRequest request) {
        requireValue(request.getCourseId(), "Course is required.");
        requireValue(request.getTimetableSessionId(), "Timetable session is required.");
        requireValue(request.getSessionDate(), "Session date is required.");
    }

    /**
     * Validates required string values.
     * @param value field value
     * @param message validation message
     * @author methum
     */
    private void requireValue(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
    }
}
