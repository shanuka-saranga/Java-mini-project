package com.fot.system.model;

public class CourseMarksOverview {
    private int totalStudentAttempts;
    private int quizAttemptsRecorded;
    private int assignmentSubmissionsRecorded;
    private int midExamAttemptsRecorded;
    private int endExamAttemptsRecorded;
    private double averageQuizMark;
    private double averageAssignmentMark;
    private double averageMidExamMark;
    private double averageEndExamMark;

    public int getTotalStudentAttempts() {
        return totalStudentAttempts;
    }

    public void setTotalStudentAttempts(int totalStudentAttempts) {
        this.totalStudentAttempts = totalStudentAttempts;
    }

    public int getQuizAttemptsRecorded() {
        return quizAttemptsRecorded;
    }

    public void setQuizAttemptsRecorded(int quizAttemptsRecorded) {
        this.quizAttemptsRecorded = quizAttemptsRecorded;
    }

    public int getAssignmentSubmissionsRecorded() {
        return assignmentSubmissionsRecorded;
    }

    public void setAssignmentSubmissionsRecorded(int assignmentSubmissionsRecorded) {
        this.assignmentSubmissionsRecorded = assignmentSubmissionsRecorded;
    }

    public int getMidExamAttemptsRecorded() {
        return midExamAttemptsRecorded;
    }

    public void setMidExamAttemptsRecorded(int midExamAttemptsRecorded) {
        this.midExamAttemptsRecorded = midExamAttemptsRecorded;
    }

    public int getEndExamAttemptsRecorded() {
        return endExamAttemptsRecorded;
    }

    public void setEndExamAttemptsRecorded(int endExamAttemptsRecorded) {
        this.endExamAttemptsRecorded = endExamAttemptsRecorded;
    }

    public double getAverageQuizMark() {
        return averageQuizMark;
    }

    public void setAverageQuizMark(double averageQuizMark) {
        this.averageQuizMark = averageQuizMark;
    }

    public double getAverageAssignmentMark() {
        return averageAssignmentMark;
    }

    public void setAverageAssignmentMark(double averageAssignmentMark) {
        this.averageAssignmentMark = averageAssignmentMark;
    }

    public double getAverageMidExamMark() {
        return averageMidExamMark;
    }

    public void setAverageMidExamMark(double averageMidExamMark) {
        this.averageMidExamMark = averageMidExamMark;
    }

    public double getAverageEndExamMark() {
        return averageEndExamMark;
    }

    public void setAverageEndExamMark(double averageEndExamMark) {
        this.averageEndExamMark = averageEndExamMark;
    }
}
