package com.fot.system.view.dashboard.lecturer.myCourses;

import com.fot.system.model.Course;
import com.fot.system.view.components.FeedItemCard;

import javax.swing.*;
import java.awt.*;

public class LecturerCourseCard extends FeedItemCard {

    private final Course course;

    public LecturerCourseCard(Course course, boolean selected) {
        super(
                course.getCourseCode() + " - " + course.getCourseName(),
                "Session: " + valueOrDash(course.getSessionType()) + "  |  Credits: " + course.getCredits() + "  |  Hours: " + course.getTotalHours(),
                "Department: " + valueOrDash(course.getDepartmentName()),
                104
        );

        this.course = course;
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (selected) {
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(com.fot.system.config.AppTheme.PRIMARY, 2, true),
                    BorderFactory.createEmptyBorder(13, 13, 13, 13)
            ));
        }
    }

    public Course getCourse() {
        return course;
    }

    private static String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
