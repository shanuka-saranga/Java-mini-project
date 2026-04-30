package com.fot.system.view.dashboard.lecturer.examEligibility;

import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.CourseService;
import com.fot.system.service.ExamEligibilityService;
import com.fot.system.view.shared_components.CloseActionButton;
import com.fot.system.view.dashboard.lecturer.shared_components.LecturerCourseCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.List;

/**
 * displays lecturer exam eligibility summaries and student eligibility rows
 * @author poornika
 */
public class LecturerExamEligibilityPanel extends JPanel {
    private static final String LIST_CARD = "LIST";
    private static final String DETAILS_CARD = "DETAILS";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final Color ELIGIBLE_ROW_COLOR = new Color(46, 204, 113, 70);
    private static final Color NOT_ELIGIBLE_ROW_COLOR = new Color(231, 76, 60, 55);
    private static final int BATCH_COLUMN_INDEX = 2;
    private static final int ELIGIBILITY_COLUMN_INDEX = 7;

    private final User currentUser;
    private final CourseService courseService;
    private final ExamEligibilityService examEligibilityService;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final JPanel courseListPanel;
    private final JLabel lblOpenedCourseTab;
    private final JLabel lblTotalStudents;
    private final JLabel lblEligibleStudents;
    private final JLabel lblNotEligibleStudents;
    private final JTextField txtSearch;
    private final JComboBox<String> cmbRegistrationYear;
    private final DefaultTableModel tableModel;
    private final JTable eligibilityTable;
    private final TableRowSorter<DefaultTableModel> rowSorter;

    private List<Course> assignedCourses;
    private Course selectedCourse;

