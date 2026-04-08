package com.fot.system.view.dashboard.lecturer.marksGrades;

import com.fot.system.config.AppTheme;
import com.fot.system.model.AssessmentCardSummary;
import com.fot.system.model.Course;
import com.fot.system.model.CourseSemesterContext;
import com.fot.system.model.StudentMarksOverviewRow;
import com.fot.system.model.User;
import com.fot.system.service.CourseService;
import com.fot.system.service.LecturerMarksService;
import com.fot.system.view.components.CloseActionButton;
import com.fot.system.view.components.SectionCard;
import com.fot.system.view.dashboard.lecturer.myCourses.LecturerCourseCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.Year;
import java.util.List;

public class LecturerMarksAndGradesPanel extends JPanel {
    private static final String LIST_CARD = "LIST";
    private static final String DETAILS_CARD = "DETAILS";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    private final User currentUser;
    private final CourseService courseService;
    private final LecturerMarksService lecturerMarksService;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final JPanel courseListPanel;
    private final JLabel lblSelectedCourse;
    private final JLabel lblSemesterSummary;
    private final JLabel lblOpenedCourseTab;
    private final JPanel summaryCardsPanel;
    private final JTable overviewTable;
    private final DefaultTableModel overviewTableModel;

    private List<Course> assignedCourses;
    private Course selectedCourse;

