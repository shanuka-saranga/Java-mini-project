package com.fot.system.view.dashboard.admin.manageCourses;

import com.fot.system.view.dashboard.admin.components.BaseAdminTablePanel;

public class CourseTablePanel extends BaseAdminTablePanel {

    public CourseTablePanel() {
        super(new String[]{"ID", "Code", "Course Name", "Department", "Credits", "Hours", "Session", "Quizzes", "Assignments", "Lecturer"});
    }
}
