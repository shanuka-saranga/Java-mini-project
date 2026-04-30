package com.fot.system.view.dashboard.lecturer.timetable;
import com.fot.system.model.entity.User;
import com.fot.system.view.shared_components.TimeTablePanel;

/**
 * Provides a lecturer-facing read-only timetable view using the shared timetable layout.
 * @author janith
 */
public class LecturerTimetablePanel extends TimeTablePanel {

    /**
     * Creates the lecturer timetable panel without edit controls.
     * @param user logged-in lecturer user
     * @author janith
     */
    public LecturerTimetablePanel(User user) {
        super();
    }

    /**
     * Returns the lecturer timetable subtitle.
     * @author janith
     */
    @Override
    protected String getPanelSubtitle() {
        return "View the weekly timetable in a read-only timetable layout with weekdays across the top.";
    }
}
