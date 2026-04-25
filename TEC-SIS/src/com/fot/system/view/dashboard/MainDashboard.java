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
    private final User currentUser;
    private final String normalizedRole;

    /**
     * Main Dashboard frame
     * @param user logged in user
     * @author methum
     */
    public MainDashboard(User user) {
        this.currentUser = user;
        this.normalizedRole = normalizeRole(user);

        setTitle("TEC-SIS | " + user.getRole() + " Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(Color.WHITE);
        addRolePanels();
        add(contentArea, BorderLayout.CENTER);
    }

    /**
     * Create sidebar by user role
     * @author methum
     */
    private BaseSidebar createSidebar() {
        if (AppConfig.ROLE_ADMIN.equals(normalizedRole)) {
            return new AdminSidebar(this);
        }

        if (AppConfig.ROLE_STUDENT.equals(normalizedRole)) {
            return new StudentSidebar(this);
        }

        if (AppConfig.ROLE_TO.equals(normalizedRole)) {
            return new TOSidebar(this);
        }

        if (AppConfig.ROLE_LECTURER.equals(normalizedRole) || AppConfig.ROLE_DEAN.equals(normalizedRole)) {
            return new LecturerSidebar(this);
        }

        return new LecturerSidebar(this);
    }

    /**
     * Register dashboard cards by role
     * @author methum
     */
    private void addRolePanels() {
        if (AppConfig.ROLE_LECTURER.equals(normalizedRole)) {
            addLecturerPanels();
            return;
        }

        if (AppConfig.ROLE_STUDENT.equals(normalizedRole)) {
            addStudentPanels();
            return;
        }

        if (AppConfig.ROLE_TO.equals(normalizedRole)) {
            addTOPanels();
            return;
        }

        addAdminPanels();
    }

    /**
     * Add lecturer dashboard panels
     * @author methum
     */
    private void addLecturerPanels() {
        contentArea.add(new LecturerHomePanel(currentUser), AppConfig.MENU_HOME);
        contentArea.add(UserProfilePanelFactory.create(currentUser), AppConfig.MENU_PROFILE);
        contentArea.add(new LecturerCoursesPanel(currentUser), AppConfig.MENU_COURSES);
        contentArea.add(new LecturerAttendancePanel(currentUser), AppConfig.MENU_ATTENDANCE);
        contentArea.add(new LecturerMarksAndGradesPanel(currentUser), AppConfig.MENU_MARKS);
        contentArea.add(new LecturerExamEligibilityPanel(currentUser), AppConfig.MENU_EXAM_ELIGIBILITY);
        contentArea.add(new TimetablePanel(currentUser), AppConfig.MENU_TIMETABLES);
        contentArea.add(new StudentDetailsPanel(currentUser), AppConfig.MENU_STUDENTS);
        contentArea.add(new NoticePanel(currentUser), AppConfig.MENU_NOTICES);
    }

    /**
     * Add student dashboard panels
     * @author janith
     */
    private void addStudentPanels() {
        contentArea.add(new StudentHomePanel(currentUser), AppConfig.MENU_HOME);
        contentArea.add(UserProfilePanelFactory.create(currentUser), AppConfig.MENU_PROFILE);
        contentArea.add(new StudentMarksAndGradesPanel(currentUser), AppConfig.MENU_MARKS);
        contentArea.add(new StudentAttendanceMedicalPanel(currentUser), AppConfig.MENU_ATTENDANCE);
        contentArea.add(new NoticePanel(currentUser), AppConfig.MENU_NOTICES);
        contentArea.add(new StudentTimetablePanel(currentUser), AppConfig.MENU_TIMETABLES);
    }

    /**
     * Add technical officer dashboard panels
     * @author methum
     */
    private void addTOPanels() {
        contentArea.add(new TOHomePanel(currentUser), AppConfig.MENU_HOME);
        contentArea.add(UserProfilePanelFactory.create(currentUser), AppConfig.MENU_PROFILE);
        contentArea.add(new TOAttendancePanel(currentUser), AppConfig.MENU_ATTENDANCE);
        contentArea.add(new TOMedicalPanel(currentUser), AppConfig.MENU_MEDICALS);
        contentArea.add(new TOTimetablePanel(currentUser), AppConfig.MENU_TIMETABLES);
        contentArea.add(new NoticePanel(currentUser), AppConfig.MENU_NOTICES);
    }

    /**
     * Add admin dashboard panels
     * @author methum
     */
    private void addAdminPanels() {
        contentArea.add(new AdminHomePanel(currentUser), AppConfig.MENU_HOME);
        contentArea.add(UserProfilePanelFactory.create(currentUser), AppConfig.MENU_PROFILE);
        contentArea.add(new ManageUsersPanel(currentUser), AppConfig.MENU_MANAGE_USERS);
        contentArea.add(new ManageCoursesPanel(currentUser), AppConfig.MENU_MANAGE_COURSES);
        contentArea.add(new ManageNoticesPanel(currentUser), AppConfig.MENU_MANAGE_NOTICES);
        contentArea.add(new TimetablePanel(currentUser), AppConfig.MENU_TIMETABLES);
    }

    /**
     * Normalize user role text to uppercase
     * @param user logged in user
     * @author methum
     */
    private String normalizeRole(User user) {
        if (user == null || user.getRole() == null) {
            return "";
        }
        return user.getRole().trim().toUpperCase();
    }

    /**
     * Switch content panel by card name
     * @param cardName panel card id
     * @author methum
     */
    public void switchPanel(String cardName) {
        cardLayout.show(contentArea, cardName);
        contentArea.revalidate();
        contentArea.repaint();
    }

    /**
     * get currently logged in user
     * @author methum
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Logout from dashboard and open login screen
     * @author methum
     */
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

    /**
     * Get development email by current role
     * @author methum
     */
    private String getDevEmailForRole() {
        return switch (normalizedRole) {
            case "ADMIN" -> "admin@tec.ruh.ac.lk";
            case "STUDENT" -> "aruni@fot.ruh.ac.lk";
            case "LECTURER" -> "nimal@tec.ruh.ac.lk";
            case "TO" -> "jagath@tec.ruh.ac.lk";
            default -> "";
        };
    }
}
