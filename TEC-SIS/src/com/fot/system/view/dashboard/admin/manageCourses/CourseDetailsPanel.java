package com.fot.system.view.dashboard.admin.manageCourses;

import com.fot.system.config.AppConfig;
import com.fot.system.config.AppTheme;
import com.fot.system.controller.EditCourseController;
import com.fot.system.model.entity.*;
import com.fot.system.view.shared_components.CustomButton;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * show selected course details and edit/delete actions
 * @author janith
 */
public class CourseDetailsPanel extends JPanel {
    private static final String VIEW_CARD = "VIEW";
    private static final String EDIT_CARD = "EDIT";
    private static final Dimension DETAIL_LABEL_SIZE = new Dimension(330, 32);

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

    private Course currentCourse;
    private Runnable onCloseAction;
    private Runnable onCourseUpdatedAction;
    private Runnable onCourseDeletedAction;
    private Consumer<Boolean> onEditModeChangedAction;

    /**
     * initialize course details panel
     * @author janith
     */
    public CourseDetailsPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.BG_LIGHT);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(AppTheme.PRIMARY), " Course Details "),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        setVisible(false);

        container.add(createViewPanel(), VIEW_CARD);
        container.add(editCourseDetailsPanel, EDIT_CARD);

        add(container, BorderLayout.CENTER);
        add(createBottomActions(), BorderLayout.SOUTH);
    }

    /**
     * create read only details card
     * @author janith
     */
    private JPanel createViewPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.BG_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

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

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    /**
     * create details panel action button section
     * @author janith
     */
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
                AppConfig.BUTTON_SIZE_CLOSE
        );

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        CustomButton btnEdit = new CustomButton(
                "Edit Course",
                AppTheme.BTN_EDIT_BG,
                AppTheme.BTN_EDIT_FG,
                AppTheme.BTN_EDIT_HOVER,
                AppConfig.BUTTON_SIZE_SAVE
        );

        CustomButton btnDelete = new CustomButton(
                "Delete",
                AppTheme.BTN_DELETE_BG,
                AppTheme.BTN_DELETE_FG,
                AppTheme.BTN_DELETE_HOVER,
                AppConfig.BUTTON_SIZE_CLOSE
        );

        CustomButton btnSave = new CustomButton(
                "Save Changes",
                AppTheme.BTN_SAVE_BG,
                AppTheme.BTN_SAVE_FG,
                AppTheme.BTN_SAVE_HOVER,
                AppConfig.BUTTON_SIZE_SAVE
        );

        CustomButton btnCancel = new CustomButton(
                "Cancel",
                AppTheme.BTN_CANCEL_BG,
                AppTheme.BTN_CANCEL_FG,
                AppTheme.BTN_CANCEL_HOVER,
                AppConfig.BUTTON_SIZE_CLOSE
        );

        btnSave.setVisible(false);
        btnCancel.setVisible(false);

        btnClose.addActionListener(e -> {
            notifyEditModeChanged(false);
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
            notifyEditModeChanged(true);
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
            notifyEditModeChanged(false);
        });

        btnSave.addActionListener(e -> {
            if (saveUpdatedData()) {
                cardLayout.show(container, VIEW_CARD);
                btnSave.setVisible(false);
                btnCancel.setVisible(false);
                btnEdit.setVisible(true);
                btnClose.setVisible(true);
                notifyEditModeChanged(false);
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

    /**
     * save updated course data from edit form
     * @author janith
     */
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

    /**
     * delete currently selected course
     * @author janith
     */
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
            notifyEditModeChanged(false);
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
        label.setFont(AppTheme.fontPlain(14));
        label.setForeground(AppTheme.TEXT_DARK);
        label.setIcon(FontIcon.of(icon, 16, AppTheme.ICON_ACCENT));
        label.setIconTextGap(12);
        label.setPreferredSize(DETAIL_LABEL_SIZE);
        label.setMinimumSize(DETAIL_LABEL_SIZE);
        return label;
    }

    /**
     * apply selected course values to details card
     * @param course selected course
     * @author janith
     */
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
        notifyEditModeChanged(false);
        setVisible(true);
    }

    /**
     * set close action callback
     * @param onCloseAction close callback
     * @author janith
     */
    public void setOnCloseAction(Runnable onCloseAction) {
        this.onCloseAction = onCloseAction;
    }

    /**
     * set course updated callback
     * @param onCourseUpdatedAction update callback
     * @author janith
     */
    public void setOnCourseUpdatedAction(Runnable onCourseUpdatedAction) {
        this.onCourseUpdatedAction = onCourseUpdatedAction;
    }

    /**
     * set course deleted callback
     * @param onCourseDeletedAction delete callback
     * @author janith
     */
    public void setOnCourseDeletedAction(Runnable onCourseDeletedAction) {
        this.onCourseDeletedAction = onCourseDeletedAction;
    }

    /**
     * set edit mode state callback
     * @param onEditModeChangedAction edit mode callback
     * @author janith
     */
    public void setOnEditModeChangedAction(Consumer<Boolean> onEditModeChangedAction) {
        this.onEditModeChangedAction = onEditModeChangedAction;
    }

    /**
     * set department lookup data for edit form
     * @param departments department list
     * @author janith
     */
    public void setDepartments(List<Department> departments) {
        editCourseDetailsPanel.setDepartments(departments);
    }

    /**
     * set lecturer lookup data for edit form
     * @param lecturers lecturer list
     * @author janith
     */
    public void setLecturers(List<Staff> lecturers) {
        editCourseDetailsPanel.setLecturers(lecturers);
    }

    private void notifyEditModeChanged(boolean editing) {
        if (onEditModeChangedAction != null) {
            onEditModeChangedAction.accept(editing);
        }
    }
}
