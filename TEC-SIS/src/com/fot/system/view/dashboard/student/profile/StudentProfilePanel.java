package com.fot.system.view.dashboard.student.profile;

import com.fot.system.model.User;
import com.fot.system.view.dashboard.shared.UserProfilePanel;

public class StudentProfilePanel extends UserProfilePanel {
    private static final String SUBTITLE_TEXT =
            "Review your student profile. Students can update only contact details and profile picture.";
    private static final String ACCESS_HINT =
            "Can update only contact details and profile picture of their profile.";

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
        return SUBTITLE_TEXT;
    }

    @Override
    protected String getViewAccessHint() {
        return ACCESS_HINT;
    }

    @Override
    protected String getEditAccessHint() {
        return ACCESS_HINT;
    }
}