    public LecturerMarksAndGradesPanel(User user) {
        this.currentUser = user;
        this.courseService = new CourseService();
        this.lecturerMarksService = new LecturerMarksService();
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(createHeader(), BorderLayout.NORTH);
        cardPanel.setOpaque(false);
        courseListPanel = new JPanel();
        courseListPanel.setOpaque(true);
        courseListPanel.setBackground(AppTheme.SURFACE_SOFT);
        courseListPanel.setLayout(new BoxLayout(courseListPanel, BoxLayout.Y_AXIS));
        JScrollPane courseListScrollPane = createScrollPane(courseListPanel);
        courseListScrollPane.getViewport().setBackground(AppTheme.SURFACE_SOFT);

        lblSelectedCourse = createTitleLabel("-");
        lblSemesterSummary = createMetaLabel("-");
        lblOpenedCourseTab = new JLabel("Opened Course");
        lblOpenedCourseTab.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblOpenedCourseTab.setForeground(AppTheme.TEXT_DARK);

        JPanel detailsView = new JPanel(new BorderLayout());
        detailsView.setOpaque(false);

        JPanel openedCoursePanel = new JPanel(new BorderLayout(0, 16));
        openedCoursePanel.setBackground(AppTheme.CARD_BG);
        openedCoursePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(22, 22, 22, 22)
        ));

        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);
        panelHeader.add(lblOpenedCourseTab, BorderLayout.WEST);

        CloseActionButton closeButton = new CloseActionButton();
        closeButton.addActionListener(e -> cardLayout.show(cardPanel, LIST_CARD));
        panelHeader.add(closeButton, BorderLayout.EAST);

        summaryCardsPanel = new JPanel(new GridLayout(0, 3, 14, 14));
        summaryCardsPanel.setOpaque(false);
        SectionCard summarySection = new SectionCard(
                "Marks Summary",
                "Current year and semester assessment summary for each available course item."
        );
        summarySection.setContent(summaryCardsPanel);

        overviewTableModel = new DefaultTableModel(
                new String[]{"Registration No", "Type", "Attempt", "Quizzes", "Assignments", "Mid Theory", "Mid Practical", "End Theory", "End Practical"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        overviewTable = new JTable(overviewTableModel);
        overviewTable.setRowHeight(28);
        overviewTable.setFillsViewportHeight(true);
        overviewTable.getTableHeader().setReorderingAllowed(false);
        overviewTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleOverviewTable();

        JScrollPane tableScrollPane = new JScrollPane(overviewTable);
        tableScrollPane.setBorder(null);
        tableScrollPane.getViewport().setBackground(AppTheme.CARD_BG);

        SectionCard tableSection = new SectionCard(
                "Student Marks Overview",
                "Track quiz, assignment, mid, and end-exam participation statuses for each student attempt."
        );
        tableSection.setContent(tableScrollPane);

        JPanel contentStack = new JPanel();
        contentStack.setOpaque(false);
        contentStack.setLayout(new BoxLayout(contentStack, BoxLayout.Y_AXIS));
        contentStack.add(Box.createVerticalStrut(6));
        contentStack.add(lblSemesterSummary);
        contentStack.add(Box.createVerticalStrut(18));
        contentStack.add(summarySection);
        contentStack.add(Box.createVerticalStrut(18));
        contentStack.add(tableSection);
        contentStack.add(Box.createVerticalGlue());

        JScrollPane openedCourseScrollPane = createScrollPane(contentStack);
        openedCourseScrollPane.getViewport().setBackground(AppTheme.CARD_BG);

        openedCoursePanel.add(panelHeader, BorderLayout.NORTH);
        openedCoursePanel.add(openedCourseScrollPane, BorderLayout.CENTER);
        detailsView.add(openedCoursePanel, BorderLayout.CENTER);

        cardPanel.add(courseListScrollPane, LIST_CARD);
        cardPanel.add(detailsView, DETAILS_CARD);

        add(cardPanel, BorderLayout.CENTER);
        loadAssignedCourses();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);

        JLabel title = new JLabel("Marks / Grades");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Open one of your assigned courses to review marks participation and assessment progress.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(AppTheme.TEXT_SUBTLE);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JScrollPane createScrollPane(JPanel content) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppTheme.CARD_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        label.setForeground(AppTheme.TEXT_DARK);
        return label;
    }

    private JLabel createMetaLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(AppTheme.TEXT_SUBTLE);
        return label;
    }

    private void styleOverviewTable() {
        overviewTable.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        overviewTable.getTableHeader().setForeground(AppTheme.TABLE_HEADER_FG);
        overviewTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        overviewTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        overviewTable.setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
        overviewTable.setSelectionForeground(AppTheme.TABLE_SELECTION_FG);
        overviewTable.setGridColor(AppTheme.BORDER_LIGHT);
    }

    private void loadAssignedCourses() {
        SwingWorker<List<Course>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Course> doInBackground() {
                return courseService.getCoursesByLecturerId(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    assignedCourses = get();
                    renderCourseList();
                    cardLayout.show(cardPanel, LIST_CARD);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            LecturerMarksAndGradesPanel.this,
                            "Failed to load assigned courses.",
                            "Marks Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void renderCourseList() {
        courseListPanel.removeAll();

        if (assignedCourses == null || assignedCourses.isEmpty()) {
            JLabel empty = new JLabel("No assigned courses available.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setForeground(AppTheme.TEXT_SUBTLE);
            empty.setBorder(new EmptyBorder(12, 8, 12, 8));
            courseListPanel.add(empty);
        } else {
            for (Course course : assignedCourses) {
                LecturerCourseCard card = new LecturerCourseCard(course, false);
                MouseAdapter openCourseHandler = new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        openCourse(course);
                    }
                };
                attachClickHandler(card, openCourseHandler);
                courseListPanel.add(card);
                courseListPanel.add(Box.createVerticalStrut(12));
            }
        }

        courseListPanel.revalidate();
        courseListPanel.repaint();
    }

    private void openCourse(Course course) {
        selectedCourse = course;
        lblOpenedCourseTab.setText(course.getCourseName());
        lblSelectedCourse.setText(course.getCourseName());
        loadMarksOverview();
        cardLayout.show(cardPanel, DETAILS_CARD);
    }

    private void loadMarksOverview() {
        if (selectedCourse == null) {
            return;
        }

        SwingWorker<MarksViewData, Void> worker = new SwingWorker<>() {
            @Override
            protected MarksViewData doInBackground() {
                int currentYear = Year.now().getValue();
                CourseSemesterContext context = lecturerMarksService.getCurrentSemesterContext(selectedCourse.getId(), currentYear);
                List<AssessmentCardSummary> summaries = buildAssessmentSummaries(context);
                List<StudentMarksOverviewRow> rows = lecturerMarksService.getStudentMarksOverviewByCourse(selectedCourse.getId(), context.getSemesterYear());
                return new MarksViewData(context, summaries, rows);
            }

            @Override
            protected void done() {
                try {
                    MarksViewData data = get();
                    lblSemesterSummary.setText("Current Year Marks Summary: " + data.context.getSemesterYear());
                    updateSummaryCards(data.summaries);
                    updateOverviewTable(data.rows);
                } catch (Exception e) {
                    lblSemesterSummary.setText("Current Year Marks Summary: -");
                    updateSummaryCards(List.of());
                    updateOverviewTable(List.of());
                    JOptionPane.showMessageDialog(
                            LecturerMarksAndGradesPanel.this,
                            "Unable to load marks overview. Make sure the new marks tables and status columns exist in your database.",
                            "Marks Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private List<AssessmentCardSummary> buildAssessmentSummaries(CourseSemesterContext context) {
        List<AssessmentCardSummary> summaries = new java.util.ArrayList<>();
        summaries.addAll(lecturerMarksService.getQuizCardSummaries(
                selectedCourse.getId(),
                context.getSemesterYear(),
                selectedCourse.getNoOfQuizzes()
        ));
        summaries.addAll(lecturerMarksService.getAssignmentCardSummaries(
                selectedCourse.getId(),
                context.getSemesterYear(),
                selectedCourse.getNoOfAssignments()
        ));

        AssessmentCardSummary midSummary = lecturerMarksService.getMidExamSummary(
                selectedCourse.getId(),
                context.getSemesterYear()
        );
        if (hasSummaryData(midSummary)) {
            summaries.add(midSummary);
        }

        AssessmentCardSummary endSummary = lecturerMarksService.getEndExamSummary(
                selectedCourse.getId(),
                context.getSemesterYear()
        );
        if (hasSummaryData(endSummary)) {
            summaries.add(endSummary);
        }

        return summaries;
    }

    private void updateSummaryCards(List<AssessmentCardSummary> summaries) {
        summaryCardsPanel.removeAll();

        if (summaries == null || summaries.isEmpty()) {
            JLabel empty = new JLabel("No assessment summary data available for the current year and semester.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setForeground(AppTheme.TEXT_SUBTLE);
            summaryCardsPanel.setLayout(new BorderLayout());
            summaryCardsPanel.add(empty, BorderLayout.CENTER);
        } else {
            summaryCardsPanel.setLayout(new GridLayout(0, 3, 14, 14));
            for (AssessmentCardSummary summary : summaries) {
                AssessmentSummaryCard card = new AssessmentSummaryCard();
                card.setSummary(
                        summary.getTitle(),
                        formatMark(summary.getAverageMark()),
                        summary.getAttemptCount(),
                        summary.getAbsentCount(),
                        summary.getMedicalCount()
                );
                summaryCardsPanel.add(card);
            }
        }

        summaryCardsPanel.revalidate();
        summaryCardsPanel.repaint();
    }

    private boolean hasSummaryData(AssessmentCardSummary summary) {
        return summary != null
                && (summary.getAttemptCount() > 0
                || summary.getAbsentCount() > 0
                || summary.getMedicalCount() > 0
                || summary.getPendingCount() > 0);
    }

    private void updateOverviewTable(List<StudentMarksOverviewRow> rows) {
        overviewTableModel.setRowCount(0);
        if (rows == null || rows.isEmpty()) {
            overviewTableModel.addRow(new Object[]{"No marks data", "-", "-", "-", "-", "-", "-", "-", "-"});
            return;
        }

        for (StudentMarksOverviewRow row : rows) {
            overviewTableModel.addRow(new Object[]{
                    row.getRegistrationNo(),
                    row.getStudentType(),
                    row.getAttemptNo(),
                    row.getQuizzesCompleted(),
                    row.getAssignmentsCompleted(),
                    row.getMidTheoryStatus(),
                    row.getMidPracticalStatus(),
                    row.getEndTheoryStatus(),
                    row.getEndPracticalStatus()
            });
        }
    }

    private String formatMark(double value) {
        return DECIMAL_FORMAT.format(value);
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private void attachClickHandler(Component component, MouseAdapter adapter) {
        component.addMouseListener(adapter);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                attachClickHandler(child, adapter);
            }
        }
    }

    private record MarksViewData(
            CourseSemesterContext context,
            List<AssessmentCardSummary> summaries,
            List<StudentMarksOverviewRow> rows
    ) {
    }
}
