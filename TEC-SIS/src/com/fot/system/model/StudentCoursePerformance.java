package com.fot.system.model;

public class StudentCoursePerformance {
    private int courseId;
    private int credits;
    private String sessionType;
    private double caTotal;
    private double endExamMarks;

    private int presentCount;
    private int absentCount;
    private int medicalCount;
    private int totalSessions;
    private double attendancePercentage;

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public double getCaMarks(){
        return caTotal;
    }

    public void setCaMarks(double caTotal) {
        this.caTotal = caTotal;
    }

    public double getEndExamMarks() {
        return endExamMarks;
    }

    public void setEndExamMarks(double endExamAverage) {
        this.endExamMarks = endExamAverage;
    }

    // New Getters and Setters for Attendance Details
    public int getPresentCount() {
        return presentCount;
    }

    public void setPresentCount(int presentCount) {
        this.presentCount = presentCount;
    }

    public int getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(int absentCount) {
        this.absentCount = absentCount;
    }

    public int getMedicalCount() {
        return medicalCount;
    }

    public void setMedicalCount(int medicalCount) {
        this.medicalCount = medicalCount;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }


    @Override
    public String toString() {
        return String.format("Course: %d | CA: %.2f | End: %.2f | Att: %.2f%% (%d/%d)",
                courseId, caTotal, endExamMarks, attendancePercentage, presentCount, totalSessions);
    }
}