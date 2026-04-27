package com.fot.system.view.dashboard.sidebar;

import com.fot.system.config.AppConfig;
import com.fot.system.view.dashboard.MainDashboard;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import javax.swing.*;

public class LecturerSidebar extends BaseSidebar {

    /**
     * initialize lecturer sidebar
     * @param frame MainDashboard parent frame
     * @author methum
     */
    public LecturerSidebar(MainDashboard frame) {
        super(frame, "LECTURER");
    }

    /**
     * add lecturer specific menu bu ttons
     * @author methum
     */
    @Override
    protected void addRoleSpecificButtons() {
        add(createMenuButton("My Courses", FontAwesomeSolid.BOOK_OPEN, AppConfig.MENU_COURSES));
        add(Box.createVerticalStrut(10));

        add(createMenuButton("Attendance", FontAwesomeSolid.USER_CHECK, AppConfig.MENU_ATTENDANCE));
        add(Box.createVerticalStrut(10));

        add(createMenuButton("Marks / Grades", FontAwesomeSolid.POLL, AppConfig.MENU_MARKS));
        add(Box.createVerticalStrut(10));

        add(createMenuButton("Exam Eligibility", FontAwesomeSolid.CLIPBOARD_CHECK, AppConfig.MENU_EXAM_ELIGIBILITY));
        add(Box.createVerticalStrut(10));

        add(createMenuButton("Students", FontAwesomeSolid.USERS_COG, AppConfig.MENU_STUDENTS));
        add(Box.createVerticalStrut(10));

        add(createMenuButton("Notices", FontAwesomeSolid.BULLHORN, AppConfig.MENU_NOTICES));
        add(Box.createVerticalStrut(10));

        add(createMenuButton("Timetables", FontAwesomeSolid.CALENDAR_ALT, AppConfig.MENU_TIMETABLES));
    }
}
