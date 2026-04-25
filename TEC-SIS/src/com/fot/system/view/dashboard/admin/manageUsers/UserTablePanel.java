package com.fot.system.view.dashboard.admin.manageUsers;

import com.fot.system.view.dashboard.admin.components.BaseAdminTablePanel;

public class UserTablePanel extends BaseAdminTablePanel {

    public UserTablePanel() {
        super(new String[]{"ID", "Name", "Email", "Role", "Status"});
    }
}
