package com.fot.system.view.dashboard.admin;

import com.fot.system.model.Staff;
import com.fot.system.model.Student;
import com.fot.system.model.User;
import javax.swing.*;
import java.awt.*;

public class EditUserDetailsPanel extends JPanel {
    private JTextField txtFirstName, txtLastName, txtEmail, txtPhone, txtAddress, txtExtra;
    private JLabel lblExtraEdit;
    private JComboBox<String> cmbStatus;

    public EditUserDetailsPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        initComponent();
    }

    private void initComponent() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtFirstName = new JTextField(15);
        txtLastName = new JTextField(15);
        txtEmail = new JTextField(15);
        txtPhone = new JTextField(15);
        txtAddress = new JTextField(15);
        txtExtra = new JTextField(15);
        lblExtraEdit = new JLabel("Extra Detail:");
        cmbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "PENDING"});

        addEditRow("First Name:", txtFirstName, 0, gbc);
        addEditRow("Last Name:", txtLastName, 1, gbc);
        addEditRow("Email:", txtEmail, 2, gbc);
        addEditRow("Phone:", txtPhone, 3, gbc);
        addEditRow("Address:", txtAddress, 4, gbc);
        addEditRow("Status:", cmbStatus, 5, gbc);

        gbc.gridy = 6; gbc.gridx = 0; gbc.weightx = 0.2;
        add(lblExtraEdit, gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        add(txtExtra, gbc);
    }

    public void setUserData(User user) {
        txtFirstName.setText(user.getFirstName());
        txtLastName.setText(user.getLastName());
        txtEmail.setText(user.getEmail());
        txtPhone.setText(user.getPhone());
        txtAddress.setText(user.getAddress());
        cmbStatus.setSelectedItem(user.getStatus());

        if (user instanceof Student) {
            lblExtraEdit.setText("Registration No:");
            txtExtra.setText(((Student) user).getRegistrationNo());
        } else if (user instanceof Staff) {
            lblExtraEdit.setText("Staff Code:");
            txtExtra.setText(((Staff) user).getStaffCode());
        } else {
            lblExtraEdit.setText("Extra Detail:");
            txtExtra.setText("");
        }
    }

    public void updateModel(User user) {
        user.setFirstName(txtFirstName.getText());
        user.setLastName(txtLastName.getText());
        user.setEmail(txtEmail.getText());
        user.setPhone(txtPhone.getText());
        user.setAddress(txtAddress.getText());
        user.setStatus(cmbStatus.getSelectedItem().toString());

        if (user instanceof Student) {
            ((Student) user).setRegistrationNo(txtExtra.getText());
        } else if (user instanceof Staff) {
            ((Staff) user).setStaffCode(txtExtra.getText());
        }
    }

    private void addEditRow(String label, Component c, int y, GridBagConstraints gbc) {
        gbc.gridy = y; gbc.gridx = 0; gbc.weightx = 0.2;
        add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        add(c, gbc);
    }
}
