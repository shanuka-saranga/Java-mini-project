package com.fot.system.service;

import com.fot.system.model.AbsentSessionOption;
import com.fot.system.model.AddStudentMedicalRequest;
import com.fot.system.repository.AttendanceRepository;

import java.sql.Date;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class StudentMedicalService {

    private final AttendanceRepository attendanceRepository;
    private final MedicalDocumentStorageService medicalDocumentStorageService;

    public StudentMedicalService() {
        this.attendanceRepository = new AttendanceRepository();
        this.medicalDocumentStorageService = new MedicalDocumentStorageService();
    }

    public List<AbsentSessionOption> getAbsentSessionsForPeriod(int studentUserId, String startDate, String endDate) {
        Date parsedStartDate = parseDate(startDate, "Medical start date must be in yyyy-mm-dd format.");
        Date parsedEndDate = parseDate(endDate, "Medical end date must be in yyyy-mm-dd format.");
        validateDateOrder(parsedStartDate, parsedEndDate);
        return attendanceRepository.findAbsentSessionsForStudentByDateRange(studentUserId, parsedStartDate, parsedEndDate);
    }

    public void submitMedical(AddStudentMedicalRequest request) {
        if (request.getStudentUserId() <= 0) {
            throw new RuntimeException("Invalid student account.");
        }

        Date parsedStartDate = parseDate(request.getStartDate(), "Medical start date must be in yyyy-mm-dd format.");
        Date parsedEndDate = parseDate(request.getEndDate(), "Medical end date must be in yyyy-mm-dd format.");
        validateDateOrder(parsedStartDate, parsedEndDate);

        if (request.getSessionIds() == null || request.getSessionIds().isEmpty()) {
            throw new RuntimeException("Select at least one absent session.");
        }

        String registrationNo = attendanceRepository.findStudentRegistrationNoByUserId(request.getStudentUserId());
        if (registrationNo == null || registrationNo.trim().isEmpty()) {
            throw new RuntimeException("Student registration could not be found.");
        }

        String storedDocumentPath = medicalDocumentStorageService.saveMedicalDocument(
                request.getFilePath(),
                registrationNo,
                request.getStartDate() + "_" + request.getEndDate()
        );

        attendanceRepository.saveStudentMedicalSubmissions(
                request.getStudentUserId(),
                request.getSessionIds(),
                storedDocumentPath,
                new Date(System.currentTimeMillis())
        );
    }

    private Date parseDate(String value, String message) {
        try {
            return Date.valueOf(value == null ? "" : value.trim());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(message);
        }
    }

    private void validateDateOrder(Date startDate, Date endDate) {
        if (endDate.before(startDate)) {
            throw new RuntimeException("Medical end date cannot be before start date.");
        }
        long daysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
        if (daysBetween > 4) {
            throw new RuntimeException("Medical date range cannot be more than 5 days.");
        }
    }
}
