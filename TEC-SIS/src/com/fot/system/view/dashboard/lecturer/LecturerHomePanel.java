package com.fot.system.view.dashboard.lecturer;

import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.LecturerDashboardData;
import com.fot.system.model.entity.*;
import com.fot.system.service.CourseService;
import com.fot.system.service.NoticeService;
import com.fot.system.view.components.FeedItemCard;
import com.fot.system.view.dashboard.admin.components.DashboardStatCard;
import com.fot.system.view.dashboard.admin.components.NoticeFeedPanel;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class LecturerHomePanel extends JPanel {
    private static final int COURSE_CARD_HEIGHT = 104;

    private final User currentUser;
    private final CourseService courseService;
    private final NoticeService noticeService;

    private final DashboardStatCard myCoursesCard;
    private final DashboardStatCard theoryCoursesCard;
    private final DashboardStatCard practicalCoursesCard;
    private final DashboardStatCard noticesCard;
    private final NoticeFeedPanel noticeFeedPanel;

    private JLabel totalCreditsLabel;
    private JLabel totalHoursLabel;
    private JLabel bothSessionsLabel;
    private JPanel assignedCoursesList;

    public LecturerHomePanel(User user) {
        this.currentUser = user;
        this.courseService = new CourseService();
        this.noticeService = new NoticeService();

        setBackground(AppTheme.SURFACE_SOFT);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        myCoursesCard = new DashboardStatCard("My Courses", "...", FontAwesomeSolid.BOOK_OPEN);
        theoryCoursesCard = new DashboardStatCard("Theory Courses", "...", FontAwesomeSolid.CHALKBOARD);
        practicalCoursesCard = new DashboardStatCard("Practical / Both", "...", FontAwesomeSolid.FLASK);
        noticesCard = new DashboardStatCard("Visible Notices", "...", FontAwesomeSolid.BULLHORN);
        noticeFeedPanel = new NoticeFeedPanel("Notices");

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        loadDashboardData();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(AppTheme.fontBold(28));

        JLabel subtitleLabel = new JLabel("Here is your teaching overview with assigned courses, workload details, and recent notices.");
        subtitleLabel.setFont(AppTheme.fontPlain(14));
        subtitleLabel.setForeground(AppTheme.TEXT_SUBTLE);

        header.add(welcomeLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setOpaque(false);

        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.add(myCoursesCard);
        statsGrid.add(theoryCoursesCard);
        statsGrid.add(practicalCoursesCard);
        statsGrid.add(noticesCard);

        JPanel lowerSection = new JPanel(new GridLayout(1, 2, 20, 0));
        lowerSection.setOpaque(false);
        lowerSection.add(createCourseSummaryPanel());
        lowerSection.add(noticeFeedPanel);

        content.add(statsGrid, BorderLayout.NORTH);
        content.add(lowerSection, BorderLayout.CENTER);
        return content;
    }

    private JPanel createCourseSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(AppTheme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel title = new JLabel("Teaching Summary");
        title.setFont(AppTheme.fontBold(20));

        JPanel summaryList = new JPanel(new GridLayout(3, 1, 0, 12));
        summaryList.setOpaque(false);
        totalCreditsLabel = createMiniSummaryRow("Total Credits", FontAwesomeSolid.LAYER_GROUP);
        totalHoursLabel = createMiniSummaryRow("Total Hours", FontAwesomeSolid.CLOCK);
        bothSessionsLabel = createMiniSummaryRow("Both Sessions", FontAwesomeSolid.MICROSCOPE);

        summaryList.add((Component) totalCreditsLabel.getParent());
        summaryList.add((Component) totalHoursLabel.getParent());
        summaryList.add((Component) bothSessionsLabel.getParent());

        assignedCoursesList = new JPanel();
        assignedCoursesList.setOpaque(false);
        assignedCoursesList.setLayout(new BoxLayout(assignedCoursesList, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(assignedCoursesList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppTheme.CARD_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);
        body.add(summaryList, BorderLayout.NORTH);
        body.add(scrollPane, BorderLayout.CENTER);

        panel.add(title, BorderLayout.NORTH);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createMiniSummaryRow(String labelText, FontAwesomeSolid icon) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_SOFT, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel iconLabel = new JLabel(FontIcon.of(icon, 18, AppTheme.ICON_ACCENT));
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

    private JPanel createCourseCard(Course course) {
        return new FeedItemCard(
                course.getCourseCode() + " - " + course.getCourseName(),
                "Session: " + valueOrDash(course.getSessionType()) + "  |  Credits: " + course.getCredits() + "  |  Hours: " + course.getTotalHours(),
                "Department: " + valueOrDash(course.getDepartmentName()),
                COURSE_CARD_HEIGHT
        );
    }

    private void loadDashboardData() {
        SwingWorker<LecturerDashboardData, Void> worker = new SwingWorker<LecturerDashboardData, Void>() {
            @Override
            protected LecturerDashboardData doInBackground() {
                List<Course> assignedCourses = courseService.getCoursesByLecturerId(currentUser.getId());
                List<Notice> visibleNotices = noticeService.getRecentVisibleNoticesForRole(currentUser.getRole(), 6);

                int theoryCourses = 0;
                int practicalOrBoth = 0;
                int bothSessions = 0;
                int totalCredits = 0;
                int totalHours = 0;

                for (Course course : assignedCourses) {
                    totalCredits += course.getCredits();
                    totalHours += course.getTotalHours();

                    if ("THEORY".equalsIgnoreCase(course.getSessionType())) {
                        theoryCourses++;
                    }
                    if ("PRACTICAL".equalsIgnoreCase(course.getSessionType())
                            || "BOTH".equalsIgnoreCase(course.getSessionType())) {
                        practicalOrBoth++;
                    }
                    if ("BOTH".equalsIgnoreCase(course.getSessionType())) {
                        bothSessions++;
                    }
                }

                return new LecturerDashboardData(
                        assignedCourses,
                        theoryCourses,
                        practicalOrBoth,
                        bothSessions,
                        totalCredits,
                        totalHours,
                        noticeService.getVisibleNoticeCountForRole(currentUser.getRole()),
                        visibleNotices
                );
            }

            @Override
            protected void done() {
                try {
                    LecturerDashboardData data = get();
                    myCoursesCard.setValue(String.valueOf(data.getAssignedCourses().size()));
                    theoryCoursesCard.setValue(String.valueOf(data.getTheoryCourses()));
                    practicalCoursesCard.setValue(String.valueOf(data.getPracticalOrBothCourses()));
                    noticesCard.setValue(String.valueOf(data.getVisibleNoticesCount()));

                    totalCreditsLabel.setText(String.valueOf(data.getTotalCredits()));
                    totalHoursLabel.setText(String.valueOf(data.getTotalHours()));
                    bothSessionsLabel.setText(String.valueOf(data.getBothSessions()));

                    applyAssignedCourses(data.getAssignedCourses());
                    noticeFeedPanel.setNotices(data.getNotices());
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                            LecturerHomePanel.this,
                            "Failed to load lecturer dashboard data.",
                            "Dashboard Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void applyAssignedCourses(List<Course> courses) {
        assignedCoursesList.removeAll();

        JLabel sectionTitle = new JLabel("Assigned Courses");
        sectionTitle.setFont(AppTheme.fontBold(16));
        sectionTitle.setBorder(new EmptyBorder(4, 0, 8, 0));
        assignedCoursesList.add(sectionTitle);

        if (courses == null || courses.isEmpty()) {
            JLabel emptyLabel = new JLabel("No courses are assigned to this lecturer yet.");
            emptyLabel.setFont(AppTheme.fontPlain(14));
            emptyLabel.setForeground(AppTheme.TEXT_SUBTLE);
            emptyLabel.setBorder(new EmptyBorder(12, 8, 12, 8));
            assignedCoursesList.add(emptyLabel);
        } else {
            for (Course course : courses) {
                assignedCoursesList.add(createCourseCard(course));
                assignedCoursesList.add(Box.createVerticalStrut(12));
            }
        }

        assignedCoursesList.revalidate();
        assignedCoursesList.repaint();
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
