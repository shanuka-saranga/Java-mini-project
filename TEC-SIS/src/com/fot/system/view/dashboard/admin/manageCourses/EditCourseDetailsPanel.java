package com.fot.system.view.dashboard.admin.manageCourses;

import com.fot.system.config.AppConfig;
import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.view.shared_components.ThemedComboBox;
import com.fot.system.view.shared_components.ThemedRadioButton;
import com.fot.system.view.shared_components.ThemedTextField;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * render editable course form for details panel
 * @author janith
 */
public class EditCourseDetailsPanel extends JPanel {
    private static final int LABEL_COLUMN_WIDTH = 170;
    private static final Dimension INPUT_SIZE = new Dimension(280, 34);
    private static final Dimension INPUT_MIN_SIZE = new Dimension(220, 34);


    private JTextField txtCourseCode;
    private JTextField txtCourseName;
    private JTextField txtCredits;
    private JTextField txtTotalHours;
    private JTextField txtNoOfQuizzes;
    private JTextField txtNoOfAssignments;
    private ButtonGroup sessionTypeGroup;
    private ThemedRadioButton rdoTheory;
    private ThemedRadioButton rdoPractical;
    private ThemedRadioButton rdoBoth;
    private JComboBox<Department> cmbDepartment;
    private JComboBox<LecturerOption> cmbLecturer;
    private int courseId;

