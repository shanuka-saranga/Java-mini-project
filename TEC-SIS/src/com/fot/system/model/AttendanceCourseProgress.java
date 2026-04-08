package com.fot.system.model;

public class AttendanceCourseProgress {
    private int heldHours;
    private int totalHours;
    private double progressPercentage;

    public int getHeldHours() {
        return heldHours;
    }

    public void setHeldHours(int heldHours) {
        this.heldHours = heldHours;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }

    public double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
}
