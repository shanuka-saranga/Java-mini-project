package com.fot.system.view.dashboard.lecturer.timetable;

import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.view.components.CustomButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TimetableSessionDialog extends JDialog {
    private static final String[] SESSION_TYPES = {"THEORY", "PRACTICAL"};
    private static final String[] DAYS = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};

    private final JComboBox<CourseOption> cmbCourse;
    private final JComboBox<LecturerOption> cmbLecturer;
    private final JComboBox<String> cmbType;
    private final JComboBox<String> cmbDay;
    private final JTextField txtStartTime;
    private final JTextField txtEndTime;
    private final JTextField txtVenue;
    private TimetableSessionRequest request;

    public TimetableSessionDialog(Window owner, String title, List<Course> courses, List<Staff> lecturers, TimetableSession session) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setSize(560, 470);
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(AppTheme.SURFACE_SOFT);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel heading = new JLabel(title);
        heading.setFont(AppTheme.fontBold(22));
        heading.setForeground(AppTheme.TEXT_DARK);

        JPanel form = new JPanel(new GridLayout(0, 2, 14, 14));
        form.setBackground(AppTheme.CARD_BG);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        cmbCourse = new JComboBox<>();
        for (Course course : courses) {
            cmbCourse.addItem(new CourseOption(course));
        }

        cmbLecturer = new JComboBox<>();
        cmbLecturer.addItem(new LecturerOption(null));
        for (Staff lecturer : lecturers) {
            cmbLecturer.addItem(new LecturerOption(lecturer));
        }

        cmbType = new JComboBox<>(SESSION_TYPES);
        cmbDay = new JComboBox<>(DAYS);
        txtStartTime = new JTextField();
        txtEndTime = new JTextField();
        txtVenue = new JTextField();

        styleComponent(cmbCourse);
        styleComponent(cmbLecturer);
        styleComponent(cmbType);
        styleComponent(cmbDay);
        styleComponent(txtStartTime);
        styleComponent(txtEndTime);
        styleComponent(txtVenue);

        cmbCourse.addActionListener(e -> applyCourseSessionTypeRule());

        form.add(createField("Course", cmbCourse));
        form.add(createField("Lecturer", cmbLecturer));
        form.add(createField("Session Type", cmbType));
        form.add(createField("Day", cmbDay));
        form.add(createField("Start Time", txtStartTime));
        form.add(createField("End Time", txtEndTime));
        form.add(createField("Venue", txtVenue));
        form.add(createHint("Use time as HH:mm"));

        if (session != null) {
            applySession(session);
        } else {
            applyCourseSessionTypeRule();
        }

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        CustomButton cancel = new CustomButton("Cancel", AppTheme.BTN_CANCEL_BG, AppTheme.BTN_CANCEL_FG, AppTheme.BTN_CANCEL_HOVER, new Dimension(110, 40));
        cancel.addActionListener(e -> dispose());

        CustomButton save = new CustomButton("Save", AppTheme.BTN_SAVE_BG, AppTheme.BTN_SAVE_FG, AppTheme.BTN_SAVE_HOVER, new Dimension(110, 40));
        save.addActionListener(e -> {
            request = buildRequest(session);
            dispose();
        });

        actions.add(cancel);
        actions.add(save);

        root.add(heading, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        setContentPane(root);
    }

    public TimetableSessionRequest getRequest() {
        return request;
    }

    private void applySession(TimetableSession session) {
        selectCourse(session.getCourseId());
        applyCourseSessionTypeRule();
        selectLecturer(session.getLecturerId());
        cmbType.setSelectedItem(session.getSessionType());
        cmbDay.setSelectedItem(session.getDay());
        txtStartTime.setText(formatTime(session.getStartTime()));
        txtEndTime.setText(formatTime(session.getEndTime()));
        txtVenue.setText(session.getVenue() == null ? "" : session.getVenue());
    }

    private void applyCourseSessionTypeRule() {
        CourseOption selectedCourse = (CourseOption) cmbCourse.getSelectedItem();
        String currentSelection = (String) cmbType.getSelectedItem();

        cmbType.removeAllItems();

        if (selectedCourse == null || selectedCourse.course == null) {
            for (String sessionType : SESSION_TYPES) {
                cmbType.addItem(sessionType);
            }
        } else {
            String courseSessionType = normalize(selectedCourse.course.getSessionType()).toUpperCase();
            if ("THEORY".equals(courseSessionType)) {
                cmbType.addItem("THEORY");
            } else if ("PRACTICAL".equals(courseSessionType)) {
                cmbType.addItem("PRACTICAL");
            } else {
                for (String sessionType : SESSION_TYPES) {
                    cmbType.addItem(sessionType);
                }
            }
        }

        if (currentSelection != null) {
            cmbType.setSelectedItem(currentSelection);
        }
        if (cmbType.getSelectedItem() == null && cmbType.getItemCount() > 0) {
            cmbType.setSelectedIndex(0);
        }
    }

    private void selectCourse(int courseId) {
        for (int i = 0; i < cmbCourse.getItemCount(); i++) {
            CourseOption option = cmbCourse.getItemAt(i);
            if (option.course.getId() == courseId) {
                cmbCourse.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectLecturer(Integer lecturerId) {
        for (int i = 0; i < cmbLecturer.getItemCount(); i++) {
            LecturerOption option = cmbLecturer.getItemAt(i);
            if (lecturerId == null && option.staff == null) {
                cmbLecturer.setSelectedIndex(i);
                return;
            }
            if (option.staff != null && lecturerId != null && option.staff.getId() == lecturerId) {
                cmbLecturer.setSelectedIndex(i);
                return;
            }
        }
    }

    private TimetableSessionRequest buildRequest(TimetableSession session) {
        TimetableSessionRequest request = new TimetableSessionRequest();
        request.setSessionId(session == null ? 0 : session.getId());
        request.setCourseId(String.valueOf(((CourseOption) cmbCourse.getSelectedItem()).course.getId()));

        LecturerOption lecturerOption = (LecturerOption) cmbLecturer.getSelectedItem();
        request.setLecturerId(lecturerOption == null || lecturerOption.staff == null ? "" : String.valueOf(lecturerOption.staff.getId()));
        request.setSessionType((String) cmbType.getSelectedItem());
        request.setDay((String) cmbDay.getSelectedItem());
        request.setStartTime(txtStartTime.getText().trim());
        request.setEndTime(txtEndTime.getText().trim());
        request.setVenue(txtVenue.getText().trim());
        return request;
    }

    private JPanel createField(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(AppTheme.fontPlain(13));
        label.setForeground(AppTheme.TEXT_DARK);

        panel.add(label, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private JComponent createHint(String text) {
        JLabel hint = new JLabel(text);
        hint.setFont(AppTheme.fontPlain(12));
        hint.setForeground(AppTheme.TEXT_SUBTLE);
        return hint;
    }

    private void styleComponent(JComponent component) {
        component.setFont(AppTheme.fontPlain(14));
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_MUTED),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }

    private String formatTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        return value.length() >= 5 ? value.substring(0, 5) : value;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
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

    private static class LecturerOption {
        private final Staff staff;

        private LecturerOption(Staff staff) {
            this.staff = staff;
        }

        @Override
        public String toString() {
            return staff == null ? "Not Assigned" : staff.getFirstName() + " " + staff.getLastName();
        }
    }
}
