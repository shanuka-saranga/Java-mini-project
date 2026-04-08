package com.fot.system.model;

public class StudentMarksOverviewRow {
    private String registrationNo;
    private String studentType;
    private int attemptNo;
    private int quizzesCompleted;
    private int assignmentsCompleted;
    private String midTheoryStatus;
    private String midPracticalStatus;
    private String endTheoryStatus;
    private String endPracticalStatus;

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getStudentType() {
        return studentType;
    }

    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }

    public int getAttemptNo() {
        return attemptNo;
    }

    public void setAttemptNo(int attemptNo) {
        this.attemptNo = attemptNo;
    }

    public int getQuizzesCompleted() {
        return quizzesCompleted;
    }

    public void setQuizzesCompleted(int quizzesCompleted) {
        this.quizzesCompleted = quizzesCompleted;
    }

    public int getAssignmentsCompleted() {
        return assignmentsCompleted;
    }

    public void setAssignmentsCompleted(int assignmentsCompleted) {
        this.assignmentsCompleted = assignmentsCompleted;
    }

    public String getMidTheoryStatus() {
        return midTheoryStatus;
    }

    public void setMidTheoryStatus(String midTheoryStatus) {
        this.midTheoryStatus = midTheoryStatus;
    }

    public String getMidPracticalStatus() {
        return midPracticalStatus;
    }

    public void setMidPracticalStatus(String midPracticalStatus) {
        this.midPracticalStatus = midPracticalStatus;
    }

    public String getEndTheoryStatus() {
        return endTheoryStatus;
    }

    public void setEndTheoryStatus(String endTheoryStatus) {
        this.endTheoryStatus = endTheoryStatus;
    }

    public String getEndPracticalStatus() {
        return endPracticalStatus;
    }

    public void setEndPracticalStatus(String endPracticalStatus) {
        this.endPracticalStatus = endPracticalStatus;
    }
}
