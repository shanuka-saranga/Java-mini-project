package com.fot.system.util;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import java.util.List;

public class AcademicPerformance {
    private static final double CA_MINIMUM_MARK = 30.0;
    private static final double END_MINIMUM_MARK = 30.0;

    public double calculateCaAverage(StudentCourseCaRecord record) {
        return averageComponentScores(
                calculateQuizAverageForConfiguredCount(
                        record.getQuizTotal(),
                        record.getQuizLowestPresentMark(),
                        record.getQuizPresentCount(),
                        record.getQuizCount()
                ),
                calculateAssignmentAverageForConfiguredCount(record.getAssignmentTotal(), record.getAssignmentCount()),
                calculateExamAverageForConfiguredCount(record.getMidExamTotal(), record.getMidExamCount())
        );
    }

    public double calculateCaAverage(StudentCourseGradeRecord record) {
        return averageComponentScores(
                calculateQuizAverageForConfiguredCount(
                        record.getQuizTotal(),
                        record.getQuizLowestPresentMark(),
                        record.getQuizPresentCount(),
                        record.getQuizCount()
                ),
                calculateAssignmentAverageForConfiguredCount(record.getAssignmentTotal(), record.getAssignmentCount()),
                calculateExamAverageForConfiguredCount(record.getMidExamTotal(), record.getMidExamCount())
        );
    }

    public double calculateEndExamAverage(StudentCourseGradeRecord record) {
        Double endExamAverage = calculateExamAverageForConfiguredCount(record.getEndExamTotal(), record.getEndExamCount());
        return endExamAverage == null ? 0 : endExamAverage;
    }


    public int getRequiredQuizCount(int configuredQuizCount) {
        if (configuredQuizCount <= 0) {
            return 0;
        }
        return configuredQuizCount == 1 ? 1 : configuredQuizCount - 1;
    }

    public int getConsideredQuizCount(int configuredQuizCount) {
        int requiredQuizCount = getRequiredQuizCount(configuredQuizCount);
        if (requiredQuizCount <= 0) {
            return 0;
        }
        return requiredQuizCount;
    }

    public double getAdjustedQuizTotal(
            double presentTotal,
            Double lowestPresentMark,
            int presentCount,
            int configuredQuizCount
    ) {
        boolean shouldDropLowest = configuredQuizCount > 1
                && presentCount >= configuredQuizCount
                && lowestPresentMark != null;
        return shouldDropLowest ? presentTotal - lowestPresentMark : presentTotal;
    }

    public int getConsideredAssignmentCount(int configuredAssignmentCount) {
        if (configuredAssignmentCount <= 0) {
            return 0;
        }
        return configuredAssignmentCount;
    }

    public int getConsideredExamCount(int requiredExamCount) {
        if (requiredExamCount <= 0) {
            return 0;
        }
        return requiredExamCount;
    }

    public Double calculateQuizAverageForConfiguredCount(
            double presentTotal,
            Double lowestPresentMark,
            int presentCount,
            int configuredQuizCount
    ) {
        int denominator = getConsideredQuizCount(configuredQuizCount);
        if (denominator <= 0) {
            return null;
        }
        return getAdjustedQuizTotal(presentTotal, lowestPresentMark, presentCount, configuredQuizCount) / denominator;
    }

    public Double calculateAssignmentAverageForConfiguredCount(double assignmentTotal, int configuredAssignmentCount) {
        int denominator = getConsideredAssignmentCount(configuredAssignmentCount);
        if (denominator <= 0) {
            return null;
        }
        return assignmentTotal / denominator;
    }

    public Double calculateExamAverageForConfiguredCount(double examTotal, int requiredExamCount) {
        int denominator = getConsideredExamCount(requiredExamCount);
        if (denominator <= 0) {
            return null;
        }
        return examTotal / denominator;
    }

    public double averageComponentScores(Double... componentScores) {
        double total = 0;
        int count = 0;
        for (Double score : componentScores) {
            if (score != null) {
                total += score;
                count++;
            }
        }
        return count == 0 ? 0 : total / count;
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

    public String resolveSpecialGrade(StudentCourseGradeRecord record, double caAverage, double endExamAverage) {
        if (record.getQuizMedicalCount() > 0
                || record.getAssignmentMedicalCount() > 0
                || record.getMidExamMedicalCount() > 0
                || record.getEndExamMedicalCount() > 0) {
            return "MC";
        }

        int requiredQuizCount = getRequiredQuizCount(record.getQuizCount());
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
