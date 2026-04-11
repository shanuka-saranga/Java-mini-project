package com.fot.system.model.dto;

import com.fot.system.model.entity.*;

public class StudentCourseGradeRecord {
    private int courseId;
    private String courseCode;
    private String courseName;
    private int credits;
    private int semesterYear;
    private String registrationNo;
    private String studentName;
    private int registrationYear;
    private String sessionType;
    private double quizTotal;
    private double assignmentTotal;
    private double midExamTotal;
    private double endExamTotal;
    private int quizCount;
    private int assignmentCount;
    private int midExamCount;
    private int endExamCount;

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

    public int getSemesterYear() {
        return semesterYear;
    }

    public void setSemesterYear(int semesterYear) {
        this.semesterYear = semesterYear;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getRegistrationYear() {
        return registrationYear;
    }

    public void setRegistrationYear(int registrationYear) {
        this.registrationYear = registrationYear;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
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
}
