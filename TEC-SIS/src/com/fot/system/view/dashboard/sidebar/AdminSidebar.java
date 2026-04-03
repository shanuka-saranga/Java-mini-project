package com.fot.system.view.dashboard.sidebar;

import com.fot.system.view.dashboard.MainDashboard;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javax.swing.*;

public class AdminSidebar extends BaseSidebar {
    public AdminSidebar(MainDashboard frame) {
        super(frame, "ADMIN");
    }

    @Override
    protected void addRoleSpecificButtons() {
        add(createMenuButton("Manage Users", FontAwesomeSolid.USERS_COG, "UserListCard"));
        add(Box.createVerticalStrut(10));
        add(createMenuButton("Reports", FontAwesomeSolid.CHART_BAR, "ReportCard"));
    }
}