package com.fot.system.controller;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.CourseService;

/**
 * handle edit and delete course actions with validation
 * @author janith
 */
public class EditCourseController {

    private final CourseService courseService;

    /**
     * initialize edit course controller
     * @author janith
     */
    public EditCourseController() {
        this.courseService = new CourseService();
    }

    /**
     * validate and update course record
     * @param request edit course request payload
     * @author janith
     */
    public Course updateCourse(EditCourseRequest request) {
        validate(request);
        return courseService.updateCourse(request);
    }

    /**
     * delete course by id
     * @param courseId course id
     * @author janith
     */
    public void deleteCourse(int courseId) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }

        courseService.deleteCourse(courseId);
    }

    /**
     * validate edit request values
     * @param request edit course request payload
     * @author janith
     */
    private void validate(EditCourseRequest request) {
        if (request == null) {
            throw new RuntimeException("Course request cannot be null.");
        }

        if (request.getCourseId() <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }

        requireValue(request.getCourseCode(), "Course code is required.");
        requireValue(request.getCourseName(), "Course name is required.");
        requireValue(request.getCredits(), "Credits are required.");
        requireValue(request.getTotalHours(), "Total hours are required.");
        requireValue(request.getSessionType(), "Session type is required.");
        requireValue(request.getNoOfQuizzes(), "Number of quizzes is required.");
        requireValue(request.getNoOfAssignments(), "Number of assignments is required.");
        requireValue(request.getDepartmentId(), "Department is required.");
        parsePositiveInt(request.getCredits(), "Credits must be a valid number.");
        parsePositiveInt(request.getTotalHours(), "Total hours must be a valid number.");
        parseNonNegativeInt(request.getNoOfQuizzes(), "Number of quizzes must be a valid non-negative number.");
        parseNonNegativeInt(request.getNoOfAssignments(), "Number of assignments must be a valid non-negative number.");
        parsePositiveInt(request.getDepartmentId(), "Department is invalid.");

        if (!request.getLecturerInChargeId().trim().isEmpty()) {
            parsePositiveInt(request.getLecturerInChargeId(), "Lecturer is invalid.");
        }
    }

    /**
     * check required text value
     * @param value field value
     * @param message error message
     * @author janith
     */
    private void requireValue(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
    }

    /**
     * parse positive integer value
     * @param value field value
     * @param message error message
     * @author janith
     */
    private int parsePositiveInt(String value, String message) {
        try {
            int parsedValue = Integer.parseInt(value.trim());
            if (parsedValue <= 0) {
                throw new RuntimeException(message);
            }
            return parsedValue;
        } catch (NumberFormatException e) {
            throw new RuntimeException(message);
        }
    }

    /**
     * parse non negative integer value
     * @param value field value
     * @param message error message
     * @author janith
     */
    private int parseNonNegativeInt(String value, String message) {
        try {
            int parsedValue = Integer.parseInt(value.trim());
            if (parsedValue < 0) {
                throw new RuntimeException(message);
            }
            return parsedValue;
        } catch (NumberFormatException e) {
            throw new RuntimeException(message);
        }
    }
}
