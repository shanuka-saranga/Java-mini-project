package com.fot.system.view.dashboard.sidebar;

import com.fot.system.config.AppConfig;
import com.fot.system.view.dashboard.MainDashboard;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import javax.swing.*;

public class TOSidebar extends BaseSidebar {

    /**
     * initialize technical officer sidebar
     * @param frame MainDashboard parent frame
     * @author methum
     */
    public TOSidebar(MainDashboard frame) {
        super(frame, "TECHNICAL OFFICER");
    }

    /**
     * add technical officer specific menu buttons
     * @author methum
     */
    @Override
    protected void addRoleSpecificButtons() {
        add(createMenuButton("Attendance", FontAwesomeSolid.USER_CHECK, AppConfig.MENU_ATTENDANCE));
        add(Box.createVerticalStrut(10));
        add(createMenuButton("Medicals", FontAwesomeSolid.FILE_MEDICAL, AppConfig.MENU_MEDICALS));
        add(Box.createVerticalStrut(10));
        add(createMenuButton("Timetable", FontAwesomeSolid.CALENDAR_ALT, AppConfig.MENU_TIMETABLES));
        add(Box.createVerticalStrut(10));
        add(createMenuButton("Notices", FontAwesomeSolid.BULLHORN, AppConfig.MENU_NOTICES));
        add(Box.createVerticalStrut(10));
    }
}
