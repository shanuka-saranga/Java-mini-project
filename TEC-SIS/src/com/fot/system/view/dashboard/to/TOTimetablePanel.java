package com.fot.system.view.dashboard.to;

import com.fot.system.model.entity.TimetableSession;
import com.fot.system.model.entity.User;
import com.fot.system.view.shared_components.TimeTablePanel;

/**
 * Shows the technical officer timetable using the shared read-only timetable layout.
 * @author janith
 */
public class TOTimetablePanel extends TimeTablePanel {

    /**
     * Creates the technical officer timetable panel.
     * @param user logged-in technical officer user
     * @author janith
     */
    public TOTimetablePanel(User user) {
        super();
    }

    /**
     * Returns the technical officer timetable subtitle.
     * @author janith
     */
    @Override
    protected String getPanelSubtitle() {
        return "View the weekly timetable for all courses in a real timetable layout.";
    }

    /**
     * Shows both session type and lecturer name in timetable cells for technical officers.
     * @param session timetable session
     * @author janith
     */
    @Override
    protected String buildSessionMeta(TimetableSession session) {
        return valueOrDash(session.getSessionType()) + " | " + valueOrDash(session.getLecturerName());
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
