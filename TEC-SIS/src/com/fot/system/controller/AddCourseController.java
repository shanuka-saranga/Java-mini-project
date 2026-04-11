package com.fot.system.controller;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.CourseService;
import com.fot.system.view.dashboard.admin.manageCourses.AddNewCoursePanel;

import javax.swing.*;

public class AddCourseController {

    private final AddNewCoursePanel view;
    private final CourseService courseService;
    private final Runnable onSuccessAction;

    public AddCourseController(AddNewCoursePanel view, Runnable onSuccessAction) {
        this.view = view;
        this.courseService = new CourseService();
        this.onSuccessAction = onSuccessAction;
        this.view.setOnSaveAction(this::handleSaveCourse);
    }

    private void handleSaveCourse() {
        try {
            AddCourseRequest request = view.buildRequest();
            validate(request);
            Course course = courseService.addCourse(request);

            JOptionPane.showMessageDialog(
                    view,
                    course.getCourseCode() + " added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            view.resetForm();
            if (onSuccessAction != null) {
                onSuccessAction.run();
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    view,
                    ex.getMessage(),
                    "Add Course Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void validate(AddCourseRequest request) {
        if (request == null) {
            throw new RuntimeException("Course request cannot be null.");
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
