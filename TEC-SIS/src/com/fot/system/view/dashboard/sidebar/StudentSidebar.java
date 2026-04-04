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

        add(createMenuButton("My Courses", FontAwesomeSolid.BOOK_OPEN, AppConfig.MENU_MY_COURSES));
        add(Box.createVerticalStrut(10));

        add(createMenuButton("My Timetable", FontAwesomeSolid.CALENDAR_ALT, AppConfig.MENU_MY_TIMETABLE));
        add(Box.createVerticalStrut(10));

        add(createMenuButton("Notices", FontAwesomeSolid.BULLHORN, AppConfig.MENU_NOTICES));
        add(Box.createVerticalStrut(10));

        add(createMenuButton("Results", FontAwesomeSolid.FILE_ALT, AppConfig.MENU_RESULTS));
        add(Box.createVerticalStrut(10));

        add(createMenuButton("Attendance", FontAwesomeSolid.CLIPBOARD_CHECK, AppConfig.MENU_ATTENDANCE));
    }
}