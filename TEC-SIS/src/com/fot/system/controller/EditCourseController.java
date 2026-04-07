package com.fot.system.controller;

import com.fot.system.model.Course;
import com.fot.system.model.EditCourseRequest;
import com.fot.system.service.CourseService;

public class EditCourseController {

    private final CourseService courseService;

    public EditCourseController() {
        this.courseService = new CourseService();
    }

    public Course updateCourse(EditCourseRequest request) {
        validate(request);
        return courseService.updateCourse(request);
    }

    public void deleteCourse(int courseId) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID.");
        }

        courseService.deleteCourse(courseId);
    }

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
        requireValue(request.getDepartmentId(), "Department is required.");
        parsePositiveInt(request.getCredits(), "Credits must be a valid number.");
        parsePositiveInt(request.getTotalHours(), "Total hours must be a valid number.");
        parsePositiveInt(request.getDepartmentId(), "Department is invalid.");

        if (!request.getLecturerInChargeId().trim().isEmpty()) {
            parsePositiveInt(request.getLecturerInChargeId(), "Lecturer is invalid.");
        }
    }

    private void requireValue(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
    }

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
}
