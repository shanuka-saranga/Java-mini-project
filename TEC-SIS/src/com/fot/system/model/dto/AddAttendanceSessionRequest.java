package com.fot.system.model.dto;

import com.fot.system.model.entity.*;

public class AddAttendanceSessionRequest {
    private final String courseId;
    private final String timetableSessionId;
    private final String sessionDate;

    public AddAttendanceSessionRequest(String courseId, String timetableSessionId, String sessionDate) {
        this.courseId = courseId;
        this.timetableSessionId = timetableSessionId;
        this.sessionDate = sessionDate;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTimetableSessionId() {
        return timetableSessionId;
    }

    public String getSessionDate() {
        return sessionDate;
    }
}
