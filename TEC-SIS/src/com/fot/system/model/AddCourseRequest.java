package com.fot.system.model;

public class AddCourseRequest {
    private final String courseCode;
    private final String courseName;
    private final String credits;
    private final String totalHours;
    private final String sessionType;
    private final String departmentId;
    private final String lecturerInChargeId;

    public AddCourseRequest(String courseCode, String courseName, String credits, String totalHours,
                            String sessionType, String departmentId, String lecturerInChargeId) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.totalHours = totalHours;
        this.sessionType = sessionType;
        this.departmentId = departmentId;
        this.lecturerInChargeId = lecturerInChargeId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCredits() {
        return credits;
    }

    public String getTotalHours() {
        return totalHours;
    }

    public String getSessionType() {
        return sessionType;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public String getLecturerInChargeId() {
        return lecturerInChargeId;
    }
}
