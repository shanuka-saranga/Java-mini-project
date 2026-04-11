package com.fot.system.model.dto;

import com.fot.system.model.entity.*;

import java.util.List;

public class StudentMarksGradeViewData {
    private List<StudentSubjectGradeRow> rows;
    private int currentSemesterYear;
    private double currentSgpa;
    private double currentCgpa;

    public List<StudentSubjectGradeRow> getRows() {
        return rows;
    }

    public void setRows(List<StudentSubjectGradeRow> rows) {
        this.rows = rows;
    }

    public int getCurrentSemesterYear() {
        return currentSemesterYear;
    }

    public void setCurrentSemesterYear(int currentSemesterYear) {
        this.currentSemesterYear = currentSemesterYear;
    }

    public double getCurrentSgpa() {
        return currentSgpa;
    }

    public void setCurrentSgpa(double currentSgpa) {
        this.currentSgpa = currentSgpa;
    }

    public double getCurrentCgpa() {
        return currentCgpa;
    }

    public void setCurrentCgpa(double currentCgpa) {
        this.currentCgpa = currentCgpa;
    }
}
