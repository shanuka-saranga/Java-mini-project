package com.fot.system.model.dto;

import com.fot.system.model.entity.*;

import java.util.List;

public class StudentAttendanceMedicalViewData {
    private List<StudentSessionAttendanceRow> attendanceRows;
    private List<StudentMedicalRow> medicalRows;

    public List<StudentSessionAttendanceRow> getAttendanceRows() {
        return attendanceRows;
    }

    public void setAttendanceRows(List<StudentSessionAttendanceRow> attendanceRows) {
        this.attendanceRows = attendanceRows;
    }

    public List<StudentMedicalRow> getMedicalRows() {
        return medicalRows;
    }

    public void setMedicalRows(List<StudentMedicalRow> medicalRows) {
        this.medicalRows = medicalRows;
    }
}
