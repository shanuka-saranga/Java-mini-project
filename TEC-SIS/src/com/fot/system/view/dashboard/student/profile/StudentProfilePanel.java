package com.fot.system.view.dashboard.student.profile;

import com.fot.system.model.entity.*;
import com.fot.system.view.dashboard.shared_components.UserProfilePanel;

public class StudentProfilePanel extends UserProfilePanel {

    /**
     * initialize student profile panel
     * @param user logged in student user
     * @author shanuka
     */
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

    /**
     * returns student profile subtitle text
     * @return student profile subtitle
     * @author shanuka
     */
    @Override
    protected String getSubtitleText() {
        return "Review your student profile. Students can update only contact details and profile picture.";
    }

}
