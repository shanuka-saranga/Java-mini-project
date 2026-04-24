package com.fot.system.view.dashboard;

import com.fot.system.config.AppConfig;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.view.login.LoginView;
import com.fot.system.view.dashboard.admin.manageCourses.ManageCoursesPanel;
import com.fot.system.view.dashboard.admin.manageNotices.ManageNoticesPanel;
import com.fot.system.view.dashboard.admin.AdminHomePanel;
import com.fot.system.view.dashboard.admin.manageUsers.ManageUsersPanel;
import com.fot.system.view.dashboard.lecturer.LecturerHomePanel;
import com.fot.system.view.dashboard.lecturer.attendance.LecturerAttendancePanel;
import com.fot.system.view.dashboard.lecturer.examEligibility.LecturerExamEligibilityPanel;
import com.fot.system.view.dashboard.lecturer.marksGrades.LecturerMarksAndGradesPanel;
import com.fot.system.view.dashboard.lecturer.myCourses.LecturerCoursesPanel;
import com.fot.system.view.dashboard.lecturer.notice.NoticePanel;
import com.fot.system.view.dashboard.lecturer.studentDetails.StudentDetailsPanel;
import com.fot.system.view.dashboard.lecturer.timetable.TimetablePanel;
import com.fot.system.view.dashboard.student.attendance.StudentAttendanceMedicalPanel;
import com.fot.system.view.dashboard.student.marksGrades.StudentMarksAndGradesPanel;
import com.fot.system.view.dashboard.student.timetable.StudentTimetablePanel;
import com.fot.system.view.dashboard.student.StudentHomePanel;
import com.fot.system.view.dashboard.to.TOHomePanel;
import com.fot.system.view.dashboard.to.TOAttendancePanel;
import com.fot.system.view.dashboard.to.TOMedicalPanel;
import com.fot.system.view.dashboard.shared.FeaturePlaceholderPanel;
import com.fot.system.view.dashboard.to.TOTimetablePanel;
import com.fot.system.view.dashboard.shared.UserProfilePanelFactory;
import com.fot.system.view.dashboard.sidebar.AdminSidebar;
import com.fot.system.view.dashboard.sidebar.BaseSidebar;
import com.fot.system.view.dashboard.sidebar.LecturerSidebar;
import com.fot.system.view.dashboard.sidebar.StudentSidebar;
import com.fot.system.view.dashboard.sidebar.TOSidebar;

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

        if (AppConfig.ROLE_ADMIN.equalsIgnoreCase(user.getRole())) {
            sidebar = new AdminSidebar(this);
        } else if (AppConfig.ROLE_STUDENT.equalsIgnoreCase(user.getRole())) {
            sidebar = new StudentSidebar(this);
        } else if (AppConfig.ROLE_LECTURER.equalsIgnoreCase(user.getRole()) || AppConfig.ROLE_DEAN.equalsIgnoreCase(user.getRole())) {
            sidebar = new LecturerSidebar(this);
        } else if (AppConfig.ROLE_TO.equalsIgnoreCase(user.getRole())) {
            sidebar = new TOSidebar(this);
        } else {
            sidebar = new LecturerSidebar(this);
        }
        add(sidebar, BorderLayout.WEST);
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(Color.WHITE);

        if (AppConfig.ROLE_LECTURER.equalsIgnoreCase(user.getRole())) {
            contentArea.add(new LecturerHomePanel(user), AppConfig.MENU_HOME);
            contentArea.add(UserProfilePanelFactory.create(user), AppConfig.MENU_PROFILE);
            contentArea.add(new LecturerCoursesPanel(user), AppConfig.MENU_COURSES);
            contentArea.add(new LecturerAttendancePanel(user), AppConfig.MENU_ATTENDANCE);
            contentArea.add(new LecturerMarksAndGradesPanel(user), AppConfig.MENU_MARKS);
            contentArea.add(new LecturerExamEligibilityPanel(user), AppConfig.MENU_EXAM_ELIGIBILITY);
            contentArea.add(new FeaturePlaceholderPanel(
                    "Notices",
                    "This section can show notices relevant to lecturers, including faculty-wide and course-related announcements."
            ), AppConfig.MENU_NOTICES);
            contentArea.add(new TimetablePanel(user), AppConfig.MENU_TIMETABLES);
            contentArea.add(new StudentDetailsPanel(user),AppConfig.MENU_STUDENTS);
            contentArea.add(new NoticePanel(user),AppConfig.MENU_NOTICES);
        } else if (AppConfig.ROLE_STUDENT.equalsIgnoreCase(user.getRole())) {
            contentArea.add(new StudentHomePanel(user), AppConfig.MENU_HOME);
            contentArea.add(UserProfilePanelFactory.create(user), AppConfig.MENU_PROFILE);
            contentArea.add(new StudentMarksAndGradesPanel(user), AppConfig.MENU_MARKS);
            contentArea.add(new StudentAttendanceMedicalPanel(user), AppConfig.MENU_ATTENDANCE);
            contentArea.add(new NoticePanel(user), AppConfig.MENU_NOTICES);
            contentArea.add(new StudentTimetablePanel(user), AppConfig.MENU_TIMETABLES);
        } else if (AppConfig.ROLE_TO.equalsIgnoreCase(user.getRole())) {
            contentArea.add(new TOHomePanel(user), AppConfig.MENU_HOME);
            contentArea.add(UserProfilePanelFactory.create(user), AppConfig.MENU_PROFILE);
            contentArea.add(new TOAttendancePanel(user), AppConfig.MENU_ATTENDANCE);
            contentArea.add(new TOMedicalPanel(user), AppConfig.MENU_MEDICALS);
            contentArea.add(new TOTimetablePanel(user), AppConfig.MENU_TIMETABLES);
            contentArea.add(new NoticePanel(user), AppConfig.MENU_NOTICES);
        } else {
            contentArea.add(new AdminHomePanel(user), AppConfig.MENU_HOME);
            contentArea.add(UserProfilePanelFactory.create(user), AppConfig.MENU_PROFILE);
            contentArea.add(new ManageUsersPanel(user),AppConfig.MENU_MANAGE_USERS);
            contentArea.add(new ManageCoursesPanel(user), AppConfig.MENU_MANAGE_COURSES);
            contentArea.add(new ManageNoticesPanel(user), AppConfig.MENU_MANAGE_NOTICES);
            contentArea.add(new TimetablePanel(user), AppConfig.MENU_TIMETABLES);


        }
        add(contentArea, BorderLayout.CENTER);


    }

    public void switchPanel(String cardName) {
        cardLayout.show(contentArea, cardName);
        contentArea.revalidate();
        contentArea.repaint();
    }

    public void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Do you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        LoginView loginView = new LoginView(getDevEmailForRole(), "1234");
        loginView.setVisible(true);
        dispose();
    }

    private String getDevEmailForRole() {
        if (currentUser == null || currentUser.getRole() == null) {
            return "";
        }

        String role = currentUser.getRole().trim().toUpperCase();
        return switch (role) {
            case "ADMIN" -> "admin@tec.ruh.ac.lk";
            case "STUDENT" -> "aruni@fot.ruh.ac.lk";
            case "LECTURER" -> "nimal@tec.ruh.ac.lk";
            case "TO" -> "jagath@tec.ruh.ac.lk";
            default -> "";
        };
    }


}