    /**
     * initialize edit course form panel
     * @author janith
     */
    public EditCourseDetailsPanel() {
        setLayout(new GridBagLayout());
        setBackground(AppTheme.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtCourseCode = createTextField();
        txtCourseName = createTextField();
        txtCredits = createTextField();
        txtTotalHours = createTextField();
        txtNoOfQuizzes = createTextField();
        txtNoOfAssignments = createTextField();
        cmbDepartment = new ThemedComboBox<>();
        cmbLecturer = new ThemedComboBox<>();

        styleInputComponent(cmbDepartment);
        styleInputComponent(cmbLecturer);

        addFormRow("Course Code:", txtCourseCode, 0, gbc);
        addFormRow("Course Name:", txtCourseName, 1, gbc);
        addFormRow("Credits:", txtCredits, 2, gbc);
        addFormRow("Total Hours:", txtTotalHours, 3, gbc);
        addFormRow("Session Type:", createSessionTypePanel(), 4, gbc);
        addFormRow("No. of Quizzes:", txtNoOfQuizzes, 5, gbc);
        addFormRow("No. of Assignments:", txtNoOfAssignments, 6, gbc);
        addFormRow("Department:", cmbDepartment, 7, gbc);
        addFormRow("Lecturer in Charge:", cmbLecturer, 8, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        add(Box.createVerticalGlue(), gbc);
    }

    /**
     * bind selected course values to form controls
     * @param course selected course
     * @author janith
     */
    public void setCourseData(Course course) {
        this.courseId = course.getId();
        txtCourseCode.setText(course.getCourseCode());
        txtCourseName.setText(course.getCourseName());
        txtCredits.setText(String.valueOf(course.getCredits()));
        txtTotalHours.setText(String.valueOf(course.getTotalHours()));
        selectSessionType(course.getSessionType());
        txtNoOfQuizzes.setText(String.valueOf(course.getNoOfQuizzes()));
        txtNoOfAssignments.setText(String.valueOf(course.getNoOfAssignments()));
        selectDepartmentById(course.getDepartmentId());
        selectLecturerById(course.getLecturerInChargeId());
    }

    /**
     * build edit course request from form values
     * @author janith
     */
    public EditCourseRequest buildRequest() {
        return new EditCourseRequest(
                courseId,
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
     * set department lookup list
     * @param departments department list
     * @author janith
     */
    public void setDepartments(List<Department> departments) {
        DefaultComboBoxModel<Department> model = new DefaultComboBoxModel<>();
        for (Department department : departments) {
            model.addElement(department);
        }
        cmbDepartment.setModel(model);
    }

    /**
     * set lecturer lookup list
     * @param lecturers lecturer list
     * @author janith
     */
    public void setLecturers(List<Staff> lecturers) {
        DefaultComboBoxModel<LecturerOption> model = new DefaultComboBoxModel<>();
        model.addElement(new LecturerOption("", "Not Assigned"));
        for (Staff lecturer : lecturers) {
            model.addElement(new LecturerOption(String.valueOf(lecturer.getId()), lecturer.getFullName()));
        }
        cmbLecturer.setModel(model);
    }

    /**
     * get selected department id value
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
     * get selected lecturer id value
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
     * select department item by id
     * @param departmentId department id
     * @author janith
     */
    private void selectDepartmentById(int departmentId) {
        for (int i = 0; i < cmbDepartment.getItemCount(); i++) {
            Department department = cmbDepartment.getItemAt(i);
            if (department != null && department.getDepartmentId() == departmentId) {
                cmbDepartment.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * select lecturer item by id
     * @param lecturerId lecturer id
     * @author janith
     */
    private void selectLecturerById(Integer lecturerId) {
        if (lecturerId == null) {
            cmbLecturer.setSelectedIndex(0);
            return;
        }

        for (int i = 0; i < cmbLecturer.getItemCount(); i++) {
            LecturerOption option = cmbLecturer.getItemAt(i);
            if (option != null && String.valueOf(lecturerId).equals(option.getId())) {
                cmbLecturer.setSelectedIndex(i);
                return;
            }
        }
        cmbLecturer.setSelectedIndex(0);
    }

    /**
     * add labeled component row into form layout
     * @author janith
     */
    private void addFormRow(String label, Component component, int row, GridBagConstraints gbc) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FORM_LABEL_FONT);
        lbl.setForeground(AppTheme.TEXT_DARK);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl.setPreferredSize(new Dimension(LABEL_COLUMN_WIDTH, INPUT_SIZE.height));
        lbl.setMinimumSize(new Dimension(LABEL_COLUMN_WIDTH, INPUT_SIZE.height));
        add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(component, gbc);
    }

    /**
     * create themed text field input
     * @author janith
     */
    private JTextField createTextField() {
        JTextField textField = new ThemedTextField(18);
        styleInputComponent(textField);
        return textField;
    }

    /**
     * create course session type radio options
     * @author janith
     */
    private JPanel createSessionTypePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(INPUT_SIZE);
        panel.setMinimumSize(INPUT_MIN_SIZE);

        sessionTypeGroup = new ButtonGroup();
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
     * resolve selected session type
     * @author janith
     */
    private String getSelectedSessionType() {
        if (rdoPractical.isSelected()) {
            return AppConfig.COURSE_SESSION_TYPES[1];
        }
        if (rdoBoth.isSelected()) {
            return AppConfig.COURSE_SESSION_TYPES[2];
        }
        return AppConfig.COURSE_SESSION_TYPES[0];
    }

    /**
     * select session type radio by value
     * @param sessionType session type value
     * @author janith
     */
    private void selectSessionType(String sessionType) {
        if (AppConfig.COURSE_SESSION_TYPES[1].equalsIgnoreCase(sessionType)) {
            rdoPractical.setSelected(true);
            return;
        }
        if (AppConfig.COURSE_SESSION_TYPES[2].equalsIgnoreCase(sessionType)) {
            rdoBoth.setSelected(true);
            return;
        }
        rdoTheory.setSelected(true);
    }

    /**
     * apply shared input sizing and font
     * @param component input component
     * @author janith
     */
    private void styleInputComponent(JComponent component) {
        component.setFont(AppTheme.FORM_INPUT_FONT);
        component.setPreferredSize(INPUT_SIZE);
        component.setMinimumSize(INPUT_MIN_SIZE);
    }
}
