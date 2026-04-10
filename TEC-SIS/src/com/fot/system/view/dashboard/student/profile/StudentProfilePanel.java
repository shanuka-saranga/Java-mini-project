package com.fot.system.view.dashboard.student.profile;

import com.fot.system.model.User;
import com.fot.system.view.dashboard.shared.UserProfilePanel;

public class StudentProfilePanel extends UserProfilePanel {

    public StudentProfilePanel(User user) {
        super(user);
    }

    @Override
    protected boolean canEditDob() {
        return false;
    }

    @Override
    protected boolean canEditPassword() {
        return false;
    }

    @Override
    protected String getSubtitleText() {
        return "Review your student profile. Students can update only contact details and profile picture.";
    }

    @Override
    protected String getViewAccessHint() {
        return "Can update only contact details and profile picture of their profile.";
    }

    @Override
    protected String getEditAccessHint() {
        return "Can update only contact details and profile picture of their profile.";
    }
}
