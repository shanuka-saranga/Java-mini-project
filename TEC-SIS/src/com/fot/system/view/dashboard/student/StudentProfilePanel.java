package com.fot.system.view.dashboard.student;

import com.fot.system.model.Student;
import com.fot.system.model.User;
import com.fot.system.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StudentProfilePanel extends JPanel {

    private final Student student;
    private final UserService userService;

    private JTextField registrationNoField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;

    private JButton editButton;
    private JButton saveButton;
    private JButton cancelButton;

    public StudentProfilePanel(User user) {
        this.student = (Student) user;
        this.userService = new UserService();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel("Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setBorder(new EmptyBorder(20, 25, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 247, 250));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        formPanel.setPreferredSize(new Dimension(460, 430));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        registrationNoField = new JTextField(student.getRegistrationNo() != null ? student.getRegistrationNo() : "");
        registrationNoField.setEditable(false);

        firstNameField = new JTextField(student.getFirstName() != null ? student.getFirstName() : "");
        lastNameField = new JTextField(student.getLastName() != null ? student.getLastName() : "");
        emailField = new JTextField(student.getEmail() != null ? student.getEmail() : "");
        phoneField = new JTextField(student.getPhone() != null ? student.getPhone() : "");
        addressArea = new JTextArea(student.getAddress() != null ? student.getAddress() : "", 4, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);

        setEditMode(false);

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Registration No", registrationNoField);
        addFormRow(formPanel, gbc, row++, "First Name", firstNameField);
        addFormRow(formPanel, gbc, row++, "Last Name", lastNameField);
        addFormRow(formPanel, gbc, row++, "Email", emailField);
        addFormRow(formPanel, gbc, row++, "Phone", phoneField);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Address"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setPreferredSize(new Dimension(220, 80));
        formPanel.add(addressScroll, gbc);
        row++;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        editButton = new JButton("Edit Profile");
        saveButton = new JButton("Save Changes");
        cancelButton = new JButton("Cancel");

        saveButton.setVisible(false);
        cancelButton.setVisible(false);

        editButton.addActionListener(e -> {
            setEditMode(true);
            editButton.setVisible(false);
            saveButton.setVisible(true);
            cancelButton.setVisible(true);
        });

        saveButton.addActionListener(e -> saveProfile());

        cancelButton.addActionListener(e -> {
            resetFields();
            setEditMode(false);
            editButton.setVisible(true);
            saveButton.setVisible(false);
            cancelButton.setVisible(false);
        });

        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(buttonPanel, gbc);

        wrapper.add(formPanel);
        add(wrapper, BorderLayout.CENTER);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void setEditMode(boolean editable) {
        registrationNoField.setEditable(false);
        firstNameField.setEditable(editable);
        lastNameField.setEditable(editable);
        emailField.setEditable(editable);
        phoneField.setEditable(editable);
        addressArea.setEditable(editable);
    }

    private void resetFields() {
        registrationNoField.setText(student.getRegistrationNo() != null ? student.getRegistrationNo() : "");
        firstNameField.setText(student.getFirstName() != null ? student.getFirstName() : "");
        lastNameField.setText(student.getLastName() != null ? student.getLastName() : "");
        emailField.setText(student.getEmail() != null ? student.getEmail() : "");
        phoneField.setText(student.getPhone() != null ? student.getPhone() : "");
        addressArea.setText(student.getAddress() != null ? student.getAddress() : "");
    }

    private void saveProfile() {
        student.setFirstName(firstNameField.getText().trim());
        student.setLastName(lastNameField.getText().trim());
        student.setEmail(emailField.getText().trim());
        student.setPhone(phoneField.getText().trim());
        student.setAddress(addressArea.getText().trim());

        boolean updated = userService.updateUserProfile(student);

        if (updated) {
            JOptionPane.showMessageDialog(this, "Profile updated successfully.");
            setEditMode(false);
            editButton.setVisible(true);
            saveButton.setVisible(false);
            cancelButton.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(this, "Profile update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}