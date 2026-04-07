package com.fot.system.model;

public class EditCourseRequest extends AddCourseRequest {
    private final int courseId;

    public EditCourseRequest(int courseId, String courseCode, String courseName, String credits, String totalHours,
                             String sessionType, String departmentId, String lecturerInChargeId) {
        super(courseCode, courseName, credits, totalHours, sessionType, departmentId, lecturerInChargeId);
        this.courseId = courseId;
    }

    public int getCourseId() {
        return courseId;
    }
}
