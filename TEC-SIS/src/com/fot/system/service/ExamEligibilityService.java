package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.repository.MarksRepository;
import com.fot.system.util.AcademicPerformance;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * handle business logic for lecturer exam eligibility calculations
 * @author poornika
 */
public class ExamEligibilityService {
    private static final double MINIMUM_ATTENDANCE_PERCENTAGE = 80.0;
    private static final double MINIMUM_CA_PERCENTAGE = 50.0;

    private final AttendanceService attendanceService;
    private final MarksRepository marksRepository;
    private final AcademicPerformance academicPerformance;

    /**
     * initialize exam eligibility service dependencies
     * @author poornika
     */
    public ExamEligibilityService() {
        this.attendanceService = new AttendanceService();
        this.marksRepository = new MarksRepository();
        this.academicPerformance = new AcademicPerformance();
    }

    /**
     * build exam eligibility view data using attendance and CA records
     * @param courseId selected course id
     * @param totalCourseHours configured course hours
     * @author poornika
     */
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

        List<StudentAttendanceSummaryRow> attendanceRows = attendanceViewData == null || attendanceViewData.getStudentSummaryRows() == null
                ? List.of()
                : attendanceViewData.getStudentSummaryRows();
        Map<String, StudentAttendanceSummaryRow> attendanceMap = attendanceRows.stream()
                .collect(Collectors.toMap(StudentAttendanceSummaryRow::getRegistrationNo, Function.identity()));

        List<ExamEligibilityRow> rows = new ArrayList<>(caRecords.size());
        TreeSet<Integer> registrationYears = new TreeSet<>();
        for (StudentCourseCaRecord caRecord : caRecords) {
            ExamEligibilityRow row = buildEligibilityRow(caRecord, attendanceMap.get(caRecord.getRegistrationNo()));
            rows.add(row);
            if (row.getRegistrationYear() > 0) {
                registrationYears.add(row.getRegistrationYear());
            }
        }

        CourseExamEligibilityViewData viewData = new CourseExamEligibilityViewData();
        viewData.setRows(rows);
        viewData.setRegistrationYears(new ArrayList<>(registrationYears));
        viewData.setBatchSummary(buildBatchSummary(rows));
        return viewData;
    }

    /**
     * Builds one exam eligibility row using CA and attendance values.
     * @param caRecord student CA record
     * @param attendanceSummary attendance summary for the same student
     * @author poornika
     */
    private ExamEligibilityRow buildEligibilityRow(
            StudentCourseCaRecord caRecord,
            StudentAttendanceSummaryRow attendanceSummary
    ) {
        double attendancePercentage = attendanceSummary == null ? 0 : attendanceSummary.getAttendancePercentage();
        double caAverage = academicPerformance.calculateCaAverage(caRecord);
        boolean attendanceEligible = attendancePercentage >= MINIMUM_ATTENDANCE_PERCENTAGE;
        boolean caEligible = caAverage >= MINIMUM_CA_PERCENTAGE;

        ExamEligibilityRow row = new ExamEligibilityRow();
        row.setRegistrationNo(caRecord.getRegistrationNo());
        row.setStudentName(caRecord.getStudentName());
        row.setRegistrationYear(caRecord.getRegistrationYear());
        row.setAttendancePercentage(attendancePercentage);
        row.setCaAverage(caAverage);
        row.setAttendanceEligible(attendanceEligible);
        row.setCaEligible(caEligible);
        row.setEligible(attendanceEligible && caEligible);
        return row;
    }

    /**
     * build batch summary counts for the eligibility table
     * @param rows student eligibility rows
     * @author poornika
     */
    private ExamEligibilityBatchSummary buildBatchSummary(List<ExamEligibilityRow> rows) {
        ExamEligibilityBatchSummary summary = new ExamEligibilityBatchSummary();
        summary.setTotalStudents(rows.size());
        int eligibleCount = (int) rows.stream().filter(ExamEligibilityRow::isEligible).count();
        summary.setEligibleCount(eligibleCount);
        summary.setNotEligibleCount(rows.size() - eligibleCount);
        return summary;
    }
}
