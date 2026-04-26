package com.fot.system.view.dashboard.shared_components;

import com.fot.system.model.entity.*;

public class StaffProfilePanel extends UserProfilePanel {

    public StaffProfilePanel(User user) {
        super(user);
    }

    @Override
    protected boolean canEditDob() {
        return true;
    }

    @Override
    protected boolean canEditPassword() {
        return true;
    }

    @Override
    protected String getSubtitleText() {
        return "Review your staff profile and update the personal information allowed for staff accounts.";
    }

}