    /**
     * initialize lecturer exam eligibility panel for the logged in lecturer
     * @param user logged in lecturer
     * @author poornika
     */
    public LecturerExamEligibilityPanel(User user) {
        this.currentUser = user;
        this.courseService = new CourseService();
        this.examEligibilityService = new ExamEligibilityService();
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

        lblOpenedCourseTab = new JLabel("Opened Course");
        lblOpenedCourseTab.setFont(AppTheme.fontBold(16));
        lblOpenedCourseTab.setForeground(AppTheme.TEXT_DARK);

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

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        summaryPanel.setOpaque(false);
        lblTotalStudents = createSummaryValueLabel("0");
        lblEligibleStudents = createSummaryValueLabel("0");
        lblNotEligibleStudents = createSummaryValueLabel("0");
        summaryPanel.add(createSummaryCard("Total Students", lblTotalStudents));
        summaryPanel.add(createSummaryCard("Eligible", lblEligibleStudents));
        summaryPanel.add(createSummaryCard("Not Eligible", lblNotEligibleStudents));

        JPanel controlsPanel = new JPanel(new BorderLayout(12, 0));
        controlsPanel.setOpaque(false);
        controlsPanel.setPreferredSize(new Dimension(0, 40));
        controlsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        txtSearch = new JTextField();
        txtSearch.setFont(AppTheme.fontPlain(13));
        txtSearch.setPreferredSize(new Dimension(420, 38));
        txtSearch.setMinimumSize(new Dimension(220, 38));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_MUTED, 1, false),
                new EmptyBorder(8, 10, 8, 10)
        ));
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applyFilters();
            }
        });

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setPreferredSize(new Dimension(420, 38));
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        controlsPanel.add(searchPanel, BorderLayout.CENTER);

        cmbRegistrationYear = new JComboBox<>();
        cmbRegistrationYear.setFont(AppTheme.fontPlain(13));
        cmbRegistrationYear.setPreferredSize(new Dimension(160, 38));
        cmbRegistrationYear.addActionListener(e -> applyFilters());
        JPanel batchFilterPanel = new JPanel(new BorderLayout(6, 0));
        batchFilterPanel.setOpaque(false);
        batchFilterPanel.setPreferredSize(new Dimension(240, 38));
        batchFilterPanel.setMaximumSize(new Dimension(240, 38));
        JLabel batchLabel = new JLabel("Batch");
        batchLabel.setFont(AppTheme.fontPlain(13));
        batchLabel.setForeground(AppTheme.TEXT_SUBTLE);
        batchFilterPanel.add(batchLabel, BorderLayout.WEST);
        batchFilterPanel.add(cmbRegistrationYear, BorderLayout.CENTER);
        controlsPanel.add(batchFilterPanel, BorderLayout.EAST);

        tableModel = new DefaultTableModel(
                new Object[]{"Reg No", "Student", "Batch", "Attendance %", "CA %", "Attendance OK", "CA OK", "Eligibility"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        eligibilityTable = new JTable(tableModel);
        eligibilityTable.setAutoCreateRowSorter(true);
        eligibilityTable.setRowHeight(28);
        eligibilityTable.setFont(AppTheme.fontPlain(13));
        eligibilityTable.setForeground(AppTheme.TEXT_DARK);
        eligibilityTable.setGridColor(AppTheme.BORDER_SOFT);
        eligibilityTable.setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
        eligibilityTable.setSelectionForeground(AppTheme.TABLE_SELECTION_FG);
        eligibilityTable.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        eligibilityTable.getTableHeader().setForeground(AppTheme.TABLE_HEADER_FG);
        eligibilityTable.getTableHeader().setFont(AppTheme.fontBold(13));
        eligibilityTable.setFillsViewportHeight(true);
        eligibilityTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    component.setBackground(table.getSelectionBackground());
                    component.setForeground(table.getSelectionForeground());
                    return component;
                }

                component.setForeground(AppTheme.TEXT_DARK);
                component.setBackground(resolveEligibilityRowColor(table, row));
                return component;
            }
        });

        rowSorter = new TableRowSorter<>(tableModel);
        eligibilityTable.setRowSorter(rowSorter);

        JScrollPane tableScrollPane = new JScrollPane(eligibilityTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false));
        tableScrollPane.getViewport().setBackground(AppTheme.CARD_BG);
        tableScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel contentStack = new JPanel();
        contentStack.setOpaque(false);
        contentStack.setLayout(new BoxLayout(contentStack, BoxLayout.Y_AXIS));
        contentStack.add(summaryPanel);
        contentStack.add(Box.createVerticalStrut(16));
        contentStack.add(controlsPanel);
        contentStack.add(Box.createVerticalStrut(14));
        contentStack.add(tableScrollPane);

        openedCoursePanel.add(panelHeader, BorderLayout.NORTH);
        openedCoursePanel.add(contentStack, BorderLayout.CENTER);
        detailsView.add(openedCoursePanel, BorderLayout.CENTER);

        cardPanel.add(courseListScrollPane, LIST_CARD);
        cardPanel.add(detailsView, DETAILS_CARD);
        add(cardPanel, BorderLayout.CENTER);

        loadAssignedCourses();
    }

    /**
     * create the exam eligibility page header
     * @author poornika
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);

        JLabel title = new JLabel("Exam Eligibility");
        title.setFont(AppTheme.fontBold(28));
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Check whole-batch and individual eligibility using attendance and current-year CA marks.");
        subtitle.setFont(AppTheme.fontPlain(14));
        subtitle.setForeground(AppTheme.TEXT_SUBTLE);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    /**
     * create a scroll pane wrapper for the given content panel
     * @param content wrapped content panel
     * @author poornika
     */
    private JScrollPane createScrollPane(JPanel content) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppTheme.CARD_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    /**
     * create a bold number label for summary cards
     * @param text summary value text
     * @author poornika
     */
    private JLabel createSummaryValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontBold(24));
        label.setForeground(AppTheme.TEXT_DARK);
        return label;
    }

    /**
     * create a summary card for one eligibility metric
     * @param title summary title
     * @param valueLabel summary value label
     * @author poornika
     */
    private JPanel createSummaryCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(AppTheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BASE_COLOR, 1, false),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.fontPlain(13));
        titleLabel.setForeground(AppTheme.TEXT_SUBTLE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    /**
     * load lecturer assigned courses for exam eligibility selection
     * @author poornika
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
                            LecturerExamEligibilityPanel.this,
                            "Failed to load assigned courses.",
                            "Eligibility Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    /**
     * render lecturer assigned courses as clickable cards
     * @author poornika
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
     * open the selected course eligibility view
     * @param course selected course
     * @author poornika
     */
    private void openCourse(Course course) {
        selectedCourse = course;
        lblOpenedCourseTab.setText(course.getCourseName());
        txtSearch.setText("");
        loadEligibilityData();
        cardLayout.show(cardPanel, DETAILS_CARD);
    }

    /**
     * load exam eligibility data for the opened course
     * @author poornika
     */
    private void loadEligibilityData() {
        if (selectedCourse == null) {
            return;
        }

        SwingWorker<CourseExamEligibilityViewData, Void> worker = new SwingWorker<>() {
            @Override
            protected CourseExamEligibilityViewData doInBackground() {
                return examEligibilityService.getCourseExamEligibilityViewData(selectedCourse.getId(), selectedCourse.getTotalHours());
            }

            @Override
            protected void done() {
                try {
                    updateView(get());
                } catch (Exception e) {
                    updateView(createEmptyViewData());
                    JOptionPane.showMessageDialog(
                            LecturerExamEligibilityPanel.this,
                            "Unable to load exam eligibility. Make sure attendance and marks data are available for the selected course.",
                            "Eligibility Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    /**
     * update summary cards, filters and table from loaded view data
     * @param viewData exam eligibility view data
     * @author poornika
     */
    private void updateView(CourseExamEligibilityViewData viewData) {
        CourseExamEligibilityViewData safeViewData = viewData == null ? createEmptyViewData() : viewData;
        updateSummary(safeViewData.getBatchSummary());
        updateBatchFilter(safeViewData.getRegistrationYears());
        updateTable(safeViewData.getRows());
        applyFilters();
    }

    /**
     * update top summary counts for the current course
     * @param summary batch summary data
     * @author poornika
     */
    private void updateSummary(ExamEligibilityBatchSummary summary) {
        ExamEligibilityBatchSummary safeSummary = summary == null ? createEmptyBatchSummary() : summary;
        lblTotalStudents.setText(String.valueOf(safeSummary.getTotalStudents()));
        lblEligibleStudents.setText(String.valueOf(safeSummary.getEligibleCount()));
        lblNotEligibleStudents.setText(String.valueOf(safeSummary.getNotEligibleCount()));
    }

    /**
     * refresh the registration year filter options
     * @param registrationYears available registration years
     * @author poornika
     */
    private void updateBatchFilter(List<Integer> registrationYears) {
        Object selected = cmbRegistrationYear.getSelectedItem();
        cmbRegistrationYear.removeAllItems();
        cmbRegistrationYear.addItem("All Batches");
        for (Integer year : registrationYears == null ? List.<Integer>of() : registrationYears) {
            cmbRegistrationYear.addItem(String.valueOf(year));
        }
        if (selected != null) {
            cmbRegistrationYear.setSelectedItem(selected);
        }
        if (cmbRegistrationYear.getSelectedIndex() < 0) {
            cmbRegistrationYear.setSelectedIndex(0);
        }
    }

    /**
     * update student eligibility table rows
     * @param rows student eligibility rows
     * @author poornika
     */
    private void updateTable(List<ExamEligibilityRow> rows) {
        tableModel.setRowCount(0);
        for (ExamEligibilityRow row : rows == null ? List.<ExamEligibilityRow>of() : rows) {
            tableModel.addRow(new Object[]{
                    row.getRegistrationNo(),
                    row.getStudentName(),
                    row.getRegistrationYear(),
                    DECIMAL_FORMAT.format(row.getAttendancePercentage()),
                    DECIMAL_FORMAT.format(row.getCaAverage()),
                    row.isAttendanceEligible() ? "YES" : "NO",
                    row.isCaEligible() ? "YES" : "NO",
                    row.isEligible() ? "ELIGIBLE" : "NOT ELIGIBLE"
            });
        }
    }

    /**
     * apply search text and batch filters to the eligibility table
     * @author poornika
     */
    private void applyFilters() {
        String searchText = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase();
        String selectedBatch = cmbRegistrationYear.getSelectedItem() == null ? "All Batches" : cmbRegistrationYear.getSelectedItem().toString();

        if (searchText.isEmpty() && "All Batches".equals(selectedBatch)) {
            rowSorter.setRowFilter(null);
            return;
        }

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
                        || selectedBatch.equals(String.valueOf(entry.getValue(BATCH_COLUMN_INDEX)));

                return matchesSearch && matchesBatch;
            }
        };

        rowSorter.setRowFilter(filter);
    }

    /**
     * resolve the background tint for an eligibility table row
     * @param table eligibility table
     * @param viewRow row index in the current table view
     * @author poornika
     */
    private Color resolveEligibilityRowColor(JTable table, int viewRow) {
        int modelRow = table.convertRowIndexToModel(viewRow);
        Object value = table.getModel().getValueAt(modelRow, ELIGIBILITY_COLUMN_INDEX);
        String eligibility = value == null ? "" : value.toString().trim();
        if ("ELIGIBLE".equalsIgnoreCase(eligibility)) {
            return ELIGIBLE_ROW_COLOR;
        }
        if ("NOT ELIGIBLE".equalsIgnoreCase(eligibility)) {
            return NOT_ELIGIBLE_ROW_COLOR;
        }
        return AppTheme.CARD_BG;
    }

    /**
     * attach one mouse listener to a component and all nested child components
     * @param component root component
     * @param adapter mouse listener to attach
     * @author poornika
     */
    private void attachClickHandler(Component component, MouseAdapter adapter) {
        component.addMouseListener(adapter);
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                attachClickHandler(child, adapter);
            }
        }
    }

    /**
     * create an empty exam eligibility view model used for fallback rendering
     * @author poornika
     */
    private CourseExamEligibilityViewData createEmptyViewData() {
        CourseExamEligibilityViewData viewData = new CourseExamEligibilityViewData();
        viewData.setRows(List.of());
        viewData.setRegistrationYears(List.of());
        viewData.setBatchSummary(createEmptyBatchSummary());
        return viewData;
    }

    /**
     * create a zero-value batch summary for empty eligibility states
     * @author poornika
     */
    private ExamEligibilityBatchSummary createEmptyBatchSummary() {
        ExamEligibilityBatchSummary summary = new ExamEligibilityBatchSummary();
        summary.setTotalStudents(0);
        summary.setEligibleCount(0);
        summary.setNotEligibleCount(0);
        return summary;
    }
}
