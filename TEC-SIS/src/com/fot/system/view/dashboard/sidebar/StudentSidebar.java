package com.fot.system.view.dashboard.sidebar;

import com.fot.system.config.AppConfig;
import com.fot.system.view.dashboard.MainDashboard;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import javax.swing.*;

public class StudentSidebar extends BaseSidebar {

    public StudentSidebar(MainDashboard frame) {
        super(frame, "STUDENT");
    }

    @Override
    protected void addRoleSpecificButtons() {
        add(createMenuButton("Marks / Grades", FontAwesomeSolid.POLL, AppConfig.MENU_MARKS));
        add(Box.createVerticalStrut(10));
        add(createMenuButton("Attendance", FontAwesomeSolid.USER_CHECK, AppConfig.MENU_ATTENDANCE));
        add(Box.createVerticalStrut(10));
        add(createMenuButton("Notices", FontAwesomeSolid.BULLHORN, AppConfig.MENU_NOTICES));
        add(Box.createVerticalStrut(10));
        add(createMenuButton("Timetables", FontAwesomeSolid.CALENDAR_ALT, AppConfig.MENU_TIMETABLES));
        add(Box.createVerticalStrut(10));
    }
}
