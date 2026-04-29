package com.fot.system.view.dashboard.lecturer.shared_components;

import com.fot.system.model.entity.*;
import com.fot.system.view.shared_components.CourseSummaryCard;

public class LecturerCourseCard extends CourseSummaryCard {

    /**
     * create lecturer course summary card
     * @param course course entity
     * @param selected whether card is selected
     * @author poornika
     */
    public LecturerCourseCard(Course course, boolean selected) {
        super(course, selected);
    }
}
