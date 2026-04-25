package com.fot.system.view.dashboard.lecturer.attendance;

import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.AttendanceService;
import com.fot.system.service.CourseService;
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
import java.util.List;

/**
 * show lecturer attendance dashboard with course list, session rows and student summary
 * @author poornika
 */
public class LecturerAttendancePanel extends JPanel {
    private static final String LIST_CARD = "LIST";
    private static final String DETAILS_CARD = "DETAILS";
    private static final int COL_ATTENDANCE_STATUS = 8;
    private static final int COL_MEDICAL_STATUS = 9;
    private static final String STATUS_PRESENT = "PRESENT";
    private static final String STATUS_ABSENT = "ABSENT";
    private static final String MEDICAL_APPROVED = "APPROVED";
    private static final Color MEDICAL_ROW_COLOR = new Color(46, 204, 113, 80);
    private static final Color PRESENT_ROW_COLOR = new Color(241, 196, 15, 80);
    private static final Color ABSENT_ROW_COLOR = new Color(231, 76, 60, 80);
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.00");

    private final User currentUser;
    private final CourseService courseService;
    private final AttendanceService attendanceService;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final JPanel courseListPanel;
    private final JLabel lblOpenedCourseTab;
    private final JLabel lblHeldProgress;
    private final JProgressBar heldProgressBar;
    private final DefaultTableModel tableModel;
    private final JTable attendanceTable;
    private final DefaultTableModel studentSummaryTableModel;
    private final JTable studentSummaryTable;
    private final JTextField txtSearch;

    private List<Course> assignedCourses;
    private Course selectedCourse;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private TableRowSorter<DefaultTableModel> summaryRowSorter;

