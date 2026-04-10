package com.fot.system.model;

public class StudentSubjectGradeRow {
    private String courseCode;
    private String courseName;
    private int semesterYear;
    private int credits;
    private double attendancePercentage;
    private double caAverage;
    private double endExamAverage;
    private Double finalMark;
    private String grade;
    private double gradePoint;

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

    public int getSemesterYear() {
        return semesterYear;
    }

    public void setSemesterYear(int semesterYear) {
        this.semesterYear = semesterYear;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public double getCaAverage() {
        return caAverage;
    }

    public void setCaAverage(double caAverage) {
        this.caAverage = caAverage;
    }

    public double getEndExamAverage() {
        return endExamAverage;
    }

    public void setEndExamAverage(double endExamAverage) {
        this.endExamAverage = endExamAverage;
    }

    public Double getFinalMark() {
        return finalMark;
    }

    public void setFinalMark(Double finalMark) {
        this.finalMark = finalMark;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public double getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(double gradePoint) {
        this.gradePoint = gradePoint;
    }
}
