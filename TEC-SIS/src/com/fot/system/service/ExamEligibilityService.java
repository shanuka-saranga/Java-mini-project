package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.repository.MarksRepository;

import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExamEligibilityService {

    private final AttendanceService attendanceService;
    private final MarksRepository marksRepository;

    public ExamEligibilityService() {
        this.attendanceService = new AttendanceService();
        this.marksRepository = new MarksRepository();
    }

    public CourseExamEligibilityViewData getCourseExamEligibilityViewData(int courseId, int totalCourseHours) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }
        if (totalCourseHours < 0) {
            throw new RuntimeException("Invalid total course hours.");
        }

        int currentYear = Year.now().getValue();
        CourseAttendanceViewData attendanceViewData = attendanceService.getCourseAttendanceViewData(courseId, totalCourseHours);
        List<StudentCourseCaRecord> caRecords = marksRepository.findStudentCourseCaRecords(courseId, currentYear);

        Map<String, StudentAttendanceSummaryRow> attendanceMap = attendanceViewData.getStudentSummaryRows().stream()
                .collect(Collectors.toMap(StudentAttendanceSummaryRow::getRegistrationNo, Function.identity()));

        List<ExamEligibilityRow> rows = new ArrayList<>();
        for (StudentCourseCaRecord caRecord : caRecords) {
            StudentAttendanceSummaryRow attendanceSummary = attendanceMap.get(caRecord.getRegistrationNo());
            double attendancePercentage = attendanceSummary == null ? 0 : attendanceSummary.getAttendancePercentage();
            double caAverage = calculateCaAverage(caRecord);
            boolean attendanceEligible = attendancePercentage >= 80.0;
            boolean caEligible = caAverage > 50.0;

            ExamEligibilityRow row = new ExamEligibilityRow();
            row.setRegistrationNo(caRecord.getRegistrationNo());
            row.setStudentName(caRecord.getStudentName());
            row.setRegistrationYear(caRecord.getRegistrationYear());
            row.setAttendancePercentage(attendancePercentage);
            row.setCaAverage(caAverage);
            row.setAttendanceEligible(attendanceEligible);
            row.setCaEligible(caEligible);
            row.setEligible(attendanceEligible && caEligible);
            rows.add(row);
        }

        CourseExamEligibilityViewData viewData = new CourseExamEligibilityViewData();
        viewData.setRows(rows);
        viewData.setRegistrationYears(rows.stream()
                .map(ExamEligibilityRow::getRegistrationYear)
                .filter(year -> year > 0)
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .stream()
                .sorted()
                .toList());
        viewData.setBatchSummary(buildBatchSummary(rows));
        return viewData;
    }

    private double calculateCaAverage(StudentCourseCaRecord caRecord) {
        int totalComponents = caRecord.getQuizCount() + caRecord.getAssignmentCount() + caRecord.getMidExamCount();
        if (totalComponents <= 0) {
            return 0;
        }
        double totalScore = caRecord.getQuizTotal() + caRecord.getAssignmentTotal() + caRecord.getMidExamTotal();
        return totalScore / totalComponents;
    }

    private ExamEligibilityBatchSummary buildBatchSummary(List<ExamEligibilityRow> rows) {
        ExamEligibilityBatchSummary summary = new ExamEligibilityBatchSummary();
        summary.setTotalStudents(rows.size());
        int eligibleCount = (int) rows.stream().filter(ExamEligibilityRow::isEligible).count();
        summary.setEligibleCount(eligibleCount);
        summary.setNotEligibleCount(rows.size() - eligibleCount);
        return summary;
    }
}
