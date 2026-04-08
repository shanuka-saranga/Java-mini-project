package com.fot.system.service;

import com.fot.system.model.AssessmentCardSummary;
import com.fot.system.model.CourseSemesterContext;
import com.fot.system.model.StudentMarksOverviewRow;
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

    public List<StudentMarksOverviewRow> getStudentMarksOverviewByCourse(int courseId) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }
        return lecturerMarksRepository.findStudentMarksOverviewByCourse(courseId);
    }

    public List<StudentMarksOverviewRow> getStudentMarksOverviewByCourse(int courseId, int semesterYear) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }
        return lecturerMarksRepository.findStudentMarksOverviewByCourse(courseId, semesterYear);
    }
}
