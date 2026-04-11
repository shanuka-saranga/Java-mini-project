package com.fot.system.controller;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.AttendanceService;

import java.util.List;

public class AttendanceSessionController {

    private final AttendanceService attendanceService;

    public AttendanceSessionController() {
        this.attendanceService = new AttendanceService();
    }

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

    public AttendanceSessionRow createSessionForTo(AddAttendanceSessionRequest request) {
        if (request == null) {
            throw new RuntimeException("Attendance session request cannot be null.");
        }
        requireValue(request.getCourseId(), "Course is required.");
        requireValue(request.getTimetableSessionId(), "Timetable session is required.");
        requireValue(request.getSessionDate(), "Session date is required.");
        return attendanceService.addSessionForTo(request);
    }

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

    private void requireValue(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
    }
}
