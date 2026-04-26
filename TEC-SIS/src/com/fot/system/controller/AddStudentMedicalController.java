package com.fot.system.controller;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.StudentMedicalService;

import java.util.List;

public class AddStudentMedicalController {

    private final StudentMedicalService studentMedicalService;

    public AddStudentMedicalController() {
        this.studentMedicalService = new StudentMedicalService();
    }

    public List<AbsentSessionOption> loadAbsentSessions(int studentUserId, String startDate, String endDate) {
        validateDateInputs(studentUserId, startDate, endDate);
        return studentMedicalService.getAbsentSessionsForPeriod(studentUserId, startDate, endDate);
    }

    public void submitMedical(AddStudentMedicalRequest request) {
        if (request == null) {
            throw new RuntimeException("Medical request cannot be null.");
        }
        validateDateInputs(request.getStudentUserId(), request.getStartDate(), request.getEndDate());
        if (request.getFilePath() == null || request.getFilePath().trim().isEmpty()) {
            throw new RuntimeException("Medical certificate is required.");
        }
        studentMedicalService.submitMedical(request);
    }

    private void validateDateInputs(int studentUserId, String startDate, String endDate) {
        if (studentUserId <= 0) {
            throw new RuntimeException("Invalid student account.");
        }
        requireValue(startDate, "Medical start date is required.");
        requireValue(endDate, "Medical end date is required.");
    }

    private void requireValue(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
    }
}
