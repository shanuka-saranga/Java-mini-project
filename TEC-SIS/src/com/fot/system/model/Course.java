package com.fot.system.model;

public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private int credits;
    private int totalHours;
    private String sessionType;
    private int departmentId;
    private Integer lecturerInChargeId;

    public Course() {
    }

    public Course(int courseId, String courseCode, String courseName, int credits,
                  int totalHours, String sessionType, int departmentId, Integer lecturerInChargeId) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.totalHours = totalHours;
        this.sessionType = sessionType;
        this.departmentId = departmentId;
        this.lecturerInChargeId = lecturerInChargeId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getLecturerInChargeId() {
        return lecturerInChargeId;
    }

    public void setLecturerInChargeId(Integer lecturerInChargeId) {
        this.lecturerInChargeId = lecturerInChargeId;
    }
}