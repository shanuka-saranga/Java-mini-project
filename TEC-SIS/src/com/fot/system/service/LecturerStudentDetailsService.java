package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.repository.MarksRepository;
import com.fot.system.repository.StudentRepository;
import com.fot.system.util.AcademicPerformance;

import java.util.*;

public class LecturerStudentDetailsService {
    private static final double FINAL_PASS_MARK = 40.0;

    private final StudentRepository studentRepo;
    private final MarksRepository marksRepository;
    private final AcademicPerformance perfService;

    public LecturerStudentDetailsService() {
        this.studentRepo = new StudentRepository();
        this.marksRepository = new MarksRepository();
        this.perfService = new AcademicPerformance();
    }

    public List<StudentDetailsRow> getLectureViewStudentDetails() {
        List<StudentDetailsRow> detailsRows = new ArrayList<>();

        Map<String, Student> studentsByRegNo = new LinkedHashMap<>();
        for (Student student : studentRepo.findAllStudents()) {
            studentsByRegNo.put(student.getRegistrationNo(), student);
        }

        Map<String, List<StudentCourseGradeRecord>> recordsByRegNo = new LinkedHashMap<>();
        for (StudentCourseGradeRecord record : marksRepository.findAllLatestGradeRecords()) {
            recordsByRegNo.computeIfAbsent(record.getRegistrationNo(), key -> new ArrayList<>()).add(record);
        }

        for (Student student : studentsByRegNo.values()) {
            List<StudentCourseGradeRecord> studentRecords = recordsByRegNo.getOrDefault(student.getRegistrationNo(), List.of());
            double sgpa = calculateCurrentSgpa(studentRecords);
            double cgpa = calculateCumulativeGpa(studentRecords);
            StudentDetailsRow row = new StudentDetailsRow();
            row.setRegNo(student.getRegistrationNo());
            row.setRegistrationYear(student.getRegistrationYear());
            row.setStudentType(student.getStudentType());
            row.setFirstName(student.getFirstName());
            row.setLastName(student.getLastName());
            row.setEmail(student.getEmail());
            row.setPhone(student.getPhone());
            row.setAddress(student.getAddress());
            row.setSgpa(sgpa);
            row.setCgpa(cgpa);
            detailsRows.add(row);
        }

        return detailsRows;
    }

    private double calculateCurrentSgpa(List<StudentCourseGradeRecord> studentRecords) {
        if (studentRecords == null || studentRecords.isEmpty()) {
            return 0.0;
        }

        int currentSemesterYear = studentRecords.stream()
                .mapToInt(StudentCourseGradeRecord::getSemesterYear)
                .max()
                .orElse(0);

        List<String> grades = new ArrayList<>();
        List<Integer> credits = new ArrayList<>();
        for (StudentCourseGradeRecord record : studentRecords) {
            if (record.getSemesterYear() != currentSemesterYear) {
                continue;
            }
            grades.add(resolveCourseGrade(record));
            credits.add(record.getCredits() > 0 ? record.getCredits() : 3);
        }

        return perfService.calculateSGpa(grades, credits);
    }

    private double calculateCumulativeGpa(List<StudentCourseGradeRecord> studentRecords) {
        if (studentRecords == null || studentRecords.isEmpty()) {
            return 0.0;
        }

        List<String> grades = new ArrayList<>();
        List<Integer> credits = new ArrayList<>();
        for (StudentCourseGradeRecord record : studentRecords) {
            if (!perfService.isCourseIncludedInCgpa(record.getCourseCode())) {
                continue;
            }
            grades.add(resolveCourseGrade(record));
            credits.add(record.getCredits() > 0 ? record.getCredits() : 3);
        }

        return perfService.calculateSGpa(grades, credits);
    }

    private String resolveCourseGrade(StudentCourseGradeRecord record) {
        double caAverage = perfService.calculateCaAverage(record);
        double endExamAverage = perfService.calculateEndExamAverage(record);

        String specialGrade = perfService.resolveSpecialGrade(record, caAverage, endExamAverage);
        if (specialGrade != null) {
            return specialGrade;
        }

        double finalMark = perfService.calculateFinalMark(record.getSessionType(), caAverage, endExamAverage);
        return finalMark < FINAL_PASS_MARK ? "E" : perfService.resolveGrade(finalMark);
    }
}
