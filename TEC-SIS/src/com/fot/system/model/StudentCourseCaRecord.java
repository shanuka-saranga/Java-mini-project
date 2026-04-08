package com.fot.system.model;

public class StudentCourseCaRecord {
    private String registrationNo;
    private String studentName;
    private int registrationYear;
    private double quizTotal;
    private double assignmentTotal;
    private double midExamTotal;
    private int quizCount;
    private int assignmentCount;
    private int midExamCount;

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
}