    /**
     * initialize lecturer attendance panel and bind data sources
     * @param user logged in lecturer
     * @author poornika
     */
    public LecturerAttendancePanel(User user) {
        this.currentUser = user;
        this.courseService = new CourseService();
        this.attendanceService = new AttendanceService();
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

        lblHeldProgress = new JLabel("Course Progress: 0/0 hours held");
        lblHeldProgress.setFont(AppTheme.fontPlain(14));
        lblHeldProgress.setForeground(AppTheme.TEXT_SUBTLE);

        heldProgressBar = new JProgressBar(0, 100);
        heldProgressBar.setStringPainted(true);
        heldProgressBar.setValue(0);
        heldProgressBar.setString("0.00%");
        heldProgressBar.setForeground(AppTheme.PRIMARY);
        heldProgressBar.setBackground(AppTheme.SURFACE_MUTED);
        heldProgressBar.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false));

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
        closeButton.addActionListener(e -> showCourseListView());
        panelHeader.add(closeButton, BorderLayout.EAST);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search");
        searchLabel.setFont(AppTheme.fontPlain(13));
        searchLabel.setForeground(AppTheme.TEXT_SUBTLE);

        txtSearch = new JTextField();
        txtSearch.setFont(AppTheme.fontPlain(13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_MUTED, 1, false),
                new EmptyBorder(8, 10, 8, 10)
        ));
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applySearchFilter();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applySearchFilter();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applySearchFilter();
            }
        });

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        JPanel progressPanel = new JPanel();
        progressPanel.setOpaque(false);
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.add(lblHeldProgress);
        progressPanel.add(Box.createVerticalStrut(8));
        progressPanel.add(heldProgressBar);

        tableModel = new DefaultTableModel(
                new Object[]{"Reg No", "Student", "Type", "Session No", "Date", "Day", "Time", "Venue", "Status", "Medical"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        attendanceTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    component.setBackground(getSelectionBackground());
                    component.setForeground(getSelectionForeground());
                    return component;
                }

                int modelRow = convertRowIndexToModel(row);
                String attendanceStatus = safeString(tableModel.getValueAt(modelRow, COL_ATTENDANCE_STATUS));
                String medicalStatus = safeString(tableModel.getValueAt(modelRow, COL_MEDICAL_STATUS));

                component.setForeground(AppTheme.TEXT_DARK);
                component.setBackground(resolveAttendanceRowColor(attendanceStatus, medicalStatus));
                return component;
            }
        };
        attendanceTable.setAutoCreateRowSorter(true);
        attendanceTable.setRowHeight(28);
        attendanceTable.setFont(AppTheme.fontPlain(13));
        attendanceTable.setForeground(AppTheme.TEXT_DARK);
        attendanceTable.setGridColor(AppTheme.BORDER_SOFT);
        attendanceTable.setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
        attendanceTable.setSelectionForeground(AppTheme.TABLE_SELECTION_FG);
        attendanceTable.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        attendanceTable.getTableHeader().setForeground(AppTheme.TABLE_HEADER_FG);
        attendanceTable.getTableHeader().setFont(AppTheme.fontBold(13));
        attendanceTable.setFillsViewportHeight(true);

        rowSorter = new TableRowSorter<>(tableModel);
        attendanceTable.setRowSorter(rowSorter);

        JScrollPane tableScrollPane = new JScrollPane(attendanceTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false));
        tableScrollPane.getViewport().setBackground(AppTheme.CARD_BG);
        tableScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        studentSummaryTableModel = new DefaultTableModel(
                new Object[]{"Reg No", "Student", "Present", "Medical", "Absent", "Attendance %", "Eligibility"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentSummaryTable = new JTable(studentSummaryTableModel);
        studentSummaryTable.setAutoCreateRowSorter(true);
        studentSummaryTable.setRowHeight(28);
        studentSummaryTable.setFont(AppTheme.fontPlain(13));
        studentSummaryTable.setForeground(AppTheme.TEXT_DARK);
        studentSummaryTable.setGridColor(AppTheme.BORDER_SOFT);
        studentSummaryTable.setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
        studentSummaryTable.setSelectionForeground(AppTheme.TABLE_SELECTION_FG);
        studentSummaryTable.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        studentSummaryTable.getTableHeader().setForeground(AppTheme.TABLE_HEADER_FG);
        studentSummaryTable.getTableHeader().setFont(AppTheme.fontBold(13));
        studentSummaryTable.setFillsViewportHeight(true);

        summaryRowSorter = new TableRowSorter<>(studentSummaryTableModel);
        studentSummaryTable.setRowSorter(summaryRowSorter);

        JScrollPane summaryTableScrollPane = new JScrollPane(studentSummaryTable);
        summaryTableScrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false));
        summaryTableScrollPane.getViewport().setBackground(AppTheme.CARD_BG);
        summaryTableScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JLabel summaryTableTitle = new JLabel("Current Student Attendance");
        summaryTableTitle.setFont(AppTheme.fontBold(16));
        summaryTableTitle.setForeground(AppTheme.TEXT_DARK);

        JPanel lowerSection = new JPanel();
        lowerSection.setOpaque(false);
        lowerSection.setLayout(new BoxLayout(lowerSection, BoxLayout.Y_AXIS));
        lowerSection.add(summaryTableTitle);
        lowerSection.add(Box.createVerticalStrut(10));
        lowerSection.add(summaryTableScrollPane);

        JPanel contentStack = new JPanel();
        contentStack.setOpaque(false);
        contentStack.setLayout(new BoxLayout(contentStack, BoxLayout.Y_AXIS));
        contentStack.add(progressPanel);
        contentStack.add(Box.createVerticalStrut(16));
        contentStack.add(searchPanel);
        contentStack.add(Box.createVerticalStrut(14));
        contentStack.add(tableScrollPane);
        contentStack.add(Box.createVerticalStrut(18));
        contentStack.add(lowerSection);

        openedCoursePanel.add(panelHeader, BorderLayout.NORTH);
        openedCoursePanel.add(contentStack, BorderLayout.CENTER);

        detailsView.add(openedCoursePanel, BorderLayout.CENTER);

        cardPanel.add(courseListScrollPane, LIST_CARD);
        cardPanel.add(detailsView, DETAILS_CARD);

        add(cardPanel, BorderLayout.CENTER);

        loadAssignedCourses();
    }

    /**
     * create top heading section for attendance page
     * @author poornika
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);

        JLabel title = new JLabel("Attendance");
        title.setFont(AppTheme.fontBold(28));
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Open one of your assigned courses to review the recorded attendance by session.");
        subtitle.setFont(AppTheme.fontPlain(14));
        subtitle.setForeground(AppTheme.TEXT_SUBTLE);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    /**
     * create styled scroll pane wrapper
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
     * load lecturer assigned courses in background
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
                            LecturerAttendancePanel.this,
                            "Failed to load assigned courses.",
                            "Attendance Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    /**
     * render selectable lecturer course cards
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
     * open selected course details and trigger data load
     * @param course selected course
     * @author poornika
     */
    private void openCourse(Course course) {
        selectedCourse = course;
        lblOpenedCourseTab.setText(course.getCourseName());
        txtSearch.setText("");
        loadAttendanceRows();
        cardLayout.show(cardPanel, DETAILS_CARD);
    }

    /**
     * return to course list and clear active search text
     * @author poornika
     */
    private void showCourseListView() {
        txtSearch.setText("");
        selectedCourse = null;
        lblOpenedCourseTab.setText("Opened Course");
        cardLayout.show(cardPanel, LIST_CARD);
    }

    /**
     * load attendance rows and summary details for selected course
     * @author poornika
     */
    private void loadAttendanceRows() {
        if (selectedCourse == null) {
            return;
        }

        SwingWorker<CourseAttendanceViewData, Void> worker = new SwingWorker<>() {
            @Override
            protected CourseAttendanceViewData doInBackground() {
                return attendanceService.getCourseAttendanceViewData(selectedCourse.getId(), selectedCourse.getTotalHours());
            }

            @Override
            protected void done() {
                try {
                    updateView(get());
                } catch (Exception e) {
                    updateView(createEmptyViewData());
                    JOptionPane.showMessageDialog(
                            LecturerAttendancePanel.this,
                            "Unable to load attendance records. Make sure attendance, medicals, timetable_sessions, and sessions tables exist with data.",
                            "Attendance Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    /**
     * update all sections with loaded view data
     * @param viewData view data container
     * @author poornika
     */
    private void updateView(CourseAttendanceViewData viewData) {
        updateTable(viewData.getAttendanceRows());
        updateProgress(viewData.getCourseProgress());
        updateStudentSummaryTable(viewData.getStudentSummaryRows());
    }

    /**
     * bind attendance rows to main table
     * @param rows attendance rows
     * @author poornika
     */
    private void updateTable(List<AttendanceTableRow> rows) {
        tableModel.setRowCount(0);
        for (AttendanceTableRow row : rows) {
            tableModel.addRow(new Object[]{
                    row.getRegistrationNo(),
                    row.getStudentName(),
                    row.getSessionType(),
                    row.getSessionNo(),
                    row.getSessionDate(),
                    row.getSessionDay(),
                    row.getTimeRange(),
                    row.getVenue(),
                    row.getAttendanceStatus(),
                    row.getMedicalApprovalStatus()
            });
        }
    }

    /**
     * update progress label and progress bar values
     * @param progress course progress
     * @author poornika
     */
    private void updateProgress(AttendanceCourseProgress progress) {
        heldProgressBar.setValue((int) Math.round(progress.getProgressPercentage()));
        heldProgressBar.setString(PERCENT_FORMAT.format(progress.getProgressPercentage()) + "%");
        lblHeldProgress.setText("Course Progress: " + progress.getHeldHours() + "/" + progress.getTotalHours() + " hours held");
    }

    /**
     * bind student summary rows to summary table
     * @param summaryRows summary rows
     * @author poornika
     */
    private void updateStudentSummaryTable(List<StudentAttendanceSummaryRow> summaryRows) {
        studentSummaryTableModel.setRowCount(0);
        for (StudentAttendanceSummaryRow summaryRow : summaryRows) {
            studentSummaryTableModel.addRow(new Object[]{
                    summaryRow.getRegistrationNo(),
                    summaryRow.getStudentName(),
                    summaryRow.getPresentCount(),
                    summaryRow.getMedicalCount(),
                    summaryRow.getAbsentCount(),
                    PERCENT_FORMAT.format(summaryRow.getAttendancePercentage()),
                    summaryRow.isEligible() ? "ELIGIBLE" : "NOT ELIGIBLE"
            });
        }
    }

    /**
     * apply search text filter on both tables
     * @author poornika
     */
    private void applySearchFilter() {
        String text = txtSearch.getText() == null ? "" : txtSearch.getText().trim();
        if (text.isEmpty()) {
            rowSorter.setRowFilter(null);
            summaryRowSorter.setRowFilter(null);
        } else {
            RowFilter<DefaultTableModel, Object> filter =
                    RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text));
            rowSorter.setRowFilter(filter);
            summaryRowSorter.setRowFilter(filter);
        }
    }

    /**
     * attach one click handler recursively for a card tree
     * @param component root component
     * @param adapter click adapter
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
     * build fallback empty data object for error states
     * @author poornika
     */
    private CourseAttendanceViewData createEmptyViewData() {
        AttendanceCourseProgress progress = new AttendanceCourseProgress();
        progress.setHeldHours(0);
        progress.setTotalHours(selectedCourse == null ? 0 : selectedCourse.getTotalHours());
        progress.setProgressPercentage(0);

        CourseAttendanceViewData viewData = new CourseAttendanceViewData();
        viewData.setCourseProgress(progress);
        viewData.setAttendanceRows(List.of());
        viewData.setStudentSummaryRows(List.of());
        return viewData;
    }

    /**
     * resolve row highlight color based on attendance and medical statuses
     * @param attendanceStatus attendance status
     * @param medicalStatus medical approval status
     * @author poornika
     */
    private Color resolveAttendanceRowColor(String attendanceStatus, String medicalStatus) {
        if (isMedicalRow(medicalStatus)) {
            return MEDICAL_ROW_COLOR;
        }

        String normalizedStatus = normalize(attendanceStatus);
        if (STATUS_PRESENT.equals(normalizedStatus)) {
            return PRESENT_ROW_COLOR;
        }
        if (STATUS_ABSENT.equals(normalizedStatus)) {
            return ABSENT_ROW_COLOR;
        }
        return AppTheme.CARD_BG;
    }

    /**
     * identify whether row has approved/active medical status
     * @param medicalStatus medical status value
     * @author poornika
     */
    private boolean isMedicalRow(String medicalStatus) {
        String normalizedMedical = normalize(medicalStatus);
        if (normalizedMedical.isEmpty()) {
            return false;
        }
        if (MEDICAL_APPROVED.equals(normalizedMedical)) {
            return true;
        }
        return !normalizedMedical.isEmpty()
                && !"-".equals(normalizedMedical)
                && !"NONE".equals(normalizedMedical)
                && !"NO".equals(normalizedMedical)
                && !"N/A".equals(normalizedMedical)
                && !"NOT_SUBMITTED".equals(normalizedMedical)
                && !"NOT SUBMITTED".equals(normalizedMedical)
                && !"REJECTED".equals(normalizedMedical);
    }

    /**
     * convert nullable table values to text
     * @param value cell value
     * @author poornika
     */
    private String safeString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    /**
     * normalize status text for comparisons
     * @param value status value
     * @author poornika
     */
    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }
}
