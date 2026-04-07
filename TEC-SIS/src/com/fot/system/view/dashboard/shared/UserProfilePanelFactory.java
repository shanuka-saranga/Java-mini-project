package com.fot.system.view.dashboard.shared;

import com.fot.system.config.AppConfig;
import com.fot.system.model.User;

public final class UserProfilePanelFactory {

    private UserProfilePanelFactory() {
    }

    public static UserProfilePanel create(User user) {
        String role = user == null || user.getRole() == null ? "" : user.getRole().trim().toUpperCase();

        switch (role) {
            case AppConfig.ROLE_STUDENT:
                return new StudentProfilePanel(user);
            default:
                return new StaffProfilePanel(user);
        }
    }
}
