package com.fot.system.view.dashboard.to;

import com.fot.system.config.AppTheme;
import com.fot.system.controller.AttendanceSessionController;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.AttendanceService;
import com.fot.system.service.CourseService;
import com.fot.system.service.TimetableService;
import com.fot.system.view.components.CustomButton;
import com.fot.system.view.dashboard.lecturer.attendance.AttendanceSessionDialog;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TOAttendancePanel extends JPanel {
    private static final String[] ATTENDANCE_STATUSES = {"PRESENT", "ABSENT"};

    private final User currentUser;
    private final AttendanceService attendanceService;
    private final CourseService courseService;
    private final TimetableService timetableService;
    private final AttendanceSessionController attendanceSessionController;
    private DefaultTableModel sessionTableModel;
    private JTable sessionTable;
    private DefaultTableModel studentTableModel;
    private JTable studentTable;
    private JLabel lblSelectedSession;
    private JTextField txtSessionSearch;

    private List<Course> allCourses = new ArrayList<>();
    private List<TimetableSession> allTimetableSessions = new ArrayList<>();
    private AttendanceSessionRow selectedSession;

    public TOAttendancePanel(User user) {
        this.currentUser = user;
        this.attendanceService = new AttendanceService();
        this.courseService = new CourseService();
        this.timetableService = new TimetableService();
        this.attendanceSessionController = new AttendanceSessionController();

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        loadLookupData();
        loadSessions();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBlock = new JPanel(new BorderLayout(0, 8));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("Attendance Management");
        title.setFont(AppTheme.fontBold(28));
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Manage all sessions, create new session records, and update student attendance statuses.");
        subtitle.setFont(AppTheme.fontPlain(14));
        subtitle.setForeground(AppTheme.TEXT_SUBTLE);

        titleBlock.add(title, BorderLayout.NORTH);
        titleBlock.add(subtitle, BorderLayout.SOUTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        CustomButton refreshBtn = createActionButton("Refresh", FontAwesomeSolid.SYNC_ALT, AppTheme.BTN_EDIT_BG, AppTheme.BTN_EDIT_FG, AppTheme.BTN_EDIT_HOVER);
        refreshBtn.addActionListener(e -> loadSessions());

        CustomButton addBtn = createActionButton("Add New Session", FontAwesomeSolid.PLUS, AppTheme.BTN_SAVE_BG, AppTheme.BTN_SAVE_FG, AppTheme.BTN_SAVE_HOVER);
        addBtn.addActionListener(e -> openAddSessionDialog());

        CustomButton saveBtn = createActionButton("Save Attendance", FontAwesomeSolid.SAVE, AppTheme.BTN_SAVE_BG, AppTheme.BTN_SAVE_FG, AppTheme.BTN_SAVE_HOVER);
        saveBtn.addActionListener(e -> saveAttendance());

        actions.add(refreshBtn);
        actions.add(addBtn);
        actions.add(saveBtn);

        header.add(titleBlock, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JComponent createContent() {
        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);

        JPanel topSection = new JPanel(new BorderLayout(0, 10));
        topSection.setOpaque(false);

        txtSessionSearch = new JTextField();
        txtSessionSearch.setFont(AppTheme.fontPlain(13));
        txtSessionSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_MUTED, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        txtSessionSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applySessionFilter();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applySessionFilter();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applySessionFilter();
            }
        });

        topSection.add(createSectionLabel("All Sessions"), BorderLayout.NORTH);
        topSection.add(txtSessionSearch, BorderLayout.SOUTH);

        sessionTableModel = new DefaultTableModel(
                new Object[]{"Session ID", "Course Code", "Course Name", "Type", "Session No", "Date", "Day", "Time", "Venue", "Status"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        sessionTable = createStyledTable(sessionTableModel, false);
        sessionTable.setRowSorter(new TableRowSorter<>(sessionTableModel));
        sessionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sessionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                openSelectedSession();
            }
        });

        JScrollPane sessionsScrollPane = createScrollPane(sessionTable, 260);

        JPanel bottomSection = new JPanel(new BorderLayout(0, 10));
        bottomSection.setOpaque(false);

        lblSelectedSession = createMetaLabel("Select a session row to edit attendance.");

        studentTableModel = new DefaultTableModel(
                new Object[]{"Reg No", "Student", "Attendance Status", "Medical"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        studentTable = createStyledTable(studentTableModel, true);
        TableColumn statusColumn = studentTable.getColumnModel().getColumn(2);
        statusColumn.setCellEditor(new DefaultCellEditor(new JComboBox<>(ATTENDANCE_STATUSES)));
        JScrollPane studentsScrollPane = createScrollPane(studentTable, 320);

        bottomSection.add(createSectionLabel("Student Attendance"), BorderLayout.NORTH);
        bottomSection.add(lblSelectedSession, BorderLayout.CENTER);
        bottomSection.add(studentsScrollPane, BorderLayout.SOUTH);

        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.add(topSection);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(sessionsScrollPane);
        wrapper.add(Box.createVerticalStrut(22));
        wrapper.add(bottomSection);

        JScrollPane mainScrollPane = new JScrollPane(wrapper);
        mainScrollPane.setBorder(null);
        mainScrollPane.getViewport().setBackground(AppTheme.SURFACE_SOFT);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        content.add(mainScrollPane, BorderLayout.CENTER);
        return content;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontBold(18));
        label.setForeground(AppTheme.TEXT_DARK);
        return label;
    }

    private JLabel createMetaLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontPlain(14));
        label.setForeground(AppTheme.TEXT_SUBTLE);
        return label;
    }

    private CustomButton createActionButton(String text, FontAwesomeSolid icon, Color bg, Color fg, Color hover) {
        CustomButton button = new CustomButton(text, bg, fg, hover, new Dimension(150, 40));
        button.setIcon(FontIcon.of(icon, 14, fg));
        return button;
    }

    private JTable createStyledTable(DefaultTableModel model, boolean editable) {
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
        if (!editable) {
            table.setDefaultEditor(Object.class, null);
        }
        return table;
    }

    private JScrollPane createScrollPane(JTable table, int preferredHeight) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true));
        scrollPane.getViewport().setBackground(AppTheme.CARD_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(0, preferredHeight));
        return scrollPane;
    }

    private void loadLookupData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                allCourses = courseService.getAllCourses();
                allTimetableSessions = timetableService.getAllTimetableSessions();
                return null;
            }
        };
        worker.execute();
    }

    private void loadSessions() {
        SwingWorker<List<AttendanceSessionRow>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<AttendanceSessionRow> doInBackground() {
                return attendanceService.getAllAttendanceSessions();
            }

            @Override
            protected void done() {
                try {
                    renderSessions(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TOAttendancePanel.this,
                            "Failed to load sessions.",
                            "Attendance Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void renderSessions(List<AttendanceSessionRow> rows) {
        sessionTableModel.setRowCount(0);
        for (AttendanceSessionRow row : rows) {
            sessionTableModel.addRow(new Object[]{
                    row.getSessionId(),
                    row.getCourseCode(),
                    row.getCourseName(),
                    row.getSessionType(),
                    row.getSessionNo(),
                    row.getSessionDate(),
                    row.getSessionDay(),
                    row.getTimeRange(),
                    row.getVenue(),
                    row.getSessionStatus()
            });
        }
        studentTableModel.setRowCount(0);
        selectedSession = null;
        lblSelectedSession.setText("Select a session row to edit attendance.");
        applySessionFilter();
    }

    private void applySessionFilter() {
        TableRowSorter<?> sorter = (TableRowSorter<?>) sessionTable.getRowSorter();
        String keyword = txtSessionSearch.getText() == null ? "" : txtSessionSearch.getText().trim();
        if (keyword.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(keyword)));
        }
    }

    private void openSelectedSession() {
        int selectedRow = sessionTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        int modelRow = sessionTable.convertRowIndexToModel(selectedRow);
        int sessionId = Integer.parseInt(sessionTableModel.getValueAt(modelRow, 0).toString());

        SwingWorker<AttendanceSessionEditorData, Void> worker = new SwingWorker<>() {
            @Override
            protected AttendanceSessionEditorData doInBackground() {
                return attendanceService.getSessionEditorData(sessionId);
            }

            @Override
            protected void done() {
                try {
                    renderSessionEditor(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TOAttendancePanel.this,
                            "Failed to load session attendance.",
                            "Attendance Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void renderSessionEditor(AttendanceSessionEditorData data) {
        selectedSession = data.getSession();
        studentTableModel.setRowCount(0);

        if (selectedSession == null) {
            lblSelectedSession.setText("Select a session row to edit attendance.");
            return;
        }

        lblSelectedSession.setText(
                selectedSession.getCourseCode() + " | Session " + selectedSession.getSessionNo() + " | " +
                        selectedSession.getSessionDate() + " | " + selectedSession.getTimeRange()
        );

        data.getStudentRows().forEach(row -> studentTableModel.addRow(new Object[]{
                row.getRegistrationNo(),
                row.getStudentName(),
                row.getAttendanceStatus().isEmpty() ? "ABSENT" : row.getAttendanceStatus(),
                row.getMedicalApprovalStatus().isEmpty() ? "-" : row.getMedicalApprovalStatus()
        }));
    }

    private void openAddSessionDialog() {
        if (allCourses == null || allCourses.isEmpty() || allTimetableSessions == null || allTimetableSessions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course and timetable data are still loading.", "Attendance", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AttendanceSessionDialog dialog = new AttendanceSessionDialog(
                SwingUtilities.getWindowAncestor(this),
                allCourses,
                allTimetableSessions
        );
        dialog.setVisible(true);

        AddAttendanceSessionRequest request = dialog.getRequest();
        if (request == null) {
            return;
        }

        try {
            attendanceSessionController.createSessionForTo(request);
            JOptionPane.showMessageDialog(this, "New session created successfully.");
            loadSessions();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Attendance Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveAttendance() {
        if (selectedSession == null) {
            JOptionPane.showMessageDialog(this, "Select a session first.", "Attendance", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<StudentAttendanceUpdate> updates = new ArrayList<>();
        for (int row = 0; row < studentTableModel.getRowCount(); row++) {
            updates.add(new StudentAttendanceUpdate(
                    String.valueOf(studentTableModel.getValueAt(row, 0)),
                    String.valueOf(studentTableModel.getValueAt(row, 2))
            ));
        }

        try {
            attendanceSessionController.saveAttendance(selectedSession.getSessionId(), currentUser.getId(), updates);
            JOptionPane.showMessageDialog(this, "Attendance saved successfully.");
            openSelectedSession();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Attendance Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
