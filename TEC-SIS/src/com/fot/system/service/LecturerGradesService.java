package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.repository.MarksRepository;
import com.fot.system.util.AcademicPerformance;

import java.time.Year;
import java.util.List;

public class LecturerGradesService {
    private static final double FINAL_PASS_MARK = 40.0;
    private static final boolean ENABLE_GRADE_FLOW_LOGS = true;

    private final MarksRepository lecturerGradesRepository;
    private final AcademicPerformance academicPerformance;

    public LecturerGradesService() {
        this.lecturerGradesRepository = new MarksRepository();
        this.academicPerformance = new AcademicPerformance();
    }

    /**
     * Builds the calculated grades view data for the selected lecturer course.
     * @param courseId selected course id
     * @author janith
     */
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

    /**
     * Builds one grade row by combining CA, end exam, special grade, and final grade rules.
     * @param record aggregated student grade record
     * @author janith
     */
    private StudentGradeRow buildRow(StudentCourseGradeRecord record) {
        double caAverage = academicPerformance.calculateCaAverage(record);
        double endExamAverage = academicPerformance.calculateEndExamAverage(record);

        StudentGradeRow row = new StudentGradeRow();
        row.setRegistrationNo(record.getRegistrationNo());
        row.setStudentName(record.getStudentName());
        row.setRegistrationYear(record.getRegistrationYear());
        row.setCaAverage(caAverage);
        row.setEndExamAverage(endExamAverage);

        String specialGrade = academicPerformance.resolveSpecialGrade(record, caAverage, endExamAverage);
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

    private void logGradeFlow(String message) {
        if (ENABLE_GRADE_FLOW_LOGS) {
            System.out.println("[GRADE-FLOW][SERVICE] " + message);
        }
    }
}
