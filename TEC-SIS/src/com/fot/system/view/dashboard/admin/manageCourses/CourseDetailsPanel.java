package com.fot.system.view.dashboard.admin.manageCourses;

import com.fot.system.config.AppTheme;
import com.fot.system.controller.EditCourseController;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.view.components.CustomButton;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CourseDetailsPanel extends JPanel {
    private static final String VIEW_CARD = "VIEW";
    private static final String EDIT_CARD = "EDIT";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel container = new JPanel(cardLayout);
    private final EditCourseDetailsPanel editCourseDetailsPanel = new EditCourseDetailsPanel();
    private final EditCourseController editCourseController = new EditCourseController();

    private JLabel lblCourseCode;
    private JLabel lblCourseName;
    private JLabel lblCredits;
    private JLabel lblHours;
    private JLabel lblSessionType;
    private JLabel lblNoOfQuizzes;
    private JLabel lblNoOfAssignments;
    private JLabel lblDepartment;
    private JLabel lblLecturer;

    private final Color tealColor = new Color(0, 121, 107);
    private Course currentCourse;
    private Runnable onCloseAction;
    private Runnable onCourseUpdatedAction;
    private Runnable onCourseDeletedAction;

    public CourseDetailsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(tealColor), " Course Details "),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        setVisible(false);

        container.add(createViewPanel(), VIEW_CARD);
        container.add(editCourseDetailsPanel, EDIT_CARD);

        add(container, BorderLayout.CENTER);
        add(createBottomActions(), BorderLayout.SOUTH);
    }

    private JPanel createViewPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        lblCourseCode = createStyledLabel("Course Code: -", FontAwesomeSolid.BOOK);
        lblCourseName = createStyledLabel("Course Name: -", FontAwesomeSolid.BOOK_OPEN);
        lblCredits = createStyledLabel("Credits: -", FontAwesomeSolid.CALCULATOR);
        lblHours = createStyledLabel("Total Hours: -", FontAwesomeSolid.CLOCK);
        lblSessionType = createStyledLabel("Session Type: -", FontAwesomeSolid.LAYER_GROUP);
        lblNoOfQuizzes = createStyledLabel("No. of Quizzes: -", FontAwesomeSolid.LIST_OL);
        lblNoOfAssignments = createStyledLabel("No. of Assignments: -", FontAwesomeSolid.TASKS);
        lblDepartment = createStyledLabel("Department: -", FontAwesomeSolid.BUILDING);
        lblLecturer = createStyledLabel("Lecturer: -", FontAwesomeSolid.USER_TIE);

        addToGrid(panel, lblCourseCode, 0, 0, gbc);
        addToGrid(panel, lblCourseName, 1, 0, gbc);
        addToGrid(panel, lblCredits, 0, 1, gbc);
        addToGrid(panel, lblHours, 1, 1, gbc);
        addToGrid(panel, lblSessionType, 0, 2, gbc);
        addToGrid(panel, lblNoOfQuizzes, 1, 2, gbc);
        addToGrid(panel, lblNoOfAssignments, 0, 3, gbc);
        addToGrid(panel, lblDepartment, 1, 3, gbc);

        gbc.gridwidth = 2;
        addToGrid(panel, lblLecturer, 0, 4, gbc);

        return panel;
    }

    private JPanel createBottomActions() {
        JPanel mainActionPanel = new JPanel(new BorderLayout());
        mainActionPanel.setOpaque(false);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        CustomButton btnClose = new CustomButton(
                "Close",
                AppTheme.BTN_CANCEL_BG,
                AppTheme.BTN_CANCEL_FG,
                AppTheme.BTN_CANCEL_HOVER,
                new Dimension(120, 40)
        );

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        CustomButton btnEdit = new CustomButton(
                "Edit Course",
                AppTheme.BTN_EDIT_BG,
                AppTheme.BTN_EDIT_FG,
                AppTheme.BTN_EDIT_HOVER,
                new Dimension(150, 40)
        );

        CustomButton btnDelete = new CustomButton(
                "Delete",
                AppTheme.BTN_DELETE_BG,
                AppTheme.BTN_DELETE_FG,
                AppTheme.BTN_DELETE_HOVER,
                new Dimension(120, 40)
        );

        CustomButton btnSave = new CustomButton(
                "Save Changes",
                AppTheme.BTN_SAVE_BG,
                AppTheme.BTN_SAVE_FG,
                AppTheme.BTN_SAVE_HOVER,
                new Dimension(150, 40)
        );

        CustomButton btnCancel = new CustomButton(
                "Cancel",
                AppTheme.BTN_CANCEL_BG,
                AppTheme.BTN_CANCEL_FG,
                AppTheme.BTN_CANCEL_HOVER,
                new Dimension(120, 40)
        );

        btnSave.setVisible(false);
        btnCancel.setVisible(false);

        btnClose.addActionListener(e -> {
            setVisible(false);
            if (onCloseAction != null) {
                onCloseAction.run();
            }
        });

        btnEdit.addActionListener(e -> {
            if (currentCourse == null) {
                return;
            }

            editCourseDetailsPanel.setCourseData(currentCourse);
            cardLayout.show(container, EDIT_CARD);
            btnEdit.setVisible(false);
            btnClose.setVisible(false);
            btnSave.setVisible(true);
            btnCancel.setVisible(true);
        });

        btnDelete.addActionListener(e -> deleteCurrentCourse());

        btnCancel.addActionListener(e -> {
            if (currentCourse != null) {
                editCourseDetailsPanel.setCourseData(currentCourse);
            }
            cardLayout.show(container, VIEW_CARD);
            btnSave.setVisible(false);
            btnCancel.setVisible(false);
            btnEdit.setVisible(true);
            btnClose.setVisible(true);
        });

        btnSave.addActionListener(e -> {
            if (saveUpdatedData()) {
                cardLayout.show(container, VIEW_CARD);
                btnSave.setVisible(false);
                btnCancel.setVisible(false);
                btnEdit.setVisible(true);
                btnClose.setVisible(true);
            }
        });

        leftPanel.add(btnClose);

        rightPanel.add(btnDelete);
        rightPanel.add(btnEdit);
        rightPanel.add(btnSave);
        rightPanel.add(btnCancel);

        mainActionPanel.add(leftPanel, BorderLayout.WEST);
        mainActionPanel.add(rightPanel, BorderLayout.EAST);

        return mainActionPanel;
    }

    private boolean saveUpdatedData() {
        if (currentCourse == null) {
            return false;
        }

        try {
            Course updatedCourse = editCourseController.updateCourse(editCourseDetailsPanel.buildRequest());
            updateDetails(updatedCourse);
            if (onCourseUpdatedAction != null) {
                onCourseUpdatedAction.run();
            }
            JOptionPane.showMessageDialog(this, "Course updated successfully!");
            return true;
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Edit Course Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void deleteCurrentCourse() {
        if (currentCourse == null) {
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete " + currentCourse.getCourseCode() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            editCourseController.deleteCourse(currentCourse.getId());
            JOptionPane.showMessageDialog(this, "Course deleted successfully!");
            setVisible(false);

            if (onCourseDeletedAction != null) {
                onCourseDeletedAction.run();
            } else if (onCloseAction != null) {
                onCloseAction.run();
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete Course Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addToGrid(JPanel panel, Component component, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 1.0;
        panel.add(component, gbc);
    }

    private JLabel createStyledLabel(String text, FontAwesomeSolid icon) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setIcon(FontIcon.of(icon, 16, tealColor));
        label.setIconTextGap(12);
        return label;
    }

    public void updateDetails(Course course) {
        this.currentCourse = course;

        lblCourseCode.setText("Course Code: " + course.getCourseCode());
        lblCourseName.setText("Course Name: " + course.getCourseName());
        lblCredits.setText("Credits: " + course.getCredits());
        lblHours.setText("Total Hours: " + course.getTotalHours());
        lblSessionType.setText("Session Type: " + course.getSessionType());
        lblNoOfQuizzes.setText("No. of Quizzes: " + course.getNoOfQuizzes());
        lblNoOfAssignments.setText("No. of Assignments: " + course.getNoOfAssignments());
        lblDepartment.setText("Department: " + course.getDepartmentName());
        lblLecturer.setText("Lecturer: " + (course.getLecturerInChargeName() == null ? "Not Assigned" : course.getLecturerInChargeName()));
        editCourseDetailsPanel.setCourseData(course);

        cardLayout.show(container, VIEW_CARD);
        setVisible(true);
    }

    public void setOnCloseAction(Runnable onCloseAction) {
        this.onCloseAction = onCloseAction;
    }

    public void setOnCourseUpdatedAction(Runnable onCourseUpdatedAction) {
        this.onCourseUpdatedAction = onCourseUpdatedAction;
    }

    public void setOnCourseDeletedAction(Runnable onCourseDeletedAction) {
        this.onCourseDeletedAction = onCourseDeletedAction;
    }

    public void setDepartments(List<Department> departments) {
        editCourseDetailsPanel.setDepartments(departments);
    }

    public void setLecturers(List<Staff> lecturers) {
        editCourseDetailsPanel.setLecturers(lecturers);
    }
}
