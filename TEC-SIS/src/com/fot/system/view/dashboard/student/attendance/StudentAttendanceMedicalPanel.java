package com.fot.system.view.dashboard.student.attendance;

import com.fot.system.config.AppTheme;
import com.fot.system.controller.AddStudentMedicalController;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.AttendanceService;
import com.fot.system.service.FileOpenService;
import com.fot.system.view.components.CustomButton;
import com.fot.system.view.components.MaterialActionButton;
import com.fot.system.view.components.ThemedDatePicker;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StudentAttendanceMedicalPanel extends JPanel {
    private final User currentUser;
    private final AttendanceService attendanceService;
    private final AddStudentMedicalController addStudentMedicalController;
    private final FileOpenService fileOpenService;
    private DefaultTableModel attendanceTableModel;
    private DefaultTableModel medicalTableModel;
    private DefaultTableModel absentSessionTableModel;
    private JTable attendanceTable;
    private JTable medicalTable;
    private JTable absentSessionTable;
    private JPanel courseAttendanceSummaryPanel;
    private DefaultTableModel medicalDetailsTableModel;
    private JPanel medicalDetailsPanel;
    private JLabel lblMedicalDetailsMeta;
    private JTextField txtSearch;
    private JLabel lblAttendanceMeta;
    private ThemedDatePicker txtMedicalStartDate;
    private ThemedDatePicker txtMedicalEndDate;
    private JTextField txtMedicalDocument;
    private JPanel medicalUploadPanel;
    private JLabel lblAbsentSessionMeta;
    private TableRowSorter<DefaultTableModel> attendanceSorter;
    private List<AbsentSessionOption> absentSessionOptions;
    private List<StudentMedicalRow> medicalRows;

    /**
     * initialize student attendance and medical panel
     * @param user logged in student user
     * @author shanuka
     */
    public StudentAttendanceMedicalPanel(User user) {
        this.currentUser = user;
        this.attendanceService = new AttendanceService();
        this.addStudentMedicalController = new AddStudentMedicalController();
        this.fileOpenService = new FileOpenService();

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(createHeader(), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 16));
        contentPanel.setOpaque(false);
        contentPanel.add(createTopMeta(), BorderLayout.NORTH);
        contentPanel.add(createTablesArea(), BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppTheme.SURFACE_SOFT);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
        loadStudentAttendanceMedicalData();
    }

    /**
     * create attendance panel header section
     * @return header panel
     * @author shanuka
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);

        JLabel title = new JLabel("Attendance");
        title.setFont(AppTheme.fontBold(28));
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Review all recorded session attendance and submitted medical records for your student account.");
        subtitle.setFont(AppTheme.fontPlain(14));
        subtitle.setForeground(AppTheme.TEXT_SUBTLE);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    /**
     * create attendance meta and search section
     * @return top meta panel
     * @author shanuka
     */
    private JPanel createTopMeta() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);

        lblAttendanceMeta = new JLabel("Loading attendance records...");
        lblAttendanceMeta.setFont(AppTheme.fontPlain(14));
        lblAttendanceMeta.setForeground(AppTheme.TEXT_SUBTLE);

        txtSearch = new JTextField();
        txtSearch.setFont(AppTheme.fontPlain(13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_MUTED, 1, false),
                new EmptyBorder(8, 10, 8, 10)
        ));
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applyAttendanceFilter();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applyAttendanceFilter();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applyAttendanceFilter();
            }
        });

        JPanel searchWrap = new JPanel(new BorderLayout(8, 0));
        searchWrap.setOpaque(false);
        searchWrap.add(new JLabel("Search Sessions"), BorderLayout.WEST);
        searchWrap.add(txtSearch, BorderLayout.CENTER);

        panel.add(lblAttendanceMeta, BorderLayout.WEST);
        panel.add(searchWrap, BorderLayout.CENTER);
        return panel;
    }

    /**
     * create attendance, medical, and medical submission table area
     * @return tables area component
     * @author shanuka
     */
    private JComponent createTablesArea() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        attendanceTableModel = new DefaultTableModel(
                new Object[]{"Course Code", "Course Name", "Type", "Session No", "Date", "Day", "Time", "Venue", "Session", "Attendance"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        attendanceTable = createStyledTable(attendanceTableModel);
        attendanceSorter = new TableRowSorter<>(attendanceTableModel);
        attendanceTable.setRowSorter(attendanceSorter);
        courseAttendanceSummaryPanel = createCourseAttendanceSummaryPanel();

        medicalTableModel = new DefaultTableModel(
                new Object[]{"Medical ID", "Sessions", "Submitted Date", "Approval", "Approved At", "Document"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        medicalTable = createStyledTable(medicalTableModel);
        TableColumn idColumn = medicalTable.getColumnModel().getColumn(0);
        idColumn.setMinWidth(0);
        idColumn.setMaxWidth(0);
        idColumn.setPreferredWidth(0);
        TableColumn documentColumn = medicalTable.getColumnModel().getColumn(5);
        documentColumn.setPreferredWidth(90);
        documentColumn.setMaxWidth(90);
        documentColumn.setMinWidth(90);
        documentColumn.setCellRenderer(new DocumentActionCellRenderer());
        documentColumn.setCellEditor(new DocumentActionCellEditor());
        medicalDetailsTableModel = new DefaultTableModel(
                new Object[]{"Course Code", "Course Name", "Type", "Session No", "Session Date"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        lblMedicalDetailsMeta = new JLabel("Select a medical row to view its linked sessions.");
        lblMedicalDetailsMeta.setFont(AppTheme.fontPlain(13));
        lblMedicalDetailsMeta.setForeground(AppTheme.TEXT_SUBTLE);
        medicalDetailsPanel = createMedicalDetailsPanel();
        medicalTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedMedicalDetails();
            }
        });

        panel.add(createSectionLabel("Course Attendance"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(courseAttendanceSummaryPanel);
        panel.add(Box.createVerticalStrut(18));
        panel.add(createSectionLabel("All Session Attendance"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createScrollPane(attendanceTable, 340));
        panel.add(Box.createVerticalStrut(22));
        panel.add(createSectionLabel("Medical Records"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createScrollPane(medicalTable, 260));
        panel.add(Box.createVerticalStrut(10));
        panel.add(medicalDetailsPanel);
        panel.add(Box.createVerticalStrut(22));
        panel.add(createSectionLabel("Add Medical"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createMedicalSubmissionPanel());

        return panel;
    }

    /**
     * create section title label
     * @param text section title text
     * @return section label
     * @author shanuka
     */
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontBold(18));
        label.setForeground(AppTheme.TEXT_DARK);
        return label;
    }

    /**
     * create styled table for student attendance and medical data
     * @param model table data model
     * @return styled table
     * @author shanuka
     */
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(AppTheme.fontPlain(13));
        table.setForeground(AppTheme.TEXT_DARK);
        table.setGridColor(AppTheme.BORDER_SOFT);
        table.setSelectionBackground(AppTheme.TABLE_SELECTION_BG);
        table.setSelectionForeground(AppTheme.TABLE_SELECTION_FG);
        table.getTableHeader().setBackground(AppTheme.TABLE_HEADER_BG);
        table.getTableHeader().setForeground(AppTheme.TABLE_HEADER_FG);
        table.getTableHeader().setFont(AppTheme.fontBold(13));
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        return table;
    }

    /**
     * create table scroll pane with preferred height
     * @param table table component
     * @param preferredHeight preferred scroll pane height
     * @return table scroll pane
     * @author shanuka
     */
    private JScrollPane createScrollPane(JTable table, int preferredHeight) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false));
        scrollPane.getViewport().setBackground(AppTheme.CARD_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(0, preferredHeight));
        return scrollPane;
    }

    /**
     * create course attendance summary container
     * @return course attendance summary panel
     * @author shanuka
     */
    private JPanel createCourseAttendanceSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 8));
        panel.setOpaque(false);
        return panel;
    }

    /**
     * create one course attendance percentage row
     * @param courseCode course code
     * @param courseName course name
     * @param percentage attendance percentage
     * @return course attendance item panel
     * @author shanuka
     */
    private JPanel createCourseAttendanceItem(String courseCode, String courseName, double percentage) {
        JPanel item = new JPanel(new BorderLayout(12, 0));
        item.setBackground(AppTheme.CARD_BG);
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false),
                new EmptyBorder(10, 12, 10, 12)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JLabel courseLabel = new JLabel(courseCode + " - " + courseName);
        courseLabel.setFont(AppTheme.fontBold(13));
        courseLabel.setForeground(AppTheme.TEXT_DARK);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) Math.round(percentage));
        progressBar.setString(String.format("%.2f%%", percentage));
        progressBar.setStringPainted(true);
        progressBar.setForeground(AppTheme.PRIMARY);
        progressBar.setBackground(AppTheme.SURFACE_MUTED);
        progressBar.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false));
        progressBar.setPreferredSize(new Dimension(220, 22));

        item.add(courseLabel, BorderLayout.WEST);
        item.add(progressBar, BorderLayout.CENTER);
        return item;
    }

    /**
     * create panel for selected medical session details
     * @return medical details panel
     * @author shanuka
     */
    private JPanel createMedicalDetailsPanel() {
        JTable detailsTable = createStyledTable(medicalDetailsTableModel);
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(AppTheme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false),
                new EmptyBorder(14, 14, 14, 14)
        ));
        panel.add(lblMedicalDetailsMeta, BorderLayout.NORTH);
        panel.add(createScrollPane(detailsTable, 140), BorderLayout.CENTER);
        panel.setVisible(false);
        return panel;
    }

    /**
     * create student medical submission form
     * @return medical submission component
     * @author shanuka
     */
    private JComponent createMedicalSubmissionPanel() {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        JPanel rangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        rangePanel.setOpaque(false);

        txtMedicalStartDate = new ThemedDatePicker();
        txtMedicalEndDate = new ThemedDatePicker();
        txtMedicalStartDate.setPreferredSize(new Dimension(140, 38));
        txtMedicalEndDate.setPreferredSize(new Dimension(140, 38));

        rangePanel.add(new JLabel("Start Date"));
        rangePanel.add(txtMedicalStartDate);
        rangePanel.add(new JLabel("End Date"));
        rangePanel.add(txtMedicalEndDate);

        CustomButton loadAbsentButton = new CustomButton(
                "Load Absent Sessions",
                AppTheme.BTN_EDIT_BG,
                AppTheme.BTN_EDIT_FG,
                AppTheme.BTN_EDIT_HOVER,
                new Dimension(190, 38)
        );
        loadAbsentButton.addActionListener(e -> loadAbsentSessionsForMedicalRange());
        rangePanel.add(loadAbsentButton);

        lblAbsentSessionMeta = new JLabel("Enter the medical date range first.");
        lblAbsentSessionMeta.setFont(AppTheme.fontPlain(13));
        lblAbsentSessionMeta.setForeground(AppTheme.TEXT_SUBTLE);

        absentSessionTableModel = new DefaultTableModel(
                new Object[]{"Select", "Course Code", "Course Name", "Type", "Session No", "Date", "Day", "Time", "Venue"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : Object.class;
            }
        };

        absentSessionTable = createStyledTable(absentSessionTableModel);
        absentSessionTable.getModel().addTableModelListener(e -> updateMedicalUploadVisibility());
        TableColumn selectColumn = absentSessionTable.getColumnModel().getColumn(0);
        selectColumn.setPreferredWidth(70);
        selectColumn.setMaxWidth(70);
        selectColumn.setMinWidth(70);

        JScrollPane absentSessionsScrollPane = createScrollPane(absentSessionTable, 180);

        medicalUploadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        medicalUploadPanel.setOpaque(false);
        medicalUploadPanel.setVisible(false);

        txtMedicalDocument = createCompactTextField();
        txtMedicalDocument.setPreferredSize(new Dimension(340, 38));
        txtMedicalDocument.setEditable(false);

        CustomButton browseButton = new CustomButton(
                "Browse Certificate",
                AppTheme.BTN_EDIT_BG,
                AppTheme.BTN_EDIT_FG,
                AppTheme.BTN_EDIT_HOVER,
                new Dimension(170, 38)
        );
        browseButton.addActionListener(e -> chooseMedicalDocument());

        CustomButton submitButton = new CustomButton(
                "Submit Medical",
                AppTheme.BTN_SAVE_BG,
                AppTheme.BTN_SAVE_FG,
                AppTheme.BTN_SAVE_HOVER,
                new Dimension(150, 38)
        );
        submitButton.addActionListener(e -> submitMedical());

        medicalUploadPanel.add(new JLabel("Certificate"));
        medicalUploadPanel.add(txtMedicalDocument);
        medicalUploadPanel.add(browseButton);
        medicalUploadPanel.add(submitButton);

        wrapper.add(rangePanel);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(lblAbsentSessionMeta);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(absentSessionsScrollPane);
        wrapper.add(Box.createVerticalStrut(12));
        wrapper.add(medicalUploadPanel);
        return wrapper;
    }

    private JTextField createCompactTextField() {
        JTextField field = new JTextField();
        field.setFont(AppTheme.fontPlain(13));
        field.setPreferredSize(new Dimension(120, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_MUTED, 1, false),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    /**
     * load student attendance records and medical submissions
     * @author shanuka
     */
    private void loadStudentAttendanceMedicalData() {
        SwingWorker<StudentAttendanceMedicalViewData, Void> worker = new SwingWorker<StudentAttendanceMedicalViewData, Void>() {
            @Override
            protected StudentAttendanceMedicalViewData doInBackground() {
                return attendanceService.getStudentAttendanceMedicalViewData(currentUser.getId());
            }

            @Override
            protected void done() {
                try {
                    StudentAttendanceMedicalViewData viewData = get();
                    renderAttendanceRows(viewData.getAttendanceRows());
                    renderCourseAttendanceSummary(viewData.getAttendanceRows());
                    renderMedicalRows(viewData.getMedicalRows());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            StudentAttendanceMedicalPanel.this,
                            "Failed to load attendance records.",
                            "Attendance Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    /**
     * load absent sessions for selected medical date range
     * @author shanuka
     */
    private void loadAbsentSessionsForMedicalRange() {
        try {
            List<AbsentSessionOption> options = addStudentMedicalController.loadAbsentSessions(
                    currentUser.getId(),
                    txtMedicalStartDate.getText().trim(),
                    txtMedicalEndDate.getText().trim()
            );

            absentSessionOptions = options;
            renderAbsentSessionOptions(options);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Medical Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * render student session attendance rows
     * @param rows attendance row list
     * @author shanuka
     */
    private void renderAttendanceRows(List<StudentSessionAttendanceRow> rows) {
        attendanceTableModel.setRowCount(0);

        if (rows == null || rows.isEmpty()) {
            lblAttendanceMeta.setText("No attendance sessions are available for your account.");
            return;
        }

        lblAttendanceMeta.setText("Showing " + rows.size() + " session attendance records.");
        for (StudentSessionAttendanceRow row : rows) {
            attendanceTableModel.addRow(new Object[]{
                    row.getCourseCode(),
                    row.getCourseName(),
                    row.getSessionType(),
                    row.getSessionNo(),
                    row.getSessionDate(),
                    row.getSessionDay(),
                    row.getTimeRange(),
                    row.getVenue(),
                    row.getSessionStatus(),
                    row.getAttendanceStatus()
            });
        }
    }

    /**
     * render course wise attendance summary
     * @param rows attendance row list
     * @author shanuka
     */
    private void renderCourseAttendanceSummary(List<StudentSessionAttendanceRow> rows) {
        courseAttendanceSummaryPanel.removeAll();

        if (rows == null || rows.isEmpty()) {
            JLabel empty = new JLabel("No course attendance percentages available.");
            empty.setFont(AppTheme.fontPlain(13));
            empty.setForeground(AppTheme.TEXT_SUBTLE);
            courseAttendanceSummaryPanel.add(empty);
            courseAttendanceSummaryPanel.revalidate();
            courseAttendanceSummaryPanel.repaint();
            return;
        }

        Map<String, CourseAttendanceAggregate> aggregates = new LinkedHashMap<>();
        for (StudentSessionAttendanceRow row : rows) {
            if (!"COMPLETED".equalsIgnoreCase(row.getSessionStatus())) {
                continue;
            }

            String key = row.getCourseCode() + "|" + row.getCourseName();
            CourseAttendanceAggregate aggregate = aggregates.computeIfAbsent(
                    key,
                    ignored -> new CourseAttendanceAggregate(row.getCourseCode(), row.getCourseName())
            );
            aggregate.totalSessions++;

            if ("PRESENT".equalsIgnoreCase(row.getAttendanceStatus()) || "MEDICAL".equalsIgnoreCase(row.getAttendanceStatus())) {
                aggregate.attendedSessions++;
            }
        }

        if (aggregates.isEmpty()) {
            JLabel empty = new JLabel("No completed course sessions available yet.");
            empty.setFont(AppTheme.fontPlain(13));
            empty.setForeground(AppTheme.TEXT_SUBTLE);
            courseAttendanceSummaryPanel.add(empty);
        } else {
            for (CourseAttendanceAggregate aggregate : aggregates.values()) {
                double percentage = aggregate.totalSessions == 0
                        ? 0
                        : (aggregate.attendedSessions * 100.0) / aggregate.totalSessions;
                courseAttendanceSummaryPanel.add(
                        createCourseAttendanceItem(aggregate.courseCode, aggregate.courseName, percentage)
                );
            }
        }

        courseAttendanceSummaryPanel.revalidate();
        courseAttendanceSummaryPanel.repaint();
    }

    /**
     * render submitted student medical records
     * @param rows medical row list
     * @author shanuka
     */
    private void renderMedicalRows(List<StudentMedicalRow> rows) {
        medicalRows = rows;
        medicalTableModel.setRowCount(0);
        clearMedicalDetails();

        if (rows == null || rows.isEmpty()) {
            medicalTableModel.addRow(new Object[]{"-", "No medical submissions found.", "-", "-", "-", "-"});
            return;
        }

        for (StudentMedicalRow row : rows) {
            medicalTableModel.addRow(new Object[]{
                    row.getMedicalId(),
                    row.getSessionCount(),
                    row.getSubmittedDate(),
                    row.getApprovalStatus(),
                    row.getApprovedAt(),
                    row.getMedicalDocument()
            });
        }
    }

    /**
     * show linked session details for selected medical record
     * @author shanuka
     */
    private void showSelectedMedicalDetails() {
        int selectedRow = medicalTable.getSelectedRow();
        if (selectedRow < 0 || medicalRows == null || medicalRows.isEmpty()) {
            clearMedicalDetails();
            return;
        }

        int modelRow = medicalTable.convertRowIndexToModel(selectedRow);
        Object medicalIdValue = medicalTableModel.getValueAt(modelRow, 0);
        int medicalId;
        try {
            medicalId = Integer.parseInt(String.valueOf(medicalIdValue));
        } catch (NumberFormatException e) {
            clearMedicalDetails();
            return;
        }

        StudentMedicalRow selected = medicalRows.stream()
                .filter(row -> row.getMedicalId() == medicalId)
                .findFirst()
                .orElse(null);

        if (selected == null) {
            clearMedicalDetails();
            return;
        }

        lblMedicalDetailsMeta.setText(
                "Submitted: " + selected.getSubmittedDate() +
                        " | Approval: " + selected.getApprovalStatus() +
                        (selected.getApprovedAt() == null || selected.getApprovedAt().isEmpty() ? "" : " | Approved: " + selected.getApprovedAt())
        );

        medicalDetailsTableModel.setRowCount(0);
        for (MedicalSessionDetail detail : selected.getSessionDetails()) {
            medicalDetailsTableModel.addRow(new Object[]{
                    detail.getCourseCode(),
                    detail.getCourseName(),
                    detail.getSessionType(),
                    detail.getSessionNo(),
                    detail.getSessionDate()
            });
        }

        medicalDetailsPanel.setVisible(true);
        medicalDetailsPanel.revalidate();
        medicalDetailsPanel.repaint();
    }

    private void clearMedicalDetails() {
        lblMedicalDetailsMeta.setText("Select a medical row to view its linked sessions.");
        medicalDetailsTableModel.setRowCount(0);
        medicalDetailsPanel.setVisible(false);
    }

    private static class CourseAttendanceAggregate {
        private final String courseCode;
        private final String courseName;
        private int totalSessions;
        private int attendedSessions;

        /**
         * initialize course attendance aggregate
         * @param courseCode course code
         * @param courseName course name
         * @author shanuka
         */
        private CourseAttendanceAggregate(String courseCode, String courseName) {
            this.courseCode = courseCode;
            this.courseName = courseName;
        }
    }

    /**
     * render absent sessions available for medical submission
     * @param options absent session option list
     * @author shanuka
     */
    private void renderAbsentSessionOptions(List<AbsentSessionOption> options) {
        absentSessionTableModel.setRowCount(0);
        txtMedicalDocument.setText("");
        medicalUploadPanel.setVisible(false);

        if (options == null || options.isEmpty()) {
            lblAbsentSessionMeta.setText("No absent sessions without medical records were found in that period.");
            return;
        }

        lblAbsentSessionMeta.setText("Select absent sessions for this medical submission.");
        for (AbsentSessionOption option : options) {
            absentSessionTableModel.addRow(new Object[]{
                    false,
                    option.getCourseCode(),
                    option.getCourseName(),
                    option.getSessionType(),
                    option.getSessionNo(),
                    option.getSessionDate(),
                    option.getSessionDay(),
                    option.getTimeRange(),
                    option.getVenue()
            });
        }
    }

    private void updateMedicalUploadVisibility() {
        medicalUploadPanel.setVisible(!getSelectedAbsentSessionIds().isEmpty());
        medicalUploadPanel.revalidate();
        medicalUploadPanel.repaint();
    }

    /**
     * collect selected absent session ids from medical submission table
     * @return selected absent session id list
     * @author shanuka
     */
    private List<Integer> getSelectedAbsentSessionIds() {
        List<Integer> selectedSessionIds = new ArrayList<>();
        if (absentSessionOptions == null || absentSessionOptions.isEmpty()) {
            return selectedSessionIds;
        }

        for (int row = 0; row < absentSessionTableModel.getRowCount() && row < absentSessionOptions.size(); row++) {
            Object selectedValue = absentSessionTableModel.getValueAt(row, 0);
            if (Boolean.TRUE.equals(selectedValue)) {
                selectedSessionIds.add(absentSessionOptions.get(row).getSessionId());
            }
        }
        return selectedSessionIds;
    }

    /**
     * choose medical certificate document from local computer
     * @author shanuka
     */
    private void chooseMedicalDocument() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Medical Certificate");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Supported Files", "pdf", "jpg", "jpeg", "png"
        ));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            txtMedicalDocument.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * submit student medical request for selected absent sessions
     * @author shanuka
     */
    private void submitMedical() {
        try {
            addStudentMedicalController.submitMedical(new AddStudentMedicalRequest(
                    currentUser.getId(),
                    txtMedicalStartDate.getText().trim(),
                    txtMedicalEndDate.getText().trim(),
                    getSelectedAbsentSessionIds(),
                    txtMedicalDocument.getText().trim()
            ));

            JOptionPane.showMessageDialog(this, "Medical submitted successfully.");
            txtMedicalDocument.setText("");
            absentSessionOptions = new ArrayList<>();
            absentSessionTableModel.setRowCount(0);
            lblAbsentSessionMeta.setText("Medical submitted. Load another date range if needed.");
            medicalUploadPanel.setVisible(false);
            loadStudentAttendanceMedicalData();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Medical Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * apply search filter to attendance table
     * @author shanuka
     */
    private void applyAttendanceFilter() {
        String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            attendanceSorter.setRowFilter(null);
        } else {
            attendanceSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(keyword)));
        }
    }

    /**
     * open submitted medical document file
     * @param documentPath medical document path
     * @author shanuka
     */
    private void openMedicalDocument(String documentPath) {
        try {
            fileOpenService.openFile(documentPath);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Open Document", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class DocumentActionCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return createDocumentActionComponent(isSelected, value == null ? "" : value.toString());
        }
    }

    private class DocumentActionCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private String currentPath = "";

        private DocumentActionCellEditor() {
            panel = createDocumentActionComponent(true, "");
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    fireEditingStopped();
                    if (currentPath != null && !currentPath.trim().isEmpty()) {
                        openMedicalDocument(currentPath);
                    }
                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            return currentPath;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentPath = value == null ? "" : value.toString();
            panel.setBackground(isSelected ? AppTheme.TABLE_SELECTION_BG : Color.WHITE);
            return panel;
        }
    }

    /**
     * create medical document open action component
     * @param isSelected table row selected state
     * @param documentPath medical document path
     * @return document action panel
     * @author shanuka
     */
    private JPanel createDocumentActionComponent(boolean isSelected, String documentPath) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setOpaque(true);
        panel.setBackground(isSelected ? AppTheme.TABLE_SELECTION_BG : Color.WHITE);

        MaterialActionButton openButton = new MaterialActionButton(
                FontAwesomeSolid.EXTERNAL_LINK_ALT,
                AppTheme.ACTION_ICON_FG,
                AppTheme.ACTION_ICON_BG,
                AppTheme.ACTION_ICON_HOVER,
                "Open Medical Document"
        );
        openButton.setEnabled(documentPath != null && !documentPath.trim().isEmpty());
        openButton.addActionListener(e -> {
            if (documentPath != null && !documentPath.trim().isEmpty()) {
                openMedicalDocument(documentPath);
            }
        });
        panel.add(openButton);
        return panel;
    }
}
