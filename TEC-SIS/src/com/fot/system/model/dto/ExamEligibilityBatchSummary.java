package com.fot.system.model.dto;

import com.fot.system.model.entity.*;

/**
 * store summary counts for course exam eligibility results
 * @author poornika
 */
public class ExamEligibilityBatchSummary {
    private int totalStudents;
    private int eligibleCount;
    private int notEligibleCount;

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getEligibleCount() {
        return eligibleCount;
    }

    public void setEligibleCount(int eligibleCount) {
        this.eligibleCount = eligibleCount;
    }

    public int getNotEligibleCount() {
        return notEligibleCount;
    }

    public void setNotEligibleCount(int notEligibleCount) {
        this.notEligibleCount = notEligibleCount;
    }
}
