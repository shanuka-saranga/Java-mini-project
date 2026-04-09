package com.fot.system.view.dashboard.lecturer.timetable;

import com.fot.system.view.dashboard.admin.shared.BaseAdminTablePanel;

public class TimetableTablePanel extends BaseAdminTablePanel {

    public TimetableTablePanel() {
        super(new String[]{"ID", "Course Code", "Course Name", "Lecturer", "Day", "Start", "End", "Venue", "Type"});
    }
}
