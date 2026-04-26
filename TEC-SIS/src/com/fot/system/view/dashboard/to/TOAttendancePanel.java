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
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides the technical officer attendance workspace for creating sessions and updating student attendance.
 * @author methum
 */
public class TOAttendancePanel extends JPanel {
    private static final String[] ATTENDANCE_STATUSES = {"PRESENT", "ABSENT"};
    private static final int SESSION_TABLE_HEIGHT = 260;
    private static final int STUDENT_TABLE_HEIGHT = 320;
    private static final int SESSION_ID_COLUMN = 0;
    private static final int REGISTRATION_COLUMN = 0;
    private static final int ATTENDANCE_STATUS_COLUMN = 2;
    private static final int MEDICAL_COLUMN = 3;
    private static final String DEFAULT_EDITOR_MESSAGE = "Select a session row to edit attendance.";
    private static final String ERROR_TITLE = "Attendance Error";
    private static final String INFO_TITLE = "Attendance";

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

    /**
     * Creates the TO attendance panel and loads the initial lookup and session data.
     * @param user logged-in technical officer
     * @author methum
     */
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

    /**
     * Builds the top header with title and primary action buttons.
     * @author methum
     */
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

        actions.add(refreshBtn);
        actions.add(addBtn);

        header.add(titleBlock, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    /**
     * Builds the session list and student attendance editor content area.
     * @author methum
     */
    private JComponent createContent() {
        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);

        JPanel topSection = new JPanel(new BorderLayout(0, 10));
        topSection.setOpaque(false);

        txtSessionSearch = new JTextField();
        txtSessionSearch.setFont(AppTheme.fontPlain(13));
        txtSessionSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_MUTED, 1, false),
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

        JScrollPane sessionsScrollPane = createScrollPane(sessionTable, SESSION_TABLE_HEIGHT);

        JPanel bottomSection = new JPanel(new BorderLayout(0, 10));
        bottomSection.setOpaque(false);

        lblSelectedSession = createMetaLabel(DEFAULT_EDITOR_MESSAGE);

        studentTableModel = new DefaultTableModel(
                new Object[]{"Reg No", "Student", "Attendance Status", "Medical"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == ATTENDANCE_STATUS_COLUMN && !isApprovedMedicalRow(row);
            }
        };

        studentTable = createStyledTable(studentTableModel, true);
        TableColumn statusColumn = studentTable.getColumnModel().getColumn(ATTENDANCE_STATUS_COLUMN);
        statusColumn.setCellEditor(new DefaultCellEditor(new JComboBox<>(ATTENDANCE_STATUSES)));
        JScrollPane studentsScrollPane = createScrollPane(studentTable, STUDENT_TABLE_HEIGHT);

        bottomSection.add(createStudentAttendanceHeader(), BorderLayout.NORTH);
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

    /**
     * Builds the student attendance section header with the save action.
     * @author methum
     */
    private JPanel createStudentAttendanceHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        CustomButton saveBtn = createActionButton("Save Attendance", FontAwesomeSolid.SAVE, AppTheme.BTN_SAVE_BG, AppTheme.BTN_SAVE_FG, AppTheme.BTN_SAVE_HOVER);
        saveBtn.addActionListener(e -> saveAttendance());

