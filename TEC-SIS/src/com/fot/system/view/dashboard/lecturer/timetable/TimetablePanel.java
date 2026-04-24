package com.fot.system.view.dashboard.lecturer.timetable;

import com.fot.system.config.AppTheme;
import com.fot.system.controller.TimetableSessionController;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.CourseService;
import com.fot.system.service.TimetableService;
import com.fot.system.view.components.CustomButton;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TimetablePanel extends JPanel {
    private final TimetableService timetableService;
    private final CourseService courseService;
    private final TimetableSessionController timetableSessionController;
    private final TimetableTablePanel tablePanel;

    private List<Course> courses;
    private List<Staff> lecturers;

    public TimetablePanel(User user) {
        this.timetableService = new TimetableService();
        this.courseService = new CourseService();
        this.timetableSessionController = new TimetableSessionController();

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(createHeader(), BorderLayout.NORTH);

        tablePanel = new TimetableTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        loadLookupData();
        loadTimetableData();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBlock = new JPanel(new BorderLayout(0, 8));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("Timetable Management");
        title.setFont(AppTheme.fontBold(26));
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel("Manage timetable sessions in a clean editable table with add, edit, and delete actions.");
        subtitle.setFont(AppTheme.fontPlain(14));
        subtitle.setForeground(AppTheme.TEXT_SUBTLE);

        titleBlock.add(title, BorderLayout.NORTH);
        titleBlock.add(subtitle, BorderLayout.SOUTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        CustomButton refreshBtn = createActionButton("Refresh", FontAwesomeSolid.SYNC_ALT, AppTheme.BTN_EDIT_BG, AppTheme.BTN_EDIT_FG, AppTheme.BTN_EDIT_HOVER);
        refreshBtn.addActionListener(e -> loadTimetableData());

        CustomButton addBtn = createActionButton("Add Session", FontAwesomeSolid.PLUS, AppTheme.BTN_SAVE_BG, AppTheme.BTN_SAVE_FG, AppTheme.BTN_SAVE_HOVER);
        addBtn.addActionListener(e -> openCreateDialog());

        CustomButton editBtn = createActionButton("Edit Session", FontAwesomeSolid.EDIT, AppTheme.BTN_EDIT_BG, AppTheme.BTN_EDIT_FG, AppTheme.BTN_EDIT_HOVER);
        editBtn.addActionListener(e -> openEditDialog());

        CustomButton deleteBtn = createActionButton("Delete Session", FontAwesomeSolid.TRASH, AppTheme.BTN_DELETE_BG, AppTheme.BTN_DELETE_FG, AppTheme.BTN_DELETE_HOVER);
        deleteBtn.addActionListener(e -> deleteSelectedSession());

        actions.add(refreshBtn);
        actions.add(addBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);

        header.add(titleBlock, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private CustomButton createActionButton(String text, FontAwesomeSolid icon, Color bg, Color fg, Color hover) {
        CustomButton button = new CustomButton(text, bg, fg, hover, new Dimension(145, 40));
        button.setIcon(FontIcon.of(icon, 14, fg));
        return button;
    }

    private void loadLookupData() {
        SwingWorker<LookupData, Void> worker = new SwingWorker<>() {
            @Override
            protected LookupData doInBackground() {
                return new LookupData(courseService.getAllCourses(), courseService.getAllLecturers());
            }

            @Override
            protected void done() {
                try {
                    LookupData data = get();
                    courses = data.courses;
                    lecturers = data.lecturers;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TimetablePanel.this,
                            "Failed to load timetable lookup data.",
                            "Timetable Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadTimetableData() {
        SwingWorker<List<TimetableSession>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<TimetableSession> doInBackground() {
                return timetableService.getAllTimetableSessions();
            }

            @Override
            protected void done() {
                try {
                    List<TimetableSession> sessions = get();
                    tablePanel.getModel().setRowCount(0);

                    for (TimetableSession session : sessions) {
                        tablePanel.addRow(new Object[]{
                                session.getId(),
                                valueOrDash(session.getCourseCode()),
                                valueOrDash(session.getCourseName()),
                                valueOrDash(session.getLecturerName()),
                                valueOrDash(session.getDay()),
                                formatTime(session.getStartTime()),
                                formatTime(session.getEndTime()),
                                valueOrDash(session.getVenue()),
                                valueOrDash(session.getSessionType())
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TimetablePanel.this,
                            "Failed to load timetable sessions.",
                            "Timetable Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void openCreateDialog() {
        if (!ensureLookupsLoaded()) {
            return;
        }

        TimetableSessionDialog dialog = new TimetableSessionDialog(
                SwingUtilities.getWindowAncestor(this),
                "Add Timetable Session",
                courses,
                lecturers,
                null
        );
        dialog.setVisible(true);

        TimetableSessionRequest request = dialog.getRequest();
        if (request == null) {
            return;
        }

        try {
            timetableSessionController.createSession(request);
            JOptionPane.showMessageDialog(this, "Timetable session added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadTimetableData();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Timetable Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEditDialog() {
        if (!ensureLookupsLoaded()) {
            return;
        }

        TimetableSession session = getSelectedSession();
        if (session == null) {
            return;
        }

        TimetableSessionDialog dialog = new TimetableSessionDialog(
                SwingUtilities.getWindowAncestor(this),
                "Edit Timetable Session",
                courses,
                lecturers,
                session
        );
        dialog.setVisible(true);

        TimetableSessionRequest request = dialog.getRequest();
        if (request == null) {
            return;
        }

        try {
            timetableSessionController.updateSession(request);
            JOptionPane.showMessageDialog(this, "Timetable session updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadTimetableData();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Timetable Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedSession() {
        TimetableSession session = getSelectedSession();
        if (session == null) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete timetable session for " + valueOrDash(session.getCourseCode()) + " on " + valueOrDash(session.getDay()) + "?",
                "Delete Session",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            timetableSessionController.deleteSession(session.getId());
            JOptionPane.showMessageDialog(this, "Timetable session deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadTimetableData();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Timetable Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private TimetableSession getSelectedSession() {
        int row = tablePanel.getTable().getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a timetable session first.", "Timetable", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int modelRow = tablePanel.getTable().convertRowIndexToModel(row);
        int sessionId = Integer.parseInt(tablePanel.getModel().getValueAt(modelRow, 0).toString());
        return timetableService.getSessionById(sessionId);
    }

    private boolean ensureLookupsLoaded() {
        if (courses == null || lecturers == null) {
            JOptionPane.showMessageDialog(this, "Timetable lookup data is still loading. Please try again.", "Timetable", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private String formatTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        return value.length() >= 5 ? value.substring(0, 5) : value;
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private static class LookupData {
        private final List<Course> courses;
        private final List<Staff> lecturers;

        private LookupData(List<Course> courses, List<Staff> lecturers) {
            this.courses = courses;
            this.lecturers = lecturers;
        }
    }
}
