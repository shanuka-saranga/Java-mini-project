package com.fot.system.view.dashboard.admin.manageCourses;

import com.fot.system.config.AppConfig;
import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.view.components.CustomButton;
import com.fot.system.view.components.ThemedComboBox;
import com.fot.system.view.components.ThemedRadioButton;
import com.fot.system.view.components.ThemedTextField;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddNewCoursePanel extends JPanel {
    private JTextField txtCourseCode;
    private JTextField txtCourseName;
    private JTextField txtCredits;
    private JTextField txtTotalHours;
    private JTextField txtNoOfQuizzes;
    private JTextField txtNoOfAssignments;
    private ThemedRadioButton rdoTheory;
    private ThemedRadioButton rdoPractical;
    private ThemedRadioButton rdoBoth;
    private JComboBox<Department> cmbDepartment;
    private JComboBox<LecturerOption> cmbLecturer;
    private Runnable onCloseAction;
    private Runnable onSaveAction;

    /**
     * initialize add new course form panel
     * @author janith
     */
    public AddNewCoursePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(AppTheme.PRIMARY),
                        " Add New Course "
                ),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JScrollPane scrollPane = new JScrollPane(createFormPanel());
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
        add(createBottomActions(), BorderLayout.SOUTH);
    }

    /**
     * create course form content panel
     * @author janith
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCourseCode = createTextField();
        txtCourseName = createTextField();
        txtCredits = createTextField();
        txtTotalHours = createTextField();
        txtNoOfQuizzes = createTextField();
        txtNoOfAssignments = createTextField();
        cmbDepartment = new ThemedComboBox<>();
        cmbLecturer = new ThemedComboBox<>();

        addFormRow(formPanel, "Course Code:", txtCourseCode, 0, gbc);
        addFormRow(formPanel, "Course Name:", txtCourseName, 1, gbc);
        addFormRow(formPanel, "Credits:", txtCredits, 2, gbc);
        addFormRow(formPanel, "Total Hours:", txtTotalHours, 3, gbc);
        addFormRow(formPanel, "Session Type:", createSessionTypePanel(), 4, gbc);
        addFormRow(formPanel, "No. of Quizzes:", txtNoOfQuizzes, 5, gbc);
        addFormRow(formPanel, "No. of Assignments:", txtNoOfAssignments, 6, gbc);
        addFormRow(formPanel, "Department:", cmbDepartment, 7, gbc);
        addFormRow(formPanel, "Lecturer in Charge:", cmbLecturer, 8, gbc);

        return formPanel;
    }

    /**
     * create default text input field
     * @author janith
     */
    private JTextField createTextField() {
        return new ThemedTextField(15);
    }

    /**
     * create session type radio options panel
     * @author janith
     */
    private JPanel createSessionTypePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panel.setOpaque(false);

        ButtonGroup sessionTypeGroup = new ButtonGroup();
        rdoTheory = new ThemedRadioButton("THEORY", true);
        rdoPractical = new ThemedRadioButton("PRACTICAL");
        rdoBoth = new ThemedRadioButton("BOTH");

        sessionTypeGroup.add(rdoTheory);
        sessionTypeGroup.add(rdoPractical);
        sessionTypeGroup.add(rdoBoth);

        panel.add(rdoTheory);
        panel.add(rdoPractical);
        panel.add(rdoBoth);
        return panel;
    }

    /**
     * create bottom action buttons panel
     * @author janith
     */
    private JPanel createBottomActions() {
        JPanel mainActionPanel = new JPanel(new BorderLayout());
        mainActionPanel.setOpaque(false);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);

        CustomButton btnClose = createCloseButton();
        btnClose.addActionListener(e -> {
            if (onCloseAction != null) {
                onCloseAction.run();
            }
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        CustomButton btnSave = createSaveButton();
        btnSave.addActionListener(e -> {
            if (onSaveAction != null) {
                onSaveAction.run();
            }
        });

        leftPanel.add(btnClose);
        rightPanel.add(btnSave);

        mainActionPanel.add(leftPanel, BorderLayout.WEST);
        mainActionPanel.add(rightPanel, BorderLayout.EAST);

        return mainActionPanel;
    }

    /**
     * create close button for form
     * @author janith
     */
    private CustomButton createCloseButton() {
        return new CustomButton(
                "Close",
                AppTheme.BTN_CANCEL_BG,
                AppTheme.BTN_CANCEL_FG,
                AppTheme.BTN_CANCEL_HOVER,
                AppConfig.BUTTON_SIZE_CLOSE
        );
    }

    /**
     * create save button for form
     * @author janith
     */
    private CustomButton createSaveButton() {
        return new CustomButton(
                "Save Changes",
                AppTheme.BTN_SAVE_BG,
                AppTheme.BTN_SAVE_FG,
                AppTheme.BTN_SAVE_HOVER,
                AppConfig.BUTTON_SIZE_SAVE
        );
    }

    /**
     * clear all form fields and reset defaults
     * @author janith
     */
    public void resetForm() {
        txtCourseCode.setText("");
        txtCourseName.setText("");
        txtCredits.setText("");
        txtTotalHours.setText("");
        txtNoOfQuizzes.setText(AppConfig.DEFAULT_QUIZ_COUNT);
        txtNoOfAssignments.setText(AppConfig.DEFAULT_ASSIGNMENT_COUNT);
        selectSessionType(AppConfig.DEFAULT_COURSE_SESSION_TYPE);

        if (cmbDepartment.getItemCount() > 0) {
            cmbDepartment.setSelectedIndex(0);
        }
        if (cmbLecturer.getItemCount() > 0) {
            cmbLecturer.setSelectedIndex(0);
        }
    }

    /**
     * build course request object from form values
     * @author janith
     */
    public AddCourseRequest buildRequest() {
        return new AddCourseRequest(
                txtCourseCode.getText().trim(),
                txtCourseName.getText().trim(),
                txtCredits.getText().trim(),
                txtTotalHours.getText().trim(),
                getSelectedSessionType(),
                txtNoOfQuizzes.getText().trim(),
                txtNoOfAssignments.getText().trim(),
                getDepartmentId(),
                getLecturerId()
        );
    }

    /**
     * set department list to department combo
     * @param departments department list
     * @author janith
     */
    public void setDepartments(List<Department> departments) {
        DefaultComboBoxModel<Department> model = new DefaultComboBoxModel<>();
        for (Department department : departments) {
            model.addElement(department);
        }
        cmbDepartment.setModel(model);
        if (model.getSize() > 0) {
            cmbDepartment.setSelectedIndex(0);
        }
    }

    /**
     * set lecturer list to lecturer combo
     * @param lecturers lecturer staff list
     * @author janith
     */
    public void setLecturers(List<Staff> lecturers) {
        DefaultComboBoxModel<LecturerOption> model = new DefaultComboBoxModel<>();
        model.addElement(new LecturerOption("", "Not Assigned"));
        for (Staff lecturer : lecturers) {
            model.addElement(new LecturerOption(String.valueOf(lecturer.getId()), lecturer.getFullName()));
        }
        cmbLecturer.setModel(model);
        cmbLecturer.setSelectedIndex(0);
    }

    /**
     * set close button callback action
     * @param onCloseAction close action callback
     * @author janith
     */
    public void setOnCloseAction(Runnable onCloseAction) {
        this.onCloseAction = onCloseAction;
    }

    /**
     * set save button callback action
     * @param onSaveAction save action callback
     * @author janith
     */
    public void setOnSaveAction(Runnable onSaveAction) {
        this.onSaveAction = onSaveAction;
    }

    /**
     * get selected department id from combo box
     * @author janith
     */
    private String getDepartmentId() {
        Object selectedItem = cmbDepartment.getSelectedItem();
        if (selectedItem instanceof Department) {
            return String.valueOf(((Department) selectedItem).getDepartmentId());
        }
        return "";
    }

    /**
     * get selected lecturer id from combo box
     * @author janith
     */
    private String getLecturerId() {
        Object selectedItem = cmbLecturer.getSelectedItem();
        if (selectedItem instanceof LecturerOption) {
            return ((LecturerOption) selectedItem).getId();
        }
        return "";
    }

    /**
     * add one label + field row to form grid
     * @param panel target form panel
     * @param label row label text
     * @param component row input component
     * @param row grid row index
     * @param gbc shared grid bag constraints
     * @author janith
     */
    private void addFormRow(JPanel panel, String label, Component component, int row, GridBagConstraints gbc) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        panel.add(component, gbc);
    }

    /**
     * get selected session type from radio buttons
     * @author janith
     */
    private String getSelectedSessionType() {
        if (rdoPractical.isSelected()) {
            return "PRACTICAL";
        }
        if (rdoBoth.isSelected()) {
            return "BOTH";
        }
        return "THEORY";
    }

    /**
     * select session type radio based on value
     * @param sessionType session type text
     * @author janith
     */
    private void selectSessionType(String sessionType) {
        if ("PRACTICAL".equalsIgnoreCase(sessionType)) {
            rdoPractical.setSelected(true);
            return;
        }
        if ("BOTH".equalsIgnoreCase(sessionType)) {
            rdoBoth.setSelected(true);
            return;
        }
        rdoTheory.setSelected(true);
    }
}
