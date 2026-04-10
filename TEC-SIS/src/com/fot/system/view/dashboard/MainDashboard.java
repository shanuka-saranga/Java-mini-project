package com.fot.system.view.dashboard;

import com.fot.system.config.AppConfig;
import com.fot.system.model.User;
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
import com.fot.system.view.dashboard.shared.FeaturePlaceholderPanel;
import com.fot.system.view.dashboard.shared.UserProfilePanelFactory;
import com.fot.system.view.dashboard.sidebar.AdminSidebar;
import com.fot.system.view.dashboard.sidebar.BaseSidebar;
import com.fot.system.view.dashboard.sidebar.LecturerSidebar;
import com.fot.system.view.dashboard.sidebar.StudentSidebar;

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
        } else if (AppConfig.ROLE_LECTURER.equalsIgnoreCase(user.getRole())) {
            sidebar = new LecturerSidebar(this);
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


}
