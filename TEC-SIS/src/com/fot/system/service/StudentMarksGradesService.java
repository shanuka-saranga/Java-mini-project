package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.repository.MarksRepository;
import com.fot.system.repository.UserRepository;
import com.fot.system.util.AcademicPerformance;

import java.util.List;
import java.util.stream.Collectors;

public class StudentMarksGradesService {

    private final MarksRepository marksRepository;
    private final UserRepository userRepository;
    private final AcademicPerformance academicPerformance;

    public StudentMarksGradesService() {
        this.marksRepository = new MarksRepository();
        this.userRepository = new UserRepository();
        this.academicPerformance = new AcademicPerformance();
    }

    public StudentMarksGradeViewData getStudentMarksGradeViewData(int studentUserId) {
        if (studentUserId <= 0) {
            throw new RuntimeException("Invalid student user ID.");
        }

        List<StudentCourseGradeRecord> gradeRecords = marksRepository.findLatestGradeRecordsByStudentUserId(studentUserId);
        List<StudentSubjectGradeRow> rows = gradeRecords.stream()
                .map(this::buildRow)
                .collect(Collectors.toList());

        int currentSemesterYear = rows.stream()
                .mapToInt(StudentSubjectGradeRow::getSemesterYear)
                .max()
                .orElse(0);

        double currentSgpa = academicPerformance.calculateSGpa(
                rows.stream()
                        .filter(row -> row.getSemesterYear() == currentSemesterYear)
                        .map(StudentSubjectGradeRow::getGrade)
                        .filter(grade -> grade != null && !"NOT ELIGIBLE".equalsIgnoreCase(grade))
                        .collect(Collectors.toList()),
                rows.stream()
                        .filter(row -> row.getSemesterYear() == currentSemesterYear)
                        .filter(row -> row.getGrade() != null && !"NOT ELIGIBLE".equalsIgnoreCase(row.getGrade()))
                        .map(StudentSubjectGradeRow::getCredits)
                        .collect(Collectors.toList())
        );

        User user = userRepository.findById(studentUserId);
        String registrationNo = user instanceof Student ? ((Student) user).getRegistrationNo() : null;
        List<StudentCoursePerformance> snapshots = registrationNo == null
                ? List.of()
                : marksRepository.findAllSnapshotsByStudent(registrationNo);
        double currentCgpa = academicPerformance.calculateGpa(snapshots);

        StudentMarksGradeViewData viewData = new StudentMarksGradeViewData();
        viewData.setRows(rows);
        viewData.setCurrentSemesterYear(currentSemesterYear);
        viewData.setCurrentSgpa(currentSgpa);
        viewData.setCurrentCgpa(currentCgpa);
        return viewData;
    }

    private StudentSubjectGradeRow buildRow(StudentCourseGradeRecord record) {
        StudentSubjectGradeRow row = new StudentSubjectGradeRow();
        row.setCourseCode(record.getCourseCode());
        row.setCourseName(record.getCourseName());
        row.setSemesterYear(record.getSemesterYear());
        row.setCredits(record.getCredits());

        double caAverage = academicPerformance.calculateCaAverage(record);
        double endExamAverage = academicPerformance.calculateEndExamAverage(record);
        double attendancePercentage = 100.0;
        double finalMark = academicPerformance.calculateFinalMark(record.getSessionType(), caAverage, endExamAverage);
        String grade = academicPerformance.resolveGrade(finalMark);

        row.setAttendancePercentage(attendancePercentage);
        row.setCaAverage(caAverage);
        row.setEndExamAverage(endExamAverage);
        row.setFinalMark(finalMark);
        row.setGrade(grade);
        row.setGradePoint(academicPerformance.resolveGradePoint(grade));
        return row;
    }
}
