package com.fot.system.view.dashboard.lecturer.attendance;

import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.view.components.CustomButton;
import com.fot.system.view.components.ThemedDatePicker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class AttendanceSessionDialog extends JDialog {
    private final JComboBox<CourseOption> cmbCourse;
    private final JComboBox<TimetableOption> cmbTimetableSession;
    private final ThemedDatePicker txtSessionDate;
    private AddAttendanceSessionRequest request;

    public AttendanceSessionDialog(Window owner, List<Course> courses, List<TimetableSession> timetableSessions) {
        super(owner, "Add Attendance Session", ModalityType.APPLICATION_MODAL);

        setLayout(new BorderLayout(0, 16));
        getContentPane().setBackground(AppTheme.CARD_BG);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 12));
        form.setOpaque(false);

        cmbCourse = new JComboBox<>();
        for (Course course : courses) {
            cmbCourse.addItem(new CourseOption(course));
        }

        cmbTimetableSession = new JComboBox<>();
        txtSessionDate = new ThemedDatePicker();
        txtSessionDate.setText(LocalDate.now().toString());

        form.add(createField("Course", cmbCourse));
        form.add(createField("Timetable Session", cmbTimetableSession));
        form.add(createField("Session Date", txtSessionDate));

        add(form, BorderLayout.CENTER);
        add(createActions(), BorderLayout.SOUTH);

        cmbCourse.addActionListener(e -> populateTimetableSessions(timetableSessions));
        populateTimetableSessions(timetableSessions);

        pack();
        setSize(460, getHeight());
        setLocationRelativeTo(owner);
    }

    public AddAttendanceSessionRequest getRequest() {
        return request;
    }

    private JPanel createField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(AppTheme.fontPlain(13));
        label.setForeground(AppTheme.TEXT_DARK);

        field.setFont(AppTheme.fontPlain(14));
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);

        CustomButton cancelButton = new CustomButton(
                "Cancel",
                AppTheme.BTN_CANCEL_BG,
                AppTheme.BTN_CANCEL_FG,
                AppTheme.BTN_CANCEL_HOVER,
                new Dimension(110, 40)
        );
        cancelButton.addActionListener(e -> dispose());

        CustomButton saveButton = new CustomButton(
                "Create",
                AppTheme.BTN_SAVE_BG,
                AppTheme.BTN_SAVE_FG,
                AppTheme.BTN_SAVE_HOVER,
                new Dimension(110, 40)
        );
        saveButton.addActionListener(e -> submit());

        panel.add(cancelButton);
        panel.add(saveButton);
        return panel;
    }

    private void populateTimetableSessions(List<TimetableSession> timetableSessions) {
        cmbTimetableSession.removeAllItems();
        CourseOption selectedCourse = (CourseOption) cmbCourse.getSelectedItem();
        if (selectedCourse == null) {
            return;
        }

        for (TimetableSession session : timetableSessions) {
            if (session.getCourseId() == selectedCourse.course.getId()) {
                cmbTimetableSession.addItem(new TimetableOption(session));
            }
        }
    }

    private void submit() {
        CourseOption selectedCourse = (CourseOption) cmbCourse.getSelectedItem();
        TimetableOption selectedTimetable = (TimetableOption) cmbTimetableSession.getSelectedItem();

        if (selectedCourse == null) {
            throw new RuntimeException("Course is required.");
        }
        if (selectedTimetable == null) {
            JOptionPane.showMessageDialog(this, "Timetable session is required.", "Attendance", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sessionDate = txtSessionDate.getText() == null ? "" : txtSessionDate.getText().trim();
        if (sessionDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Session date is required.", "Attendance", JOptionPane.WARNING_MESSAGE);
            return;
        }

        request = new AddAttendanceSessionRequest(
                String.valueOf(selectedCourse.course.getId()),
                String.valueOf(selectedTimetable.session.getId()),
                sessionDate
        );
        dispose();
    }

    private static class CourseOption {
        private final Course course;

        private CourseOption(Course course) {
            this.course = course;
        }

        @Override
        public String toString() {
            return course.getCourseCode() + " - " + course.getCourseName();
        }
    }

    private static class TimetableOption {
        private final TimetableSession session;

        private TimetableOption(TimetableSession session) {
            this.session = session;
        }

        @Override
        public String toString() {
            String start = session.getStartTime() == null ? "-" : session.getStartTime().substring(0, 5);
            String end = session.getEndTime() == null ? "-" : session.getEndTime().substring(0, 5);
            return session.getDay() + " | " + start + " - " + end + " | " + session.getVenue();
        }
    }
}
