package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.repository.MarksRepository;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LecturerGradesService {

    private final MarksRepository lecturerGradesRepository;
    private final AttendanceService attendanceService;

    public LecturerGradesService() {
        this.lecturerGradesRepository = new MarksRepository();
        this.attendanceService = new AttendanceService();
    }

    public CourseGradeViewData getCourseGradeViewData(int courseId, int totalCourseHours) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }

        int currentYear = Year.now().getValue();
        CourseAttendanceViewData attendanceViewData = attendanceService.getCourseAttendanceViewData(courseId, totalCourseHours);
        Map<String, StudentAttendanceSummaryRow> attendanceMap = attendanceViewData.getStudentSummaryRows().stream()
                .collect(Collectors.toMap(StudentAttendanceSummaryRow::getRegistrationNo, Function.identity()));

        List<StudentGradeRow> rows = lecturerGradesRepository.findStudentCourseGradeRecords(courseId, currentYear).stream()
                .map(record -> buildRow(record, attendanceMap.get(record.getRegistrationNo())))
                .toList();

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

    private StudentGradeRow buildRow(StudentCourseGradeRecord record, StudentAttendanceSummaryRow attendanceSummary) {
        double attendancePercentage = attendanceSummary == null ? 0 : attendanceSummary.getAttendancePercentage();
        double caAverage = calculateCaAverage(record);
        double endExamAverage = calculateEndExamAverage(record);
        boolean eligible = attendancePercentage >= 80.0 && caAverage > 50.0;

        StudentGradeRow row = new StudentGradeRow();
        row.setRegistrationNo(record.getRegistrationNo());
        row.setStudentName(record.getStudentName());
        row.setRegistrationYear(record.getRegistrationYear());
        row.setAttendancePercentage(attendancePercentage);
        row.setCaAverage(caAverage);
        row.setEndExamAverage(endExamAverage);

        if (eligible) {
            double finalMark = calculateFinalMark(record.getSessionType(), caAverage, endExamAverage);
            row.setFinalMark(finalMark);
            row.setGrade(resolveGrade(finalMark));
        } else {
            row.setFinalMark(null);
            row.setGrade("NOT ELIGIBLE");
        }

        return row;
    }

    private double calculateCaAverage(StudentCourseGradeRecord record) {
        int totalComponents = record.getQuizCount() + record.getAssignmentCount() + record.getMidExamCount();
        if (totalComponents <= 0) {
            return 0;
        }
        return (record.getQuizTotal() + record.getAssignmentTotal() + record.getMidExamTotal()) / totalComponents;
    }

    private double calculateEndExamAverage(StudentCourseGradeRecord record) {
        if (record.getEndExamCount() <= 0) {
            return 0;
        }
        return record.getEndExamTotal() / record.getEndExamCount();
    }

    private double calculateFinalMark(String sessionType, double caAverage, double endExamAverage) {
        if ("THEORY".equalsIgnoreCase(sessionType)) {
            return (caAverage * 0.30) + (endExamAverage * 0.70);
        }
        return (caAverage * 0.40) + (endExamAverage * 0.60);
    }

    private String resolveGrade(double finalMark) {
        if (finalMark >= 85) return "A+";
        if (finalMark >= 75) return "A";
        if (finalMark >= 70) return "A-";
        if (finalMark >= 65) return "B+";
        if (finalMark >= 60) return "B";
        if (finalMark >= 55) return "B-";
        if (finalMark >= 50) return "C+";
        if (finalMark >= 45) return "C";
        if (finalMark >= 40) return "C-";
        if (finalMark >= 35) return "D";
        return "E";
    }
}
