package com.fot.system.model.dto;

public class StudentCoursePerformance {
    private int courseId;
    private String courseCode;
    private int credits;
    private String sessionType;
    private double caTotal;
    private double endExamMarks;
    private double quizTotal;
    private double assignmentTotal;
    private double midExamTotal;
    private double endExamTotal;
    private int quizPresentCount;
    private Double quizLowestPresentMark;
    private int assignmentSubmittedCount;
    private int midExamPresentCount;
    private int endExamPresentCount;
    private int quizCount;
    private int assignmentCount;
    private int midExamCount;
    private int endExamCount;

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

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
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

    public double getQuizTotal() {
        return quizTotal;
    }

    public void setQuizTotal(double quizTotal) {
        this.quizTotal = quizTotal;
    }

    public double getAssignmentTotal() {
        return assignmentTotal;
    }

    public void setAssignmentTotal(double assignmentTotal) {
        this.assignmentTotal = assignmentTotal;
    }

    public double getMidExamTotal() {
        return midExamTotal;
    }

    public void setMidExamTotal(double midExamTotal) {
        this.midExamTotal = midExamTotal;
    }

    public double getEndExamTotal() {
        return endExamTotal;
    }

    public void setEndExamTotal(double endExamTotal) {
        this.endExamTotal = endExamTotal;
    }

    public int getQuizPresentCount() {
        return quizPresentCount;
    }

    public void setQuizPresentCount(int quizPresentCount) {
        this.quizPresentCount = quizPresentCount;
    }

    public Double getQuizLowestPresentMark() {
        return quizLowestPresentMark;
    }

    public void setQuizLowestPresentMark(Double quizLowestPresentMark) {
        this.quizLowestPresentMark = quizLowestPresentMark;
    }

    public int getAssignmentSubmittedCount() {
        return assignmentSubmittedCount;
    }

    public void setAssignmentSubmittedCount(int assignmentSubmittedCount) {
        this.assignmentSubmittedCount = assignmentSubmittedCount;
    }

    public int getMidExamPresentCount() {
        return midExamPresentCount;
    }

    public void setMidExamPresentCount(int midExamPresentCount) {
        this.midExamPresentCount = midExamPresentCount;
    }

    public int getEndExamPresentCount() {
        return endExamPresentCount;
    }

    public void setEndExamPresentCount(int endExamPresentCount) {
        this.endExamPresentCount = endExamPresentCount;
    }

    public int getQuizCount() {
        return quizCount;
    }

    public void setQuizCount(int quizCount) {
        this.quizCount = quizCount;
    }

    public int getAssignmentCount() {
        return assignmentCount;
    }

    public void setAssignmentCount(int assignmentCount) {
        this.assignmentCount = assignmentCount;
    }

    public int getMidExamCount() {
        return midExamCount;
    }

    public void setMidExamCount(int midExamCount) {
        this.midExamCount = midExamCount;
    }

    public int getEndExamCount() {
        return endExamCount;
    }

    public void setEndExamCount(int endExamCount) {
        this.endExamCount = endExamCount;
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