        header.add(createSectionLabel("Student Attendance"), BorderLayout.WEST);
        header.add(saveBtn, BorderLayout.EAST);
        return header;
    }

    /**
     * Creates a bold section label for the panel.
     * @param text label text
     * @author methum
     */
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontBold(18));
        label.setForeground(AppTheme.TEXT_DARK);
        return label;
    }

    /**
     * Creates a subtle metadata label used above the student editor table.
     * @param text label text
     * @author methum
     */
    private JLabel createMetaLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontPlain(14));
        label.setForeground(AppTheme.TEXT_SUBTLE);
        return label;
    }

    /**
     * Creates a styled action button with the provided icon and colors.
     * @param text button text
     * @param icon icon glyph
     * @param bg background color
     * @param fg foreground color
     * @param hover hover background color
     * @author methum
     */
    private CustomButton createActionButton(String text, FontAwesomeSolid icon, Color bg, Color fg, Color hover) {
        CustomButton button = new CustomButton(text, bg, fg, hover, new Dimension(150, 40));
        button.setIcon(FontIcon.of(icon, 14, fg));
        return button;
    }

    /**
     * Creates the shared table styling used by both attendance tables.
     * @param model table model
     * @param editable whether cells should be editable
     * @author methum
     */
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
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        if (!editable) {
            table.setDefaultEditor(Object.class, null);
        }
        return table;
    }

    /**
     * Wraps a table in a styled scroll pane with a fixed preferred height.
     * @param table table instance
     * @param preferredHeight preferred scroll height
     * @author methum
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
     * Loads course and timetable lookup data needed by the add-session dialog.
     * @author methum
     */
    private void loadLookupData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                List<Course> courses = courseService.getAllCourses();
                List<TimetableSession> timetableSessions = timetableService.getAllTimetableSessions();
                allCourses = courses == null ? new ArrayList<>() : new ArrayList<>(courses);
                allTimetableSessions = timetableSessions == null ? new ArrayList<>() : new ArrayList<>(timetableSessions);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    allCourses = new ArrayList<>();
                    allTimetableSessions = new ArrayList<>();
                    showErrorDialog("Failed to load course and timetable data.");
                }
            }
        };
        worker.execute();
    }

    /**
     * Loads all attendance sessions shown in the top session table.
     * @author methum
     */
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
                    showErrorDialog("Failed to load sessions.");
                }
            }
        };
        worker.execute();
    }

    /**
     * Renders the session list and resets the student editor state.
     * @param rows session rows
     * @author methum
     */
    private void renderSessions(List<AttendanceSessionRow> rows) {
        stopTableEditing(studentTable);
        sessionTableModel.setRowCount(0);
        List<AttendanceSessionRow> safeRows = rows == null ? List.of() : rows;
        for (AttendanceSessionRow row : safeRows) {
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
        resetSelectedSessionEditor();
        applySessionFilter();
    }

    /**
     * Applies the keyword filter to the session table.
     * @author methum
     */
    private void applySessionFilter() {
        TableRowSorter<?> sorter = (TableRowSorter<?>) sessionTable.getRowSorter();
        String keyword = txtSessionSearch.getText() == null ? "" : txtSessionSearch.getText().trim();
        if (keyword.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(keyword)));
        }
    }

    /**
     * Loads the selected session and its editable student attendance rows.
     * @author methum
     */
    private void openSelectedSession() {
        stopTableEditing(studentTable);

        int selectedRow = sessionTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        int modelRow = sessionTable.convertRowIndexToModel(selectedRow);
        int sessionId = Integer.parseInt(String.valueOf(sessionTableModel.getValueAt(modelRow, SESSION_ID_COLUMN)));

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
                    showErrorDialog("Failed to load session attendance.");
                }
            }
        };
        worker.execute();
    }

    /**
     * Renders the selected session details and editable student attendance rows.
     * @param data editor data
     * @author methum
     */
    private void renderSessionEditor(AttendanceSessionEditorData data) {
        stopTableEditing(studentTable);

        if (data == null) {
            resetSelectedSessionEditor();
            return;
        }

        selectedSession = data.getSession();
        studentTableModel.setRowCount(0);

        if (selectedSession == null) {
            lblSelectedSession.setText(DEFAULT_EDITOR_MESSAGE);
            return;
        }

        lblSelectedSession.setText(
                selectedSession.getCourseCode() + " | Session " + selectedSession.getSessionNo() + " | " +
                        selectedSession.getSessionDate() + " | " + selectedSession.getTimeRange()
        );

        List<StudentAttendanceEditRow> studentRows = data.getStudentRows() == null ? List.of() : data.getStudentRows();
        studentRows.forEach(row -> studentTableModel.addRow(new Object[]{
                row.getRegistrationNo(),
                row.getStudentName(),
                resolveDisplayedAttendanceStatus(row),
                row.getMedicalApprovalStatus().isEmpty() ? "-" : row.getMedicalApprovalStatus()
        }));
    }

    /**
     * Checks whether the given table row belongs to an approved medical record.
     * @param row table row index
     * @author methum
     */
    private boolean isApprovedMedicalRow(int row) {
        Object medicalValue = studentTableModel.getValueAt(row, MEDICAL_COLUMN);
        return medicalValue != null && "APPROVED".equalsIgnoreCase(medicalValue.toString().trim());
    }

    /**
     * Resolves the display status for a student attendance row.
     * @param row student attendance row
     * @author methum
     */
    private String resolveDisplayedAttendanceStatus(StudentAttendanceEditRow row) {
        if (row == null) {
            return "ABSENT";
        }
        if ("APPROVED".equalsIgnoreCase(row.getMedicalApprovalStatus())) {
            return "MEDICAL";
        }
        return row.getAttendanceStatus().isEmpty() ? "ABSENT" : row.getAttendanceStatus();
    }

    /**
     * Opens the add-session dialog for TOs.
     * @author methum
     */
    private void openAddSessionDialog() {
        if (allCourses == null || allCourses.isEmpty() || allTimetableSessions == null || allTimetableSessions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course and timetable data are still loading.", INFO_TITLE, JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "New session created successfully.", INFO_TITLE, JOptionPane.INFORMATION_MESSAGE);
            loadSessions();
        } catch (RuntimeException ex) {
            showErrorDialog(ex.getMessage());
        }
    }

    /**
     * Saves all editable attendance statuses for the selected session.
     * @author methum
     */
    private void saveAttendance() {
        if (selectedSession == null) {
            JOptionPane.showMessageDialog(this, "Select a session first.", INFO_TITLE, JOptionPane.WARNING_MESSAGE);
            return;
        }

        stopTableEditing(studentTable);
        List<StudentAttendanceUpdate> updates = buildAttendanceUpdates();

        try {
            attendanceSessionController.saveAttendance(selectedSession.getSessionId(), currentUser.getId(), updates);
            JOptionPane.showMessageDialog(this, "Attendance saved successfully.", INFO_TITLE, JOptionPane.INFORMATION_MESSAGE);
            openSelectedSession();
        } catch (RuntimeException ex) {
            showErrorDialog(ex.getMessage());
        }
    }

    /**
     * Builds the attendance update payload list from the student table.
     * @author methum
     */
    private List<StudentAttendanceUpdate> buildAttendanceUpdates() {
        List<StudentAttendanceUpdate> updates = new ArrayList<>();
        for (int row = 0; row < studentTableModel.getRowCount(); row++) {
            updates.add(new StudentAttendanceUpdate(
                    String.valueOf(studentTableModel.getValueAt(row, REGISTRATION_COLUMN)),
                    String.valueOf(studentTableModel.getValueAt(row, ATTENDANCE_STATUS_COLUMN))
            ));
        }
        return updates;
    }

    /**
     * Resets the selected session state and clears the student editor table.
     * @author methum
     */
    private void resetSelectedSessionEditor() {
        stopTableEditing(studentTable);
        studentTableModel.setRowCount(0);
        selectedSession = null;
        lblSelectedSession.setText(DEFAULT_EDITOR_MESSAGE);
    }

    /**
     * Stops active table editing before the model is read or reset.
     * @param table target table
     * @author methum
     */
    private void stopTableEditing(JTable table) {
        if (table == null || !table.isEditing()) {
            return;
        }

        TableCellEditor editor = table.getCellEditor();
        if (editor == null) {
            return;
        }

        if (!editor.stopCellEditing()) {
            editor.cancelCellEditing();
        }
    }

    /**
     * Shows a standard error dialog for attendance operations.
     * @param message error message
     * @author methum
     */
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
    }
}
