package com.fot.system.view.dashboard.shared;

import com.fot.system.model.User;

public class StudentProfilePanel extends UserProfilePanel {

    public StudentProfilePanel(User user) {
        super(user);
    }

    @Override
    protected boolean canEditDob() {
        return false;
    }

    @Override
    protected String getSubtitleText() {
        return "Review your student profile and update the contact details that are allowed for students.";
    }

    @Override
    protected String getViewAccessHint() {
        return "You can update your profile photo, phone number, address, and password.";
    }

    @Override
    protected String getEditAccessHint() {
        return "Allowed updates for your role: photo, phone number, address, and password.";
    }
}
