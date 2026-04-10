package com.fot.system.model;

public class StudentAttendance {
    private String regNo;
    private String studentName;
    private String courseCode;
    private int totalSessions;
    private int attendedSessions;
    private double attendancePercentage;

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public int getTotalSessions() { return totalSessions; }
    public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }
    public int getAttendedSessions() { return attendedSessions; }
    public void setAttendedSessions(int attendedSessions) { this.attendedSessions = attendedSessions; }
    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }

    @Override
    public String toString() {
        return String.format("| %-15s | %-25s | %-10s | %3d/%-3d | %6.2f%% |",
                regNo, studentName, courseCode, attendedSessions, totalSessions, attendancePercentage);
    }
}
