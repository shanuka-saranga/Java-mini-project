package com.fot.system.view.dashboard.lecturer.marksGrades;

import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.CourseService;
import com.fot.system.service.LecturerGradesService;
import com.fot.system.service.LecturerMarksService;
import com.fot.system.view.shared_components.CloseActionButton;
import com.fot.system.view.dashboard.lecturer.shared_components.LecturerCourseCard;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.Year;
import java.util.List;

/**
 * manages lecturer marks entry, assessment summaries, and grade overview views
 * @author janith
 */
public class LecturerMarksAndGradesPanel extends JPanel {
    private static final String LIST_CARD = "LIST";
    private static final String DETAILS_CARD = "DETAILS";
    private static final String SUMMARY_VIEW = "SUMMARY";
    private static final String ITEM_VIEW = "ITEM";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final boolean ENABLE_GRADE_FLOW_LOGS = true;

    private final User currentUser;
    private final CourseService courseService;
    private final LecturerMarksService lecturerMarksService;
    private final LecturerGradesService lecturerGradesService;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final JPanel courseListPanel;
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

    /**
     * Creates the lecturer marks and grades panel for the logged-in lecturer.
     * @param user logged-in lecturer user
     * @author janith
     */
    public LecturerMarksAndGradesPanel(User user) {
        this.currentUser = user;
        this.courseService = new CourseService();
        this.lecturerMarksService = new LecturerMarksService();
        this.lecturerGradesService = new LecturerGradesService();
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);

