package com.fot.system.model.dto;

import com.fot.system.model.entity.*;

/**
 * Carries the request data needed to create a new attendance session.
 * @author methum
 */
public class AddAttendanceSessionRequest {
    private final String courseId;
    private final String timetableSessionId;
    private final String sessionDate;

    /**
     * Creates an immutable add-session request payload.
     * @param courseId course id
     * @param timetableSessionId timetable session id
     * @param sessionDate session date
     * @author methum
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
