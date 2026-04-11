package com.fot.system.view.dashboard.admin;

import com.fot.system.config.AppConfig;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.CourseService;
import com.fot.system.service.NoticeService;
import com.fot.system.service.UserService;
import com.fot.system.view.dashboard.admin.shared.DashboardStatCard;
import com.fot.system.view.dashboard.admin.shared.NoticeFeedPanel;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AdminHomePanel extends JPanel {

    private final User currentUser;
    private final UserService userService;
    private final CourseService courseService;
    private final NoticeService noticeService;

    private final DashboardStatCard totalUsersCard;
    private final DashboardStatCard lecturerCard;
    private final DashboardStatCard coursesCard;
    private final DashboardStatCard noticesCard;
    private final NoticeFeedPanel noticeFeedPanel;
    private JLabel studentCountLabel;
    private JLabel lecturerCountLabel;
    private JLabel technicalOfficerCountLabel;
    private JLabel adminCountLabel;

    public AdminHomePanel(User user) {
        this.currentUser = user;
        this.userService = new UserService();
        this.courseService = new CourseService();
        this.noticeService = new NoticeService();

        setBackground(new Color(245, 248, 248));
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        totalUsersCard = new DashboardStatCard("Total Users", "...", FontAwesomeSolid.USERS);
        lecturerCard = new DashboardStatCard("Lecturers", "...", FontAwesomeSolid.CHALKBOARD_TEACHER);
        coursesCard = new DashboardStatCard("Courses", "...", FontAwesomeSolid.BOOK_OPEN);
        noticesCard = new DashboardStatCard("Visible Notices", "...", FontAwesomeSolid.BULLHORN);
        noticeFeedPanel = new NoticeFeedPanel(buildNoticePanelTitle());

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        loadDashboardData();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel subtitleLabel = new JLabel("Here is the current system overview with live counts and recent notices.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(110, 110, 110));

        header.add(welcomeLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setOpaque(false);

        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.add(totalUsersCard);
        statsGrid.add(lecturerCard);
        statsGrid.add(coursesCard);
        statsGrid.add(noticesCard);

        JPanel lowerSection = new JPanel(new GridLayout(1, 2, 20, 0));
        lowerSection.setOpaque(false);
        lowerSection.add(createRoleSummaryPanel());
        lowerSection.add(noticeFeedPanel);

        content.add(statsGrid, BorderLayout.NORTH);
        content.add(lowerSection, BorderLayout.CENTER);
        return content;
    }

    private JPanel createRoleSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 235, 235), 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel title = new JLabel("Users Details");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JPanel list = new JPanel(new GridLayout(4, 1, 0, 12));
        list.setOpaque(false);
        studentCountLabel = createMiniSummaryRow("Students", FontAwesomeSolid.USER_GRADUATE);
        lecturerCountLabel = createMiniSummaryRow("Lecturers", FontAwesomeSolid.CHALKBOARD_TEACHER);
        technicalOfficerCountLabel = createMiniSummaryRow("Technical Officers", FontAwesomeSolid.TOOLS);
        adminCountLabel = createMiniSummaryRow("Admins", FontAwesomeSolid.USER_SHIELD);

        list.add((Component) studentCountLabel.getParent());
        list.add((Component) lecturerCountLabel.getParent());
        list.add((Component) technicalOfficerCountLabel.getParent());
        list.add((Component) adminCountLabel.getParent());

        panel.add(title, BorderLayout.NORTH);
        panel.add(list, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createMiniSummaryRow(String labelText, FontAwesomeSolid icon) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 240, 240), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel iconLabel = new JLabel(org.kordamp.ikonli.swing.FontIcon.of(icon, 18, new Color(0, 121, 107)));
        JLabel textLabel = new JLabel(labelText);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel countLabel = new JLabel("...");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(iconLabel);
        left.add(textLabel);

        row.add(left, BorderLayout.WEST);
        row.add(countLabel, BorderLayout.EAST);
        return countLabel;
    }

    private void loadDashboardData() {
        SwingWorker<DashboardData, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardData doInBackground() {
                int totalUsers = userService.getUserCount();
                int lecturerCount = userService.getUserCountByRole(AppConfig.ROLE_LECTURER);
                int courseCount = courseService.getCourseCount();
                int visibleNoticeCount = noticeService.getVisibleNoticeCountForRole(currentUser.getRole());
                List<Notice> visibleNotices = noticeService.getRecentVisibleNoticesForRole(currentUser.getRole(), 6);

                return new DashboardData(
                        totalUsers,
                        lecturerCount,
                        courseCount,
                        visibleNoticeCount,
                        userService.getUserCountByRole(AppConfig.ROLE_STUDENT),
                        lecturerCount,
                        userService.getUserCountByRole(AppConfig.ROLE_TO),
                        userService.getUserCountByRole(AppConfig.ROLE_ADMIN),
                        visibleNotices
                );
            }

            @Override
            protected void done() {
                try {
                    DashboardData data = get();
                    totalUsersCard.setValue(String.valueOf(data.totalUsers));
                    lecturerCard.setValue(String.valueOf(data.lecturers));
                    coursesCard.setValue(String.valueOf(data.courses));
                    noticesCard.setValue(String.valueOf(data.visibleNoticesCount));
                    applyRoleBreakdown(data);
                    noticeFeedPanel.setNotices(data.notices);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                            AdminHomePanel.this,
                            "Failed to load dashboard data.",
                            "Dashboard Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void applyRoleBreakdown(DashboardData data) {
        studentCountLabel.setText(String.valueOf(data.students));
        lecturerCountLabel.setText(String.valueOf(data.lecturers));
        technicalOfficerCountLabel.setText(String.valueOf(data.technicalOfficers));
        adminCountLabel.setText(String.valueOf(data.admins));
    }

    private String buildNoticePanelTitle() {
        if (AppConfig.ROLE_ADMIN.equalsIgnoreCase(currentUser.getRole())) {
            return "Recent Active Notices";
        }
        return currentUser.getRole() + " Notices";
    }

    private static class DashboardData {
        private final int totalUsers;
        private final int lecturers;
        private final int courses;
        private final int visibleNoticesCount;
        private final int students;
        private final int technicalOfficers;
        private final int admins;
        private final List<Notice> notices;

        private DashboardData(int totalUsers, int lecturers, int courses, int visibleNoticesCount,
                              int students, int lecturerTotal, int technicalOfficers, int admins,
                              List<Notice> notices) {
            this.totalUsers = totalUsers;
            this.lecturers = lecturerTotal;
            this.courses = courses;
            this.visibleNoticesCount = visibleNoticesCount;
            this.students = students;
            this.technicalOfficers = technicalOfficers;
            this.admins = admins;
            this.notices = notices;
        }
    }
}
