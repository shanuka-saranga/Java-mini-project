package com.fot.system.model.dto;

import com.fot.system.model.entity.*;

/**
 * carry request data needed to create a new attendance session
 * @author poornika
 */
public class AddAttendanceSessionRequest {
    private final String courseId;
    private final String timetableSessionId;
    private final String sessionDate;

    /**
     * create immutable add-session request payload
     * @param courseId course id
     * @param timetableSessionId timetable session id
     * @param sessionDate session date
     * @author poornika
     */
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
