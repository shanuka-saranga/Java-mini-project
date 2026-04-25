package com.fot.system.view.dashboard.admin.manageCourses;

import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EditCourseDetailsPanel extends JPanel {

    private JTextField txtCourseCode;
    private JTextField txtCourseName;
    private JTextField txtCredits;
    private JTextField txtTotalHours;
    private JTextField txtNoOfQuizzes;
    private JTextField txtNoOfAssignments;
    private JComboBox<String> cmbSessionType;
    private JComboBox<Department> cmbDepartment;
    private JComboBox<LecturerOption> cmbLecturer;
    private int courseId;

    public EditCourseDetailsPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCourseCode = new JTextField(15);
        txtCourseName = new JTextField(15);
        txtCredits = new JTextField(15);
        txtTotalHours = new JTextField(15);
        txtNoOfQuizzes = new JTextField(15);
        txtNoOfAssignments = new JTextField(15);
        cmbSessionType = new JComboBox<>(new String[]{"THEORY", "PRACTICAL", "BOTH"});
        cmbDepartment = new JComboBox<>();
        cmbLecturer = new JComboBox<>();

        addFormRow("Course Code:", txtCourseCode, 0, gbc);
        addFormRow("Course Name:", txtCourseName, 1, gbc);
        addFormRow("Credits:", txtCredits, 2, gbc);
        addFormRow("Total Hours:", txtTotalHours, 3, gbc);
        addFormRow("Session Type:", cmbSessionType, 4, gbc);
        addFormRow("No. of Quizzes:", txtNoOfQuizzes, 5, gbc);
        addFormRow("No. of Assignments:", txtNoOfAssignments, 6, gbc);
        addFormRow("Department:", cmbDepartment, 7, gbc);
        addFormRow("Lecturer in Charge:", cmbLecturer, 8, gbc);
    }

    public void setCourseData(Course course) {
        this.courseId = course.getId();
        txtCourseCode.setText(course.getCourseCode());
        txtCourseName.setText(course.getCourseName());
        txtCredits.setText(String.valueOf(course.getCredits()));
        txtTotalHours.setText(String.valueOf(course.getTotalHours()));
        cmbSessionType.setSelectedItem(course.getSessionType());
        txtNoOfQuizzes.setText(String.valueOf(course.getNoOfQuizzes()));
        txtNoOfAssignments.setText(String.valueOf(course.getNoOfAssignments()));
        selectDepartmentById(course.getDepartmentId());
        selectLecturerById(course.getLecturerInChargeId());
    }

    public EditCourseRequest buildRequest() {
        return new EditCourseRequest(
                courseId,
                txtCourseCode.getText().trim(),
                txtCourseName.getText().trim(),
                txtCredits.getText().trim(),
                txtTotalHours.getText().trim(),
                cmbSessionType.getSelectedItem() == null ? "" : cmbSessionType.getSelectedItem().toString(),
                txtNoOfQuizzes.getText().trim(),
                txtNoOfAssignments.getText().trim(),
                getDepartmentId(),
                getLecturerId()
        );
    }

    public void setDepartments(List<Department> departments) {
        DefaultComboBoxModel<Department> model = new DefaultComboBoxModel<>();
        for (Department department : departments) {
            model.addElement(department);
        }
        cmbDepartment.setModel(model);
    }

    public void setLecturers(List<Staff> lecturers) {
        DefaultComboBoxModel<LecturerOption> model = new DefaultComboBoxModel<>();
        model.addElement(new LecturerOption("", "Not Assigned"));
        for (Staff lecturer : lecturers) {
            model.addElement(new LecturerOption(String.valueOf(lecturer.getId()), lecturer.getFullName()));
        }
        cmbLecturer.setModel(model);
    }

    private String getDepartmentId() {
        Object selectedItem = cmbDepartment.getSelectedItem();
        if (selectedItem instanceof Department) {
            return String.valueOf(((Department) selectedItem).getDepartmentId());
        }
        return "";
    }

    private String getLecturerId() {
        Object selectedItem = cmbLecturer.getSelectedItem();
        if (selectedItem instanceof LecturerOption) {
            return ((LecturerOption) selectedItem).getId();
        }
        return "";
    }

    private void selectDepartmentById(int departmentId) {
        for (int i = 0; i < cmbDepartment.getItemCount(); i++) {
            Department department = cmbDepartment.getItemAt(i);
            if (department != null && department.getDepartmentId() == departmentId) {
                cmbDepartment.setSelectedIndex(i);
                return;
            }
        }
    }

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

    private void addFormRow(String label, Component component, int row, GridBagConstraints gbc) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        add(component, gbc);
    }

}
