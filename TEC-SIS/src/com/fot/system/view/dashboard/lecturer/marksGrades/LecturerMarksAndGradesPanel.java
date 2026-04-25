package com.fot.system.view.dashboard.lecturer.marksGrades;

import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.CourseService;
import com.fot.system.service.LecturerGradesService;
import com.fot.system.service.LecturerMarksService;
import com.fot.system.view.components.CloseActionButton;
import com.fot.system.view.dashboard.lecturer.myCourses.LecturerCourseCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.Year;
import java.util.List;

public class LecturerMarksAndGradesPanel extends JPanel {
    private static final String LIST_CARD = "LIST";
    private static final String DETAILS_CARD = "DETAILS";
    private static final String SUMMARY_VIEW = "SUMMARY";
    private static final String ITEM_VIEW = "ITEM";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    private final User currentUser;
    private final CourseService courseService;
    private final LecturerMarksService lecturerMarksService;
    private final LecturerGradesService lecturerGradesService;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final JPanel courseListPanel;
    private final JLabel lblSelectedCourse;
    private final JLabel lblSemesterSummary;
    private final JLabel lblOpenedCourseTab;
    private final CardLayout detailsCardLayout;
    private final JPanel detailsContentPanel;
    private final JPanel summaryCardsPanel;
    private final AssessmentMarksDetailPanel assessmentMarksDetailPanel;
    private final JTextField txtGradeSearch;
    private final JComboBox<String> cmbGradeBatch;
    private final DefaultTableModel gradeTableModel;
    private final JTable gradeTable;
    private final TableRowSorter<DefaultTableModel> gradeRowSorter;

    private List<Course> assignedCourses;
    private Course selectedCourse;
    private AssessmentCardSummary selectedAssessmentSummary;
    private int currentMarksYear;

    public LecturerMarksAndGradesPanel(User user) {
        this.currentUser = user;
        this.courseService = new CourseService();
        this.lecturerMarksService = new LecturerMarksService();
        this.lecturerGradesService = new LecturerGradesService();
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
        lblOpenedCourseTab.setFont(AppTheme.fontBold(16));
        lblOpenedCourseTab.setForeground(AppTheme.TEXT_DARK);
        detailsCardLayout = new CardLayout();
        detailsContentPanel = new JPanel(detailsCardLayout);
        detailsContentPanel.setOpaque(false);

        JPanel detailsView = new JPanel(new BorderLayout());
        detailsView.setOpaque(false);

        JPanel openedCoursePanel = new JPanel(new BorderLayout(0, 16));
        openedCoursePanel.setBackground(AppTheme.CARD_BG);
        openedCoursePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false),
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

