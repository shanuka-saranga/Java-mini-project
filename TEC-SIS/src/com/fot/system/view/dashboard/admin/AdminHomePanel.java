package com.fot.system.view.dashboard.admin;

import com.fot.system.config.AppTheme;
import com.fot.system.config.AppConfig;
import com.fot.system.model.dto.AdminDashboardData;
import com.fot.system.model.entity.*;
import com.fot.system.service.CourseService;
import com.fot.system.service.NoticeService;
import com.fot.system.service.UserService;
import com.fot.system.view.dashboard.admin.components.DashboardStatCard;
import com.fot.system.view.dashboard.admin.components.NoticeFeedPanel;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * render admin dashboard home with system metrics and notices
 * @author janith
 */
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

    /**
     * initialize admin home dashboard panel
     * @param user logged in user
     * @author janith
     */
    public AdminHomePanel(User user) {
        this.currentUser = user;
        this.userService = new UserService();
        this.courseService = new CourseService();
        this.noticeService = new NoticeService();

        setBackground(AppTheme.SURFACE_SOFT);
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

    /**
     * create dashboard header section
     * @author janith
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);
        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(AppTheme.fontBold(28));
        JLabel subtitleLabel = new JLabel("Here is the current system overview with live counts and recent notices.");
        subtitleLabel.setFont(AppTheme.fontPlain(14));
        subtitleLabel.setForeground(AppTheme.TEXT_SUBTLE);
        header.add(welcomeLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);
        return header;
    }

    /**
     * create dashboard content section
     * @author janith
     */
    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setOpaque(false);

        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.add(totalUsersCard);
        statsGrid.add(lecturerCard);
        statsGrid.add(coursesCard);
        statsGrid.add(noticesCard);

        JPanel lowerSection = new JPanel(new GridBagLayout());
        lowerSection.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 10);
        lowerSection.add(createRoleSummaryPanel(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 3.0;
        gbc.insets = new Insets(0, 10, 0, 0);
        lowerSection.add(noticeFeedPanel, gbc);

        content.add(statsGrid, BorderLayout.NORTH);
        content.add(lowerSection, BorderLayout.CENTER);
        return content;
    }

    /**
     * create user role summary panel
     * @author janith
     */
    private JPanel createRoleSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(AppTheme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel title = new JLabel("Users Details");
        title.setFont(AppTheme.fontBold(20));

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

    /**
     * create mini summary row for role breakdown
     * @param labelText row label
     * @param icon row icon
     * @author janith
     */
    private JLabel createMiniSummaryRow(String labelText, FontAwesomeSolid icon) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_SOFT, 1, false),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel iconLabel = new JLabel(org.kordamp.ikonli.swing.FontIcon.of(icon, 18, AppTheme.ICON_ACCENT));
        JLabel textLabel = new JLabel(labelText);
        textLabel.setFont(AppTheme.fontPlain(14));

        JLabel countLabel = new JLabel("...");
        countLabel.setFont(AppTheme.fontBold(16));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(iconLabel);
        left.add(textLabel);

        row.add(left, BorderLayout.WEST);
        row.add(countLabel, BorderLayout.EAST);
        return countLabel;
    }

    /**
     * load dashboard metrics and recent notices
     * @author janith
     */
    private void loadDashboardData() {
        SwingWorker<AdminDashboardData, Void> worker = new SwingWorker<>() {
            @Override
            protected AdminDashboardData doInBackground() {
                int totalUsers = userService.getUserCount();
                int lecturerCount = userService.getUserCountByRole(AppConfig.ROLE_LECTURER);
                int courseCount = courseService.getCourseCount();
                int visibleNoticeCount = noticeService.getVisibleNoticeCountForRole(currentUser.getRole());
                List<Notice> visibleNotices = noticeService.getRecentVisibleNoticesForRole(currentUser.getRole(), 6);

                return new AdminDashboardData(
                        totalUsers,
                        lecturerCount,
                        courseCount,
                        visibleNoticeCount,
                        userService.getUserCountByRole(AppConfig.ROLE_STUDENT),
                        userService.getUserCountByRole(AppConfig.ROLE_TO),
                        userService.getUserCountByRole(AppConfig.ROLE_ADMIN),
                        visibleNotices
                );
            }

            @Override
            protected void done() {
                try {
                    AdminDashboardData data = get();
                    totalUsersCard.setValue(String.valueOf(data.getTotalUsers()));
                    lecturerCard.setValue(String.valueOf(data.getLecturers()));
                    coursesCard.setValue(String.valueOf(data.getCourses()));
                    noticesCard.setValue(String.valueOf(data.getVisibleNoticesCount()));
                    applyRoleBreakdown(data);
                    noticeFeedPanel.setNotices(data.getNotices());
                } catch (Exception e) {
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

    /**
     * apply role count values to summary section
     * @param data dashboard data object
     * @author janith
     */
    private void applyRoleBreakdown(AdminDashboardData data) {
        studentCountLabel.setText(String.valueOf(data.getStudents()));
        lecturerCountLabel.setText(String.valueOf(data.getLecturers()));
        technicalOfficerCountLabel.setText(String.valueOf(data.getTechnicalOfficers()));
        adminCountLabel.setText(String.valueOf(data.getAdmins()));
    }

    /**
     * build notice section title by role
     * @author janith
     */
    private String buildNoticePanelTitle() {
        if (AppConfig.ROLE_ADMIN.equalsIgnoreCase(currentUser.getRole())) {
            return "Recent Active Notices";
        }
        return currentUser.getRole() + " Notices";
    }
}
