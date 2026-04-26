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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects the inputs required to create an attendance session from a timetable entry.
 * @author methum
 */
public class AttendanceSessionDialog extends JDialog {
    private static final String WARNING_TITLE = "Attendance";

    private final JComboBox<CourseOption> cmbCourse;
    private final JComboBox<TimetableOption> cmbTimetableSession;
    private final ThemedDatePicker txtSessionDate;
    private final List<Course> allCourses;
    private final List<TimetableSession> allTimetableSessions;
    private AddAttendanceSessionRequest request;
    private boolean updatingSelectionState;

    /**
     * Initializes the add-attendance-session dialog.
     * @param owner parent window
     * @param courses available courses
     * @param timetableSessions available timetable sessions
     * @author methum
     */
    public AttendanceSessionDialog(Window owner, List<Course> courses, List<TimetableSession> timetableSessions) {
        super(owner, "Add Attendance Session", ModalityType.APPLICATION_MODAL);
        this.allCourses = courses == null ? List.of() : new ArrayList<>(courses);
        this.allTimetableSessions = timetableSessions == null ? List.of() : new ArrayList<>(timetableSessions);

        setLayout(new BorderLayout(0, 16));
        getContentPane().setBackground(AppTheme.CARD_BG);
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 12));
        form.setOpaque(false);

        cmbCourse = new JComboBox<>();
        cmbTimetableSession = new JComboBox<>();
        txtSessionDate = new ThemedDatePicker();
        txtSessionDate.setText(LocalDate.now().toString());

        form.add(createField("Course", cmbCourse));
        form.add(createField("Timetable Session", cmbTimetableSession));
        form.add(createField("Session Date", txtSessionDate));

        add(form, BorderLayout.CENTER);
        add(createActions(), BorderLayout.SOUTH);

        cmbCourse.addActionListener(e -> {
            if (!updatingSelectionState) {
                populateTimetableSessions(getSelectedDay(), getSelectedTimetableSessionId());
            }
        });
        txtSessionDate.addDateChangeListener(this::refreshSelectableOptions);
        refreshSelectableOptions();

        pack();
        setSize(460, getHeight());
        setLocationRelativeTo(owner);
    }

    /**
     * Returns the submitted request payload.
     * @author methum
     */
    public AddAttendanceSessionRequest getRequest() {
        return request;
    }

    /**
     * Creates a labeled form row.
     * @param labelText label text
     * @param field input field
     * @author methum
     */
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

    /**
     * Builds the footer action buttons.
     * @author methum
     */
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

    /**
     * Refreshes selectable courses and timetable sessions for the chosen date.
     * @author methum
     */
    private void refreshSelectableOptions() {
        Integer selectedCourseId = getSelectedCourseId();
        Integer selectedSessionId = getSelectedTimetableSessionId();
        String selectedDay = getSelectedDay();
        List<Course> filteredCourses = getCoursesForDay(selectedDay);

        updatingSelectionState = true;
        cmbCourse.removeAllItems();

        CourseOption preferredCourse = null;
        for (Course course : filteredCourses) {
            CourseOption option = new CourseOption(course);
            cmbCourse.addItem(option);
            if (selectedCourseId != null && course.getId() == selectedCourseId) {
                preferredCourse = option;
            }
        }

        if (preferredCourse != null) {
            cmbCourse.setSelectedItem(preferredCourse);
        } else if (cmbCourse.getItemCount() > 0) {
            cmbCourse.setSelectedIndex(0);
        }
        cmbCourse.setEnabled(cmbCourse.getItemCount() > 0);
        updatingSelectionState = false;

        populateTimetableSessions(selectedDay, selectedSessionId);
    }

    /**
     * Populates the timetable combo for the selected course and date.
     * @param selectedDay selected session day
     * @param preferredSessionId previously selected timetable session id
     * @author methum
     */
    private void populateTimetableSessions(String selectedDay, Integer preferredSessionId) {
        cmbTimetableSession.removeAllItems();
        CourseOption selectedCourse = (CourseOption) cmbCourse.getSelectedItem();
        if (selectedCourse == null) {
            cmbTimetableSession.setEnabled(false);
            return;
        }

        TimetableOption preferredSession = null;
        for (TimetableSession session : allTimetableSessions) {
            boolean matchesCourse = session.getCourseId() == selectedCourse.course.getId();
            boolean matchesDay = selectedDay == null || selectedDay.equalsIgnoreCase(valueOrEmpty(session.getDay()));
            if (matchesCourse && matchesDay) {
                TimetableOption option = new TimetableOption(session);
                cmbTimetableSession.addItem(option);
                if (preferredSessionId != null && session.getId() == preferredSessionId) {
                    preferredSession = option;
                }
            }
        }

        if (preferredSession != null) {
            cmbTimetableSession.setSelectedItem(preferredSession);
        } else if (cmbTimetableSession.getItemCount() > 0) {
            cmbTimetableSession.setSelectedIndex(0);
        }
        cmbTimetableSession.setEnabled(cmbTimetableSession.getItemCount() > 0);
    }

    /**
     * Validates the form and constructs the add-session request.
     * @author methum
     */
    private void submit() {
        CourseOption selectedCourse = (CourseOption) cmbCourse.getSelectedItem();
        TimetableOption selectedTimetable = (TimetableOption) cmbTimetableSession.getSelectedItem();

        if (selectedCourse == null) {
            showValidationWarning("Course is required.");
            return;
        }
        if (selectedTimetable == null) {
            showValidationWarning("Timetable session is required.");
            return;
        }
        String sessionDate = txtSessionDate.getText() == null ? "" : txtSessionDate.getText().trim();
        if (sessionDate.isEmpty()) {
            showValidationWarning("Session date is required.");
            return;
        }

        request = new AddAttendanceSessionRequest(
                String.valueOf(selectedCourse.course.getId()),
                String.valueOf(selectedTimetable.session.getId()),
                sessionDate
        );
        dispose();
    }

    /**
     * Loads courses that have timetable sessions on the selected day.
     * @param selectedDay selected session day
     * @author methum
     */
    private List<Course> getCoursesForDay(String selectedDay) {
        if (selectedDay == null) {
            return new ArrayList<>(allCourses);
        }

        List<Course> courses = new ArrayList<>();
        for (Course course : allCourses) {
            boolean hasMatchingSession = false;
            for (TimetableSession session : allTimetableSessions) {
                if (session.getCourseId() == course.getId() && selectedDay.equalsIgnoreCase(valueOrEmpty(session.getDay()))) {
                    hasMatchingSession = true;
                    break;
                }
            }
            if (hasMatchingSession) {
                courses.add(course);
            }
        }
        return courses;
    }

    /**
     * Resolves the selected course id safely.
     * @author methum
     */
    private Integer getSelectedCourseId() {
        CourseOption selectedCourse = (CourseOption) cmbCourse.getSelectedItem();
        return selectedCourse == null ? null : selectedCourse.course.getId();
    }

    /**
     * Resolves the selected timetable session id safely.
     * @author methum
     */
    private Integer getSelectedTimetableSessionId() {
        TimetableOption selectedTimetable = (TimetableOption) cmbTimetableSession.getSelectedItem();
        return selectedTimetable == null ? null : selectedTimetable.session.getId();
    }

    /**
     * Resolves the weekday name from the chosen session date.
     * @author methum
     */
    private String getSelectedDay() {
        String value = txtSessionDate.getText();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim()).getDayOfWeek().name();
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    /**
     * Normalizes nullable text values.
     * @param value text value
     * @author methum
     */
    private String valueOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Shows a validation warning message for invalid dialog input.
     * @param message warning message
     * @author methum
     */
    private void showValidationWarning(String message) {
        JOptionPane.showMessageDialog(this, message, WARNING_TITLE, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Option wrapper used by the course combo box.
     * @author methum
     */
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

    /**
     * Option wrapper used by the timetable session combo box.
     * @author methum
     */
    private static class TimetableOption {
        private final TimetableSession session;

        private TimetableOption(TimetableSession session) {
            this.session = session;
        }

        @Override
        public String toString() {
            String start = formatTime(session.getStartTime());
            String end = formatTime(session.getEndTime());
            return session.getDay() + " | " + start + " - " + end + " | " + session.getVenue();
        }

        /**
         * Formats HH:mm text safely from timetable time values.
         * @param value raw time text
         * @author methum
         */
        private String formatTime(String value) {
            if (value == null || value.trim().isEmpty()) {
                return "-";
            }
            String trimmed = value.trim();
            return trimmed.length() >= 5 ? trimmed.substring(0, 5) : trimmed;
        }
    }
}