        txtGradeSearch = new JTextField();
        txtGradeSearch.setFont(AppTheme.fontPlain(13));
        txtGradeSearch.setPreferredSize(new Dimension(420, 38));
        txtGradeSearch.setMinimumSize(new Dimension(220, 38));
        txtGradeSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_MUTED, 1, false),
                new EmptyBorder(8, 10, 8, 10)
        ));
        txtGradeSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applyGradeFilters();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applyGradeFilters();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applyGradeFilters();
            }
        });

        cmbGradeBatch = new JComboBox<>();
        cmbGradeBatch.setFont(AppTheme.fontPlain(13));
        cmbGradeBatch.setPreferredSize(new Dimension(160, 38));
        cmbGradeBatch.addActionListener(e -> applyGradeFilters());

        gradeTableModel = new DefaultTableModel(
                new Object[]{"Reg No", "Student", "Batch", "Attendance %", "CA %", "End %", "Final Mark", "Grade"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        gradeTable = new JTable(gradeTableModel);
        gradeTable.setAutoCreateRowSorter(true);
        gradeTable.setRowHeight(28);
        gradeTable.setFont(AppTheme.fontPlain(13));
        gradeTable.setForeground(AppTheme.TEXT_DARK);
        gradeTable.setGridColor(AppTheme.BORDER_SOFT);
        gradeTable.setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
        gradeTable.setSelectionForeground(AppTheme.TABLE_SELECTION_FG);
        gradeTable.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        gradeTable.getTableHeader().setForeground(AppTheme.TABLE_HEADER_FG);
        gradeTable.getTableHeader().setFont(AppTheme.fontBold(13));
        gradeTable.setFillsViewportHeight(true);

        gradeRowSorter = new TableRowSorter<>(gradeTableModel);
        gradeTable.setRowSorter(gradeRowSorter);

        JPanel contentStack = new JPanel();
        contentStack.setOpaque(false);
        contentStack.setLayout(new BoxLayout(contentStack, BoxLayout.Y_AXIS));
        contentStack.add(Box.createVerticalStrut(6));
        contentStack.add(lblSemesterSummary);
        contentStack.add(Box.createVerticalStrut(18));
        contentStack.add(summaryCardsPanel);
        contentStack.add(Box.createVerticalStrut(20));
        contentStack.add(createGradeSection());
        contentStack.add(Box.createVerticalGlue());
        detailsContentPanel.add(contentStack, SUMMARY_VIEW);

        assessmentMarksDetailPanel = new AssessmentMarksDetailPanel(
                () -> detailsCardLayout.show(detailsContentPanel, SUMMARY_VIEW),
                this::saveAssessmentDetailRows
        );
        detailsContentPanel.add(assessmentMarksDetailPanel, ITEM_VIEW);

        JScrollPane openedCourseScrollPane = createScrollPane(detailsContentPanel);
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
        title.setFont(AppTheme.fontBold(28));
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Open one of your assigned courses to review marks participation and assessment progress.");
        subtitle.setFont(AppTheme.fontPlain(14));
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
        label.setFont(AppTheme.fontBold(22));
        label.setForeground(AppTheme.TEXT_DARK);
        return label;
    }

    private JLabel createMetaLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontPlain(14));
        label.setForeground(AppTheme.TEXT_SUBTLE);
        return label;
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
            empty.setFont(AppTheme.fontPlain(14));
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
        detailsCardLayout.show(detailsContentPanel, SUMMARY_VIEW);
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
                CourseGradeViewData gradeViewData = lecturerGradesService.getCourseGradeViewData(selectedCourse.getId(), selectedCourse.getTotalHours());
                return new MarksViewData(context, summaries, gradeViewData);
            }

            @Override
            protected void done() {
                try {
                    MarksViewData data = get();
                    currentMarksYear = data.context.getSemesterYear();
                    lblSemesterSummary.setText("Current Year Marks Summary: " + data.context.getSemesterYear());
                    updateSummaryCards(data.summaries);
                    updateGradeView(data.gradeViewData);
                } catch (Exception e) {
                    lblSemesterSummary.setText("Current Year Marks Summary: -");
                    updateSummaryCards(List.of());
                    updateGradeView(createEmptyGradeViewData());
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
            empty.setFont(AppTheme.fontPlain(14));
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
                        summary.getMedicalCount(),
                        summary.getPendingCount()
                );
                attachClickHandler(card, new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        openAssessmentDetails(summary);
                    }
                });
                summaryCardsPanel.add(card);
            }
        }

        summaryCardsPanel.revalidate();
        summaryCardsPanel.repaint();
    }

    private JPanel createGradeSection() {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));

        JLabel sectionTitle = new JLabel("Grades");
        sectionTitle.setFont(AppTheme.fontBold(18));
        sectionTitle.setForeground(AppTheme.TEXT_DARK);

        JPanel controlsPanel = new JPanel(new BorderLayout(12, 0));
        controlsPanel.setOpaque(false);
        controlsPanel.setPreferredSize(new Dimension(0, 40));
        controlsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setPreferredSize(new Dimension(420, 38));
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        searchPanel.add(txtGradeSearch, BorderLayout.CENTER);
        controlsPanel.add(searchPanel, BorderLayout.CENTER);

        JPanel batchFilterPanel = new JPanel(new BorderLayout(6, 0));
        batchFilterPanel.setOpaque(false);
        batchFilterPanel.setPreferredSize(new Dimension(240, 38));
        batchFilterPanel.setMaximumSize(new Dimension(240, 38));
        JLabel batchLabel = new JLabel("Batch");
        batchLabel.setFont(AppTheme.fontPlain(13));
        batchLabel.setForeground(AppTheme.TEXT_SUBTLE);
        batchFilterPanel.add(batchLabel, BorderLayout.WEST);
        batchFilterPanel.add(cmbGradeBatch, BorderLayout.CENTER);
        controlsPanel.add(batchFilterPanel, BorderLayout.EAST);

        JScrollPane tableScrollPane = new JScrollPane(gradeTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false));
        tableScrollPane.getViewport().setBackground(AppTheme.CARD_BG);
        tableScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        tableScrollPane.setPreferredSize(new Dimension(0, 280));

        section.add(sectionTitle);
        section.add(Box.createVerticalStrut(10));
        section.add(controlsPanel);
        section.add(Box.createVerticalStrut(12));
        section.add(tableScrollPane);
        return section;
    }

    private void updateGradeView(CourseGradeViewData viewData) {
        updateGradeBatchFilter(viewData.getRegistrationYears());
        gradeTableModel.setRowCount(0);
        for (StudentGradeRow row : viewData.getRows()) {
            gradeTableModel.addRow(new Object[]{
                    row.getRegistrationNo(),
                    row.getStudentName(),
                    row.getRegistrationYear(),
                    formatMark(row.getAttendancePercentage()),
                    formatMark(row.getCaAverage()),
                    formatMark(row.getEndExamAverage()),
                    row.getFinalMark() == null ? "-" : formatMark(row.getFinalMark()),
                    row.getGrade()
            });
        }
        applyGradeFilters();
    }

    private void updateGradeBatchFilter(List<Integer> registrationYears) {
        Object selected = cmbGradeBatch.getSelectedItem();
        cmbGradeBatch.removeAllItems();
        cmbGradeBatch.addItem("All Batches");
        for (Integer year : registrationYears) {
            cmbGradeBatch.addItem(String.valueOf(year));
        }
        if (selected != null) {
            cmbGradeBatch.setSelectedItem(selected);
        }
        if (cmbGradeBatch.getSelectedIndex() < 0) {
            cmbGradeBatch.setSelectedIndex(0);
        }
    }

    private void applyGradeFilters() {
        String searchText = txtGradeSearch.getText() == null ? "" : txtGradeSearch.getText().trim().toLowerCase();
        String selectedBatch = cmbGradeBatch.getSelectedItem() == null ? "All Batches" : cmbGradeBatch.getSelectedItem().toString();

        RowFilter<DefaultTableModel, Object> filter = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                boolean matchesSearch = searchText.isEmpty();
                if (!matchesSearch) {
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        Object value = entry.getValue(i);
                        if (value != null && value.toString().toLowerCase().contains(searchText)) {
                            matchesSearch = true;
                            break;
                        }
                    }
                }
                boolean matchesBatch = "All Batches".equals(selectedBatch)
                        || selectedBatch.equals(String.valueOf(entry.getValue(2)));
                return matchesSearch && matchesBatch;
            }
        };
        gradeRowSorter.setRowFilter(filter);
    }

    private boolean hasSummaryData(AssessmentCardSummary summary) {
        return summary != null
                && (summary.getAttemptCount() > 0
                || summary.getAbsentCount() > 0
                || summary.getMedicalCount() > 0
                || summary.getPendingCount() > 0);
    }

    private void openAssessmentDetails(AssessmentCardSummary summary) {
        if (selectedCourse == null || summary == null) {
            return;
        }
        selectedAssessmentSummary = summary;

        SwingWorker<List<AssessmentStudentMarkRow>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<AssessmentStudentMarkRow> doInBackground() {
                return lecturerMarksService.getAssessmentRows(
                        summary.getAssessmentType(),
                        selectedCourse.getId(),
                        currentMarksYear,
                        summary.getItemNo()
                );
            }

            @Override
            protected void done() {
                try {
                    assessmentMarksDetailPanel.setAssessmentTitle(summary.getTitle());
                    assessmentMarksDetailPanel.setAssessmentType(summary.getAssessmentType());
                    assessmentMarksDetailPanel.setRows(get());
                    detailsCardLayout.show(detailsContentPanel, ITEM_VIEW);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            LecturerMarksAndGradesPanel.this,
                            "Unable to load student marks for the selected assessment.",
                            "Marks Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void saveAssessmentDetailRows() {
        if (selectedAssessmentSummary == null) {
            return;
        }

        try {
            lecturerMarksService.saveAssessmentRows(
                    selectedAssessmentSummary.getAssessmentType(),
                    selectedAssessmentSummary.getItemNo(),
                    assessmentMarksDetailPanel.getEditedRows()
            );
            JOptionPane.showMessageDialog(
                    this,
                    "Assessment marks saved successfully.",
                    "Marks Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );
            openAssessmentDetails(selectedAssessmentSummary);
            loadMarksOverview();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String formatMark(double value) {
        return DECIMAL_FORMAT.format(value);
    }

    private CourseGradeViewData createEmptyGradeViewData() {
        CourseGradeViewData viewData = new CourseGradeViewData();
        viewData.setRows(List.of());
        viewData.setRegistrationYears(List.of());
        return viewData;
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
            CourseGradeViewData gradeViewData
    ) {
    }
}
