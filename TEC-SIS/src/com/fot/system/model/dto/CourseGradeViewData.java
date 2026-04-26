package com.fot.system.model.dto;

import com.fot.system.model.entity.*;

import java.util.List;

public class CourseGradeViewData {
    private List<StudentGradeRow> rows;
    private List<Integer> registrationYears;

    public List<StudentGradeRow> getRows() {
        return rows;
    }

    public void setRows(List<StudentGradeRow> rows) {
        this.rows = rows;
    }

    public List<Integer> getRegistrationYears() {
        return registrationYears;
    }

    public void setRegistrationYears(List<Integer> registrationYears) {
        this.registrationYears = registrationYears;
    }
}
