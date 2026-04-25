package com.fot.system.controller;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.AttendanceService;

import java.util.List;

/**
 * coordinate attendance session actions between view and service layers
 * @author poornika
 */
public class AttendanceSessionController {

    private final AttendanceService attendanceService;

    /**
     * initialize attendance session controller
     * @author poornika
     */
    public AttendanceSessionController() {
        this.attendanceService = new AttendanceService();
    }

    /**
     * create attendance session for lecturer flow
     * @param request add session payload
     * @param lecturerId lecturer user id
     * @author poornika
     */
    public AttendanceSessionRow createSession(AddAttendanceSessionRequest request, int lecturerId) {
        if (request == null) {
            throw new RuntimeException("Attendance session request cannot be null.");
        }
        requireValue(request.getCourseId(), "Course is required.");
        requireValue(request.getTimetableSessionId(), "Timetable session is required.");
        requireValue(request.getSessionDate(), "Session date is required.");
        if (lecturerId <= 0) {
            throw new RuntimeException("Invalid lecturer.");
        }
        return attendanceService.addSession(request, lecturerId);
    }

    /**
     * create attendance session for TO flow
     * @param request add session payload
     * @author methum
     */
    public AttendanceSessionRow createSessionForTo(AddAttendanceSessionRequest request) {
        if (request == null) {
            throw new RuntimeException("Attendance session request cannot be null.");
        }
        requireValue(request.getCourseId(), "Course is required.");
        requireValue(request.getTimetableSessionId(), "Timetable session is required.");
        requireValue(request.getSessionDate(), "Session date is required.");
        return attendanceService.addSessionForTo(request);
    }

    /**
     * validate and save attendance marks for one session
     * @param sessionId session id
     * @param markedBy marker user id
     * @param updates attendance updates
     * @author poornika
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
     * validate required string values
     * @param value field value
     * @param message validation message
     * @author poornika
     */
    private void requireValue(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
    }
}
