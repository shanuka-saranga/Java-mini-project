package com.fot.system.model;

import java.util.List;

public class CourseExamEligibilityViewData {
    private List<ExamEligibilityRow> rows;
    private List<Integer> registrationYears;
    private ExamEligibilityBatchSummary batchSummary;

    public List<ExamEligibilityRow> getRows() {
        return rows;
    }

    public void setRows(List<ExamEligibilityRow> rows) {
        this.rows = rows;
    }

    public List<Integer> getRegistrationYears() {
        return registrationYears;
    }

    public void setRegistrationYears(List<Integer> registrationYears) {
        this.registrationYears = registrationYears;
    }

    public ExamEligibilityBatchSummary getBatchSummary() {
        return batchSummary;
    }

    public void setBatchSummary(ExamEligibilityBatchSummary batchSummary) {
        this.batchSummary = batchSummary;
    }
}
