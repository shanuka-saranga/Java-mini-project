package com.fot.system.service;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.repository.LecturerMarksRepository;

import java.util.List;

public class LecturerMarksService {

    private final LecturerMarksRepository lecturerMarksRepository;

    public LecturerMarksService() {
        this.lecturerMarksRepository = new LecturerMarksRepository();
    }

    public CourseSemesterContext getCurrentSemesterContext(int courseId, int currentYear) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }
        return lecturerMarksRepository.findCurrentSemesterContext(courseId, currentYear);
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

    public List<AssessmentStudentMarkRow> getAssessmentRows(String assessmentType, int courseId, int semesterYear, int itemNo) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }

        return lecturerMarksRepository.findAssessmentRows(assessmentType, courseId, semesterYear, itemNo);
    }



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
