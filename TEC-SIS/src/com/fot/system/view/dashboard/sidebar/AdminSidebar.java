package com.fot.system.view.dashboard.sidebar;

import com.fot.system.config.AppConfig;
import com.fot.system.view.dashboard.MainDashboard;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javax.swing.*;

public class AdminSidebar extends BaseSidebar {
    /**
     * initialize admin sidebar
     * @param frame MainDashboard parent frame
     * @author methum
     */
    public AdminSidebar(MainDashboard frame) {
        super(frame, AppConfig.ROLE_ADMIN);
    }

    /**
     * add admin specific menu buttons
     * @author methum
     */
    @Override
    protected void addRoleSpecificButtons() {

        add(createMenuButton("Manage Users", FontAwesomeSolid.USERS_COG, AppConfig.MENU_MANAGE_USERS));
        add(Box.createVerticalStrut(10));

        add(createMenuButton("Manage Courses", FontAwesomeSolid.BOOK, AppConfig.MENU_MANAGE_COURSES));
        add(Box.createVerticalStrut(10));

        add(createMenuButton("Manage Notices", FontAwesomeSolid.BULLHORN, AppConfig.MENU_MANAGE_NOTICES));
        add(Box.createVerticalStrut(10));

        add(createMenuButton("Timetables", FontAwesomeSolid.CALENDAR_ALT, AppConfig.MENU_TIMETABLES));
        add(Box.createVerticalStrut(10));

        add(createMenuButton("Reports", FontAwesomeSolid.FILE_ALT, AppConfig.MENU_REPORTS));
    }
}
