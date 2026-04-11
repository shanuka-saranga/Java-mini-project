package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.repository.MarksRepository;
import com.fot.system.repository.StudentRepository;
import com.fot.system.util.AcademicPerformance;

import java.util.*;

public class LecturerStudentDetailsService {
    private final StudentRepository studentRepo; // මෙහි නව getAllStudentsPerformance පවතී
    private final AcademicPerformance perfService;

    public LecturerStudentDetailsService() {
        this.studentRepo = new StudentRepository();
        this.perfService = new AcademicPerformance();
    }

    public List<StudentDetailsRow> getLectureViewStudentDetails() {
        List<StudentDetailsRow> detailsRows = new ArrayList<>();

        // Repository එකෙන් සියලුම ශිෂ්‍ය දත්ත (සහ ඔවුන්ගේ ලකුණු ලැයිස්තු) ලබා ගැනීම
        List<StudentsPerformance> allPerformanceData = studentRepo.getAllStudentsPerformance();

        for (StudentsPerformance studentRecord : allPerformanceData) {
            List<String> grades = new ArrayList<>();
            List<Integer> credits = new ArrayList<>();

            for (StudentsPerformance.CourseMarksAndAttendanceDetail detail : studentRecord.getCourseMarks()) {
                double caTotal = detail.getCaTotal();
                double finalMark = perfService.calculateFinalMark(detail.getSessionType(), caTotal, detail.getEndExamMark());
                String grade = perfService.resolveGrade(finalMark);

                grades.add(grade);
                credits.add(3);
            }

            double gpaValue = perfService.calculateSGpa(grades, credits);

            StudentDetailsRow row = new StudentDetailsRow();

            row.setRegNo(studentRecord.getRegistrationNo());
            row.setRegistrationYear(studentRecord.getRegistrationYear());
            row.setStudentType(studentRecord.getStudentType());
            row.setFirstName(studentRecord.getFirstName());
            row.setLastName(studentRecord.getLastName());
            row.setEmail(studentRecord.getEmail());
            row.setPhone(studentRecord.getPhone());
            row.setAddress(studentRecord.getAddress());

            // GPA අගයන් ඇතුළත් කිරීම
            row.setSgpa(gpaValue);
            row.setCgpa(gpaValue);

            // මෙම ශිෂ්‍යයාගේ පේළිය අවසාන ලැයිස්තුවට එක් කරන්න
            detailsRows.add(row);
        }

        return detailsRows;
    }

    private int extractYear(String regNo) {
        if (regNo == null || regNo.isEmpty()) return 0;
        try {
            String[] parts = regNo.split("/");
            if (parts.length > 1) {
                return Integer.parseInt(parts[1]);
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    public static void main(String[] args) {
        LecturerStudentDetailsService service = new LecturerStudentDetailsService();
        List<StudentDetailsRow> details = service.getLectureViewStudentDetails();
        details.stream().map(StudentDetailsRow::toString).forEach(System.out::println);


    }
}
