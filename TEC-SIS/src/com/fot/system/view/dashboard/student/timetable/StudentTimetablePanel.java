package com.fot.system.view.dashboard.student.timetable;

import com.fot.system.model.entity.User;
import com.fot.system.view.dashboard.shared_components.TimeTablePanel;

/**
 * Shows the student timetable using the shared read-only timetable layout.
 * @author janith
 */
public class StudentTimetablePanel extends TimeTablePanel {

    /**
     * Creates the student timetable panel.
     * @param user logged-in student user
     * @author janith
     */
    public StudentTimetablePanel(User user) {
        super();
    }

    /**
     * Returns the student-specific timetable subtitle.
     * @author janith
     */
    @Override
    protected String getPanelSubtitle() {
        return "View the weekly timetable in a real timetable layout with weekdays across the top.";
    }
}
