package com.fot.system.util;

import com.fot.system.model.StudentCoursePerformance;
import com.fot.system.model.StudentCourseCaRecord;
import com.fot.system.model.StudentCourseGradeRecord;

import java.util.List;

public class AcademicPerformance {

    public double calculateCaAverage(StudentCourseCaRecord record) {
        int totalComponents = record.getQuizCount() + record.getAssignmentCount() + record.getMidExamCount();
        if (totalComponents <= 0) {
            return 0;
        }
        return (record.getQuizTotal() + record.getAssignmentTotal() + record.getMidExamTotal()) / totalComponents;
    }

    public double calculateCaAverage(StudentCourseGradeRecord record) {
        int totalComponents = record.getQuizCount() + record.getAssignmentCount() + record.getMidExamCount();
        if (totalComponents <= 0) {
            return 0;
        }
        return (record.getQuizTotal() + record.getAssignmentTotal() + record.getMidExamTotal()) / totalComponents;
    }

    public double calculateEndExamAverage(StudentCourseGradeRecord record) {
        if (record.getEndExamCount() <= 0) {
            return 0;
        }
        return record.getEndExamTotal() / record.getEndExamCount();
    }

    public boolean isAttendanceEligible(double attendancePercentage) {
        return attendancePercentage >= 80.0;
    }

    public boolean isCaEligible(double caAverage) {
        return caAverage > 50.0;
    }

    public boolean isExamEligible(double attendancePercentage, double caAverage) {
        return isAttendanceEligible(attendancePercentage) && isCaEligible(caAverage);
    }

    public double calculateFinalMark(String sessionType, double caAverage, double endExamAverage) {
        if ("THEORY".equalsIgnoreCase(sessionType) || "BOTH".equalsIgnoreCase(sessionType)) {
            return (caAverage * 0.30) + (endExamAverage * 0.70);
        }
        return (caAverage * 0.40) + (endExamAverage * 0.60);
    }

    public String resolveGrade(double finalMark) {
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

    public double resolveGradePoint(String grade) {
        return switch (grade) {
            case "A+" -> 4.00;
            case "A" -> 4.00;
            case "A-" -> 3.70;
            case "B+" -> 3.30;
            case "B" -> 3.00;
            case "B-" -> 2.70;
            case "C+" -> 2.30;
            case "C" -> 2.00;
            case "C-" -> 1.70;
            case "D" -> 1.00;
            default -> 0.00;
        };
    }

    public double calculateGpa(List<StudentCoursePerformance> courseSnapshots) {
        double weightedPoints = 0;
        double totalCredits = 0;

        for (StudentCoursePerformance snapshot : courseSnapshots) {
            if (snapshot.getAttendancePercentage() < 80.0) continue;

            double finalMark = calculateFinalMark(snapshot.getSessionType(), snapshot.getCaMarks(), snapshot.getEndExamMarks());
            String grade = resolveGrade(finalMark);
            double gradePoint = resolveGradePoint(grade);

            weightedPoints += gradePoint * snapshot.getCredits();
            totalCredits += snapshot.getCredits();
        }

        return (totalCredits <= 0) ? 0.00 : weightedPoints / totalCredits;
    }

    public double calculateSGpa(List<String> grades, List<Integer> credits) {
        if (grades == null || credits == null || grades.size() != credits.size() || grades.isEmpty()) {
            return 0.00;
        }
        double weightedPoints = 0;
        double totalCredits = 0;

        for (int i = 0; i < grades.size(); i++) {
            String grade = grades.get(i);
            int credit = credits.get(i);

            double gradePoint = resolveGradePoint(grade);

            weightedPoints += (gradePoint * credit);
            totalCredits += credit;
        }

        return (totalCredits <= 0) ? 0.00 : weightedPoints / totalCredits;
    }
}
