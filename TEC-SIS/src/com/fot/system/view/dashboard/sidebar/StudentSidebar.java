package com.fot.system.view.dashboard.sidebar;

import com.fot.system.view.dashboard.MainDashboard;

public class StudentSidebar extends BaseSidebar {

    public StudentSidebar(MainDashboard frame) {
        super(frame, "STUDENT");
    }

    @Override
    protected void addRoleSpecificButtons() {
        // Student home already contains the course list and materials flow.
    }
}
