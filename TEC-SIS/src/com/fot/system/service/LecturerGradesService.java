package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.repository.MarksRepository;
import com.fot.system.util.AcademicPerformance;

import java.time.Year;
import java.util.List;

public class LecturerGradesService {
    private static final double CA_MINIMUM_MARK = 30.0;
    private static final double END_MINIMUM_MARK = 30.0;
    private static final double FINAL_PASS_MARK = 40.0;
    private static final boolean ENABLE_GRADE_FLOW_LOGS = true;

    private final MarksRepository lecturerGradesRepository;
    private final AcademicPerformance academicPerformance;

    public LecturerGradesService() {
        this.lecturerGradesRepository = new MarksRepository();
        this.academicPerformance = new AcademicPerformance();
    }

    public CourseGradeViewData getCourseGradeViewData(int courseId) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }

        int currentYear = Year.now().getValue();
        logGradeFlow("getCourseGradeViewData.start -> courseId=" + courseId + ", year=" + currentYear);
        List<StudentCourseGradeRecord> records = lecturerGradesRepository.findStudentCourseGradeRecords(courseId, currentYear);
        logGradeFlow("getCourseGradeViewData.rawRecords -> count=" + records.size());
        List<StudentGradeRow> rows = records.stream()
                .map(this::buildRow)
                .toList();
        logGradeFlow("getCourseGradeViewData.calculatedRows -> count=" + rows.size());

        CourseGradeViewData viewData = new CourseGradeViewData();
        viewData.setRows(rows);
        viewData.setRegistrationYears(rows.stream()
                .map(StudentGradeRow::getRegistrationYear)
                .filter(year -> year > 0)
                .distinct()
                .sorted()
                .toList());
        return viewData;
    }

    private StudentGradeRow buildRow(StudentCourseGradeRecord record) {
        double caAverage = calculateCaAverage(record);
        double endExamAverage = calculateEndExamAverage(record);

        StudentGradeRow row = new StudentGradeRow();
        row.setRegistrationNo(record.getRegistrationNo());
        row.setStudentName(record.getStudentName());
        row.setRegistrationYear(record.getRegistrationYear());
        row.setCaAverage(caAverage);
        row.setEndExamAverage(endExamAverage);

        String specialGrade = resolveSpecialGrade(record, caAverage, endExamAverage);
        if (specialGrade != null) {
            row.setFinalMark(null);
            row.setGrade(specialGrade);
            logGradeFlow("buildRow -> regNo=" + row.getRegistrationNo()
                    + ", ca=" + caAverage + ", end=" + endExamAverage + ", grade=" + specialGrade + ", final=-");
            return row;
        }

        double finalMark = academicPerformance.calculateFinalMark(record.getSessionType(), caAverage, endExamAverage);
        row.setFinalMark(finalMark);
        row.setGrade(finalMark < FINAL_PASS_MARK ? "E" : academicPerformance.resolveGrade(finalMark));
        logGradeFlow("buildRow -> regNo=" + row.getRegistrationNo()
                + ", ca=" + caAverage + ", end=" + endExamAverage + ", final=" + finalMark + ", grade=" + row.getGrade());
        return row;
    }

    private double calculateCaAverage(StudentCourseGradeRecord record) {
        return academicPerformance.calculateCaAverage(record);
    }

    private double calculateEndExamAverage(StudentCourseGradeRecord record) {
        return academicPerformance.calculateEndExamAverage(record);
    }

    private String resolveSpecialGrade(StudentCourseGradeRecord record, double caAverage, double endExamAverage) {
        if (record.getQuizMedicalCount() > 0
                || record.getAssignmentMedicalCount() > 0
                || record.getMidExamMedicalCount() > 0
                || record.getEndExamMedicalCount() > 0) {
            return "MC";
        }

        int requiredQuizCount = Math.max(1, record.getQuizCount() - 1);
        boolean caCompleteByCounts = record.getQuizPresentCount() >= requiredQuizCount
                && record.getAssignmentSubmittedCount() >= record.getAssignmentCount()
                && record.getMidExamPresentCount() >= record.getMidExamCount();
        boolean endCompleteByCounts = record.getEndExamPresentCount() >= record.getEndExamCount();

        boolean caFailOrIncomplete = record.getQuizIncompleteCount() > 0
                || record.getAssignmentIncompleteCount() > 0
                || record.getMidExamIncompleteCount() > 0
                || !caCompleteByCounts
                || caAverage < CA_MINIMUM_MARK;
        boolean endFailOrIncomplete = record.getEndExamIncompleteCount() > 0
                || !endCompleteByCounts
                || endExamAverage < END_MINIMUM_MARK;

        if (caFailOrIncomplete && endFailOrIncomplete) {
            return "E";
        }
        if (caFailOrIncomplete) {
            return "EC";
        }
        if (endFailOrIncomplete) {
            return "EE";
        }

        return null;
    }

    private void logGradeFlow(String message) {
        if (ENABLE_GRADE_FLOW_LOGS) {
            System.out.println("[GRADE-FLOW][SERVICE] " + message);
        }
    }
}
