package com.fot.system.view.dashboard.admin.manageCourses;

import com.fot.system.config.AppTheme;
import com.fot.system.model.AddCourseRequest;
import com.fot.system.model.Department;
import com.fot.system.model.Staff;
import com.fot.system.view.components.CustomButton;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddNewCoursePanel extends JPanel {

    private JTextField txtCourseCode;
    private JTextField txtCourseName;
    private JTextField txtCredits;
    private JTextField txtTotalHours;
    private JComboBox<String> cmbSessionType;
    private JComboBox<Department> cmbDepartment;
    private JComboBox<LecturerOption> cmbLecturer;

    private Runnable onCloseAction;
    private Runnable onSaveAction;

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

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCourseCode = new JTextField(15);
        txtCourseName = new JTextField(15);
        txtCredits = new JTextField(15);
        txtTotalHours = new JTextField(15);
        cmbSessionType = new JComboBox<>(new String[]{"THEORY", "PRACTICAL", "BOTH"});
        cmbDepartment = new JComboBox<>();
        cmbLecturer = new JComboBox<>();

        addFormRow(formPanel, "Course Code:", txtCourseCode, 0, gbc);
        addFormRow(formPanel, "Course Name:", txtCourseName, 1, gbc);
        addFormRow(formPanel, "Credits:", txtCredits, 2, gbc);
        addFormRow(formPanel, "Total Hours:", txtTotalHours, 3, gbc);
        addFormRow(formPanel, "Session Type:", cmbSessionType, 4, gbc);
        addFormRow(formPanel, "Department:", cmbDepartment, 5, gbc);
        addFormRow(formPanel, "Lecturer in Charge:", cmbLecturer, 6, gbc);

        return formPanel;
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
        btnClose.addActionListener(e -> {
            if (onCloseAction != null) {
                onCloseAction.run();
            }
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        CustomButton btnSave = new CustomButton(
                "Save Changes",
                AppTheme.BTN_SAVE_BG,
                AppTheme.BTN_SAVE_FG,
                AppTheme.BTN_SAVE_HOVER,
                new Dimension(150, 40)
        );
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

    public void resetForm() {
        txtCourseCode.setText("");
        txtCourseName.setText("");
        txtCredits.setText("");
        txtTotalHours.setText("");
        cmbSessionType.setSelectedItem("THEORY");

        if (cmbDepartment.getItemCount() > 0) {
            cmbDepartment.setSelectedIndex(0);
        }
        if (cmbLecturer.getItemCount() > 0) {
            cmbLecturer.setSelectedIndex(0);
        }
    }

    public AddCourseRequest buildRequest() {
        return new AddCourseRequest(
                txtCourseCode.getText().trim(),
                txtCourseName.getText().trim(),
                txtCredits.getText().trim(),
                txtTotalHours.getText().trim(),
                cmbSessionType.getSelectedItem() == null ? "" : cmbSessionType.getSelectedItem().toString(),
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
        if (model.getSize() > 0) {
            cmbDepartment.setSelectedIndex(0);
        }
    }

    public void setLecturers(List<Staff> lecturers) {
        DefaultComboBoxModel<LecturerOption> model = new DefaultComboBoxModel<>();
        model.addElement(new LecturerOption("", "Not Assigned"));
        for (Staff lecturer : lecturers) {
            model.addElement(new LecturerOption(String.valueOf(lecturer.getId()), lecturer.getFullName()));
        }
        cmbLecturer.setModel(model);
        cmbLecturer.setSelectedIndex(0);
    }

    public void setOnCloseAction(Runnable onCloseAction) {
        this.onCloseAction = onCloseAction;
    }

    public void setOnSaveAction(Runnable onSaveAction) {
        this.onSaveAction = onSaveAction;
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
            return ((LecturerOption) selectedItem).id;
        }
        return "";
    }

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

    private static class LecturerOption {
        private final String id;
        private final String name;

        private LecturerOption(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