        // lecture marks and grade panel styles
        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(24, 24, 24, 24));
        add(createHeader(), BorderLayout.NORTH);

        cardPanel.setOpaque(false); // background
        courseListPanel = new JPanel();
        courseListPanel.setOpaque(true);
        courseListPanel.setBackground(AppTheme.SURFACE_SOFT);
        courseListPanel.setLayout(new BoxLayout(courseListPanel, BoxLayout.Y_AXIS));
        JScrollPane courseListScrollPane = createScrollPane(courseListPanel);
        courseListScrollPane.getViewport().setBackground(AppTheme.SURFACE_SOFT);

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
        txtGradeSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyGradeFilters();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyGradeFilters();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyGradeFilters();
            }
        });

        cmbGradeBatch = new JComboBox<>();
        cmbGradeBatch.setFont(AppTheme.fontPlain(13));
        cmbGradeBatch.setPreferredSize(new Dimension(160, 38));
        cmbGradeBatch.addActionListener(e -> applyGradeFilters());
        gradeTableModel = new DefaultTableModel(
                new Object[]{"Reg No", "Student", "Batch", "CA", "End", "Final Mark", "Grade"},
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

    /**
     * Creates the page header for the marks and grades view.
     * @author janith
     */
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

    /**
     * Wraps a panel inside a scroll pane with consistent styling.
     * @param content panel content to wrap
     * @author janith
     */
    private JScrollPane createScrollPane(JPanel content) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppTheme.CARD_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    /**
     * Creates a muted metadata label used in the summary view.
     * @param text initial label text
     * @author janith
     */
    private JLabel createMetaLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontPlain(14));
        label.setForeground(AppTheme.TEXT_SUBTLE);
        return label;
    }

    /**
     * Loads the lecturer's assigned courses and renders the course list.
     * @author janith
     */
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

    /**
     * Renders lecturer course cards for all assigned courses.
     * @author janith
     */
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

    /**
     * Opens the selected course and loads its marks overview data.
     * @param course selected course
     * @author janith
     */
    private void openCourse(Course course) {
        selectedCourse = course;
        logGradeFlow("openCourse -> courseId=" + course.getId() + ", courseCode=" + course.getCourseCode());
        lblOpenedCourseTab.setText(course.getCourseName());
        detailsCardLayout.show(detailsContentPanel, SUMMARY_VIEW);
        loadMarksOverview();
        cardLayout.show(cardPanel, DETAILS_CARD);
    }

    /**
     * Loads assessment summaries and grade data for the opened course.
     * @author janith
     */
    private void loadMarksOverview() {
        if (selectedCourse == null) {
            return;
        }

        SwingWorker<MarksViewData, Void> worker = new SwingWorker<>() {
            @Override
            protected MarksViewData doInBackground() {
                int currentYear = Year.now().getValue();
                logGradeFlow("loadMarksOverview.start -> courseId=" + selectedCourse.getId() + ", year=" + currentYear);
                CourseSemesterContext context = lecturerMarksService.getCurrentSemesterContext(selectedCourse.getId(), currentYear);
                List<AssessmentCardSummary> summaries = buildAssessmentSummaries(context);
                CourseGradeViewData gradeViewData = lecturerGradesService.getCourseGradeViewData(selectedCourse.getId());
                logGradeFlow("loadMarksOverview.fetched -> summaryCards=" + summaries.size()
                        + ", gradeRows=" + (gradeViewData.getRows() == null ? 0 : gradeViewData.getRows().size()));
                return new MarksViewData(context, summaries, gradeViewData);
            }

            @Override
            protected void done() {
                try {
                    MarksViewData data = get();
                    currentMarksYear = data.context.getSemesterYear();
                    logGradeFlow("loadMarksOverview.done -> currentMarksYear=" + currentMarksYear);
                    lblSemesterSummary.setText("Current Year Marks Summary: " + data.context.getSemesterYear());
                    updateSummaryCards(data.summaries);
                    updateGradeView(data.gradeViewData);
                } catch (Exception e) {
                    logGradeFlow("loadMarksOverview.error -> " + e.getMessage());
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

    /**
     * Builds the full set of assessment summary cards for the current course.
     * @param context current course semester context
     * @author janith
     */
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
        summaries.add(midSummary);

        AssessmentCardSummary endSummary = lecturerMarksService.getEndExamSummary(
                selectedCourse.getId(),
                context.getSemesterYear()
        );
        summaries.add(endSummary);

        return summaries;
    }

    /**
     * Refreshes the assessment summary card grid with the latest summary data.
     * @param summaries summary cards to display
     * @author janith
     */
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

    /**
     * Creates the grade section containing filters and the grade table.
     * @author janith
     */
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

    /**
     * Updates the grade table with the latest calculated student grade rows.
     * @param viewData grade view data to render
     * @author janith
     */
    private void updateGradeView(CourseGradeViewData viewData) {
        updateGradeBatchFilter(viewData.getRegistrationYears());
        gradeTableModel.setRowCount(0);
        for (StudentGradeRow row : viewData.getRows()) {
            gradeTableModel.addRow(new Object[]{
                    row.getRegistrationNo(),
                    row.getStudentName(),
                    row.getRegistrationYear(),
                    formatMark(row.getCaAverage()),
                    formatMark(row.getEndExamAverage()),
                    row.getFinalMark() == null ? "-" : formatMark(row.getFinalMark()),
                    row.getGrade()
            });
        }
        logGradeFlow("updateGradeView -> renderedRows=" + gradeTableModel.getRowCount()
                + ", batches=" + (viewData.getRegistrationYears() == null ? 0 : viewData.getRegistrationYears().size()));
        applyGradeFilters();
    }

    /**
     * Reloads the batch filter options using the available registration years.
     * @param registrationYears registration years available in the grade view
     * @author janith
     */
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

    /**
     * Applies the current search text and batch filter to the grade table.
     * @author janith
     */
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

    /**
     * Loads the selected assessment item and opens the editable marks detail view.
     * @param summary selected assessment summary card
     * @author janith
     */
    private void openAssessmentDetails(AssessmentCardSummary summary) {
        if (selectedCourse == null || summary == null) {
            return;
        }
        selectedAssessmentSummary = summary;

        SwingWorker<List<AssessmentStudentMarkRow>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<AssessmentStudentMarkRow> doInBackground() {
                try {
                    List<AssessmentStudentMarkRow> rows = lecturerMarksService.getAssessmentRows(
                            summary.getAssessmentType(),
                            selectedCourse.getId(),
                            currentMarksYear,
                            summary.getItemNo()
                    );
                    return normalizeAssessmentRowsForCourse(summary, rows);
                }catch (Exception e) {
                    logGradeFlow("openAssessmentDetails.error -> " + e.getMessage());
                    throw new RuntimeException("Failed to load assessment details. Make sure the new marks tables and status columns exist in your database.");
                }
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

    /**
     * Ensures BOTH session courses always show THEORY and PRACTICAL rows for each student attempt.
     * @param summary selected assessment summary card
     * @param rows loaded assessment rows
     * @author janith
     */
    private List<AssessmentStudentMarkRow> normalizeAssessmentRowsForCourse(
            AssessmentCardSummary summary,
            List<AssessmentStudentMarkRow> rows
    ) {
        List<AssessmentStudentMarkRow> sourceRows = rows == null ? List.of() : rows;
        if (selectedCourse == null
                || !"BOTH".equalsIgnoreCase(selectedCourse.getSessionType())
                || summary == null
                || (!"MID".equalsIgnoreCase(summary.getAssessmentType())
                && !"END".equalsIgnoreCase(summary.getAssessmentType()))) {
            return sourceRows;
        }

        java.util.Map<String, java.util.Map<String, AssessmentStudentMarkRow>> groupedRows = new java.util.LinkedHashMap<>();
        for (AssessmentStudentMarkRow row : sourceRows) {
            String groupKey = row.getRegistrationNo() + "|" + row.getAttemptNo();
            groupedRows
                    .computeIfAbsent(groupKey, key -> new java.util.LinkedHashMap<>())
                    .put(normalizeExamType(row.getExamType()), row);
        }

        List<AssessmentStudentMarkRow> normalizedRows = new java.util.ArrayList<>();
        for (java.util.Map<String, AssessmentStudentMarkRow> examTypeRows : groupedRows.values()) {
            AssessmentStudentMarkRow theoryRow = examTypeRows.get("THEORY");
            AssessmentStudentMarkRow practicalRow = examTypeRows.get("PRACTICAL");

            if (theoryRow == null && practicalRow != null) {
                theoryRow = createMissingExamTypeRow(practicalRow, "THEORY");
            }
            if (practicalRow == null && theoryRow != null) {
                practicalRow = createMissingExamTypeRow(theoryRow, "PRACTICAL");
            }
            if (theoryRow != null) {
                normalizedRows.add(theoryRow);
            }
            if (practicalRow != null) {
                normalizedRows.add(practicalRow);
            }
        }
        return normalizedRows;
    }

    /**
     * Creates an empty exam row when one exam type is missing for a BOTH session course.
     * @param source existing row used as the base student record
     * @param examType missing exam type to create
     * @author janith
     */
    private AssessmentStudentMarkRow createMissingExamTypeRow(AssessmentStudentMarkRow source, String examType) {
        AssessmentStudentMarkRow row = new AssessmentStudentMarkRow();
        row.setMarkId(source.getMarkId());
        row.setRegistrationNo(source.getRegistrationNo());
        row.setAttemptNo(source.getAttemptNo());
        row.setExamType(examType);
        row.setStatus("");
        row.setMark(null);
        return row;
    }

    /**
     * Normalizes exam type values before grouping THEORY and PRACTICAL rows.
     * @param examType raw exam type text
     * @author janith
     */
    private String normalizeExamType(String examType) {
        if (examType == null || examType.isBlank()) {
            return "";
        }
        return examType.trim().toUpperCase();
    }

    /**
     * Validates and saves the currently edited assessment marks rows.
     * @author janith
     */
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

    /**
     * Formats a numeric mark value for table and card display.
     * @param value numeric mark value
     * @author janith
     */
    private String formatMark(double value) {
        return DECIMAL_FORMAT.format(value);
    }

    /**
     * Creates an empty grade view model used as a safe fallback state.
     * @author janith
     */
    private CourseGradeViewData createEmptyGradeViewData() {
        CourseGradeViewData viewData = new CourseGradeViewData();
        viewData.setRows(List.of());
        viewData.setRegistrationYears(List.of());
        return viewData;
    }

    /**
     * Attaches the same click handler to a component and all of its children.
     * @param component root component to attach
     * @param adapter mouse adapter to register
     * @author janith
     */
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

    /**
     * Writes grade flow debug messages when logging is enabled.
     * @param message debug message text
     * @author janith
     */
    private void logGradeFlow(String message) {
        if (ENABLE_GRADE_FLOW_LOGS) {
            System.out.println("[GRADE-FLOW][UI] " + message);
        }
    }
}
