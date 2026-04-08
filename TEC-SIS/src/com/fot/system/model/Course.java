package com.fot.system.model;

public class Course {
    private int id;
    private String courseCode;
    private String courseName;
    private int credits;
    private int totalHours;
    private String sessionType;
    private int noOfQuizzes;
    private int noOfAssignments;
    private int departmentId;
    private String departmentName;
    private Integer lecturerInChargeId;
    private String lecturerInChargeName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getNoOfQuizzes() {
        return noOfQuizzes;
    }

    public void setNoOfQuizzes(int noOfQuizzes) {
        this.noOfQuizzes = noOfQuizzes;
    }

    public int getNoOfAssignments() {
        return noOfAssignments;
    }

    public void setNoOfAssignments(int noOfAssignments) {
        this.noOfAssignments = noOfAssignments;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getLecturerInChargeId() {
        return lecturerInChargeId;
    }

    public void setLecturerInChargeId(Integer lecturerInChargeId) {
        this.lecturerInChargeId = lecturerInChargeId;
    }

    public String getLecturerInChargeName() {
        return lecturerInChargeName;
    }

    public void setLecturerInChargeName(String lecturerInChargeName) {
        this.lecturerInChargeName = lecturerInChargeName;
    }
}
