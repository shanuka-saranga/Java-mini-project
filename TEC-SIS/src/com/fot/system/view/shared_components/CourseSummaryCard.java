package com.fot.system.view.shared_components;

import com.fot.system.config.AppTheme;
import com.fot.system.model.entity.*;

import javax.swing.*;
import java.awt.*;

public class CourseSummaryCard extends FeedItemCard {

    private final Course course;

    public CourseSummaryCard(Course course, boolean selected) {
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
                    BorderFactory.createLineBorder(AppTheme.PRIMARY, 2, false),
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
