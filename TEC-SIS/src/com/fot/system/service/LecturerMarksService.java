package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.repository.LecturerMarksRepository;

import java.util.List;

public class LecturerMarksService {

    private final LecturerMarksRepository lecturerMarksRepository;

    public LecturerMarksService() {
        this.lecturerMarksRepository = new LecturerMarksRepository();
    }

    public List<AssessmentCardSummary> getQuizCardSummaries(int courseId, int semesterYear, int quizCount) {
        return lecturerMarksRepository.findQuizCardSummaries(courseId, semesterYear, quizCount);
    }

    public List<AssessmentCardSummary> getAssignmentCardSummaries(int courseId, int semesterYear, int assignmentCount) {
        return lecturerMarksRepository.findAssignmentCardSummaries(courseId, semesterYear, assignmentCount);
    }

    public AssessmentCardSummary getMidExamSummary(int courseId, int semesterYear) {
        return lecturerMarksRepository.findMidExamSummary(courseId, semesterYear);
    }

    public AssessmentCardSummary getEndExamSummary(int courseId, int semesterYear) {
        return lecturerMarksRepository.findEndExamSummary(courseId, semesterYear);
    }

    /**
     * Gets editable student marks rows for the selected assessment item.
     * @param assessmentType type of the assessment
     * @param courseId selected course id
     * @param semesterYear selected semester year
     * @param itemNo assessment item number
     * @author janith
     */
    public List<AssessmentStudentMarkRow> getAssessmentRows(String assessmentType, int courseId, int semesterYear, int itemNo) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }

        return lecturerMarksRepository.findAssessmentRows(assessmentType, courseId, semesterYear, itemNo);
    }
    /**
     * Validates and saves lecturer-entered marks rows for the selected assessment item.
     * @param assessmentType type of the assessment
     * @param itemNo assessment item number
     * @param rows edited student marks rows
     * @author janith
     */
    public void saveAssessmentRows(String assessmentType, int itemNo, List<AssessmentStudentMarkRow> rows) {
        for (AssessmentStudentMarkRow row : rows) {
            String status = row.getStatus() == null ? "" : row.getStatus().trim();
            if (status.isEmpty() && row.getMark() == null) {
                continue;
            }

            boolean needsMark = "PRESENT".equalsIgnoreCase(status) || "SUBMITTED".equalsIgnoreCase(status);
            if (needsMark && row.getMark() == null) {
                throw new RuntimeException("Mark is required when status is " + status + ".");
            }

            if (row.getMark() != null && (row.getMark() < 0 || row.getMark() > 100)) {
                throw new RuntimeException("Marks must be between 0 and 100.");
            }
        }
        lecturerMarksRepository.saveAssessmentRows(assessmentType, itemNo, rows);
    }

}
