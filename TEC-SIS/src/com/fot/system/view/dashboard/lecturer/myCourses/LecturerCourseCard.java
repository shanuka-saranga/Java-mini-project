package com.fot.system.view.dashboard.lecturer.myCourses;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.view.components.CourseSummaryCard;

public class LecturerCourseCard extends CourseSummaryCard {

    public LecturerCourseCard(Course course, boolean selected) {
        super(course, selected);
    }
}
