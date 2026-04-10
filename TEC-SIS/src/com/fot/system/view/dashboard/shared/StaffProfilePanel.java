package com.fot.system.view.dashboard.shared;

import com.fot.system.model.User;

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

    @Override
    protected String getViewAccessHint() {
        return "You can update your profile photo, phone number, address, date of birth, and password.";
    }

    @Override
    protected String getEditAccessHint() {
        return "Allowed updates for your role: photo, phone number, address, date of birth, and password.";
    }
}
