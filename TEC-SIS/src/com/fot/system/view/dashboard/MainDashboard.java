package com.fot.system.view.dashboard;

import com.fot.system.config.AppConfig;
import com.fot.system.model.User;
import com.fot.system.view.dashboard.admin.AdminHomePanel;
import com.fot.system.view.dashboard.admin.ManageUsersPanel;
import com.fot.system.view.dashboard.sidebar.AdminSidebar;
import com.fot.system.view.dashboard.sidebar.BaseSidebar;
import com.fot.system.view.dashboard.sidebar.StudentSidebar;
import com.fot.system.view.dashboard.student.MyCoursesPanel;

import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {

    private BaseSidebar sidebar;
    private JPanel contentArea;
    private CardLayout cardLayout;
    private User currentUser;

    public MainDashboard(User user) {
        this.currentUser = user;

        setTitle("TEC-SIS | " + user.getRole() + " Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initializeSidebar();
        initializeContentArea();

        add(sidebar, BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);
    }

    private void initializeSidebar() {
        if (AppConfig.ROLE_ADMIN.equalsIgnoreCase(currentUser.getRole())) {
            sidebar = new AdminSidebar(this);
        } else if (AppConfig.ROLE_STUDENT.equalsIgnoreCase(currentUser.getRole())) {
            sidebar = new StudentSidebar(this);
        } else {
            sidebar = new JPanelSidebarFallback(this);
        }
    }

    private void initializeContentArea() {
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(Color.WHITE);

        if (AppConfig.ROLE_ADMIN.equalsIgnoreCase(currentUser.getRole())) {
            contentArea.add(new AdminHomePanel(currentUser), AppConfig.MENU_HOME);
            contentArea.add(new ManageUsersPanel(currentUser), AppConfig.MENU_MANAGE_USERS);

        } else if (AppConfig.ROLE_STUDENT.equalsIgnoreCase(currentUser.getRole())) {
            contentArea.add(new MyCoursesPanel(currentUser, "Home"), AppConfig.MENU_HOME);
            contentArea.add(new MyCoursesPanel(currentUser, "My Courses"), AppConfig.MENU_MY_COURSES);
            contentArea.add(createPlaceholderPanel("My Timetable"), AppConfig.MENU_MY_TIMETABLE);
            contentArea.add(createPlaceholderPanel("Notices"), AppConfig.MENU_NOTICES);
            contentArea.add(createPlaceholderPanel("Results"), AppConfig.MENU_RESULTS);
            contentArea.add(createPlaceholderPanel("Attendance"), AppConfig.MENU_ATTENDANCE);
            contentArea.add(createPlaceholderPanel("Profile"), AppConfig.MENU_PROFILE);
        }

        cardLayout.show(contentArea, AppConfig.MENU_HOME);
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    public void switchPanel(String cardName) {
        cardLayout.show(contentArea, cardName);
    }

    private static class JPanelSidebarFallback extends BaseSidebar {
        public JPanelSidebarFallback(MainDashboard frame) {
            super(frame, "USER");
        }

        @Override
        protected void addRoleSpecificButtons() {
        }
    }
}