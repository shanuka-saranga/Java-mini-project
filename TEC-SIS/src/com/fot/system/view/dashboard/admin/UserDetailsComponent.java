package com.fot.system.view.dashboard.admin.manageUsers;

import com.fot.system.model.Staff;
import com.fot.system.model.Student;
import com.fot.system.model.User;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;

public class UserDetailsComponent extends JPanel {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel container = new JPanel(cardLayout);

    // View Labels
    private JLabel lblFirstName, lblLastName, lblEmail, lblRole, lblStatus, lblPhone, lblAddress, lblExtra;

    // Edit Fields
    private JTextField txtFirstName, txtLastName, txtEmail, txtPhone, txtAddress, txtExtra;
    private JLabel lblExtraEdit; // Edit mode එකේදී Reg No/Staff Code කියන label එක මාරු කරන්න
    private JComboBox<String> cmbStatus;

    private final Color TEAL_COLOR = new Color(0, 121, 107);
    private User currentUser;

    public UserDetailsComponent() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(TEAL_COLOR), " User Profile Details "),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        setVisible(false);

        container.add(createViewPanel(), "VIEW");
        container.add(createEditPanel(), "EDIT");

        add(container, BorderLayout.CENTER);
        add(createBottomActions(), BorderLayout.SOUTH);
    }

    private JPanel createViewPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        lblFirstName = createStyledLabel("First Name: -", FontAwesomeSolid.USER);
        lblLastName = createStyledLabel("Last Name: -", FontAwesomeSolid.USER);
        lblEmail = createStyledLabel("Email: -", FontAwesomeSolid.ENVELOPE);
        lblPhone = createStyledLabel("Phone: -", FontAwesomeSolid.PHONE);
        lblRole = createStyledLabel("Role: -", FontAwesomeSolid.USER_TAG);
        lblStatus = createStyledLabel("Status: -", FontAwesomeSolid.INFO_CIRCLE);
        lblAddress = createStyledLabel("Address: -", FontAwesomeSolid.MAP_MARKER_ALT);
        lblExtra = createStyledLabel("-", FontAwesomeSolid.ID_CARD);

        // Grid එකට පිළිවෙලට එකතු කරමු
        addToGrid(panel, lblFirstName, 0, 0, gbc);
        addToGrid(panel, lblLastName, 1, 0, gbc);
        addToGrid(panel, lblEmail, 0, 1, gbc);
        addToGrid(panel, lblPhone, 1, 1, gbc);
        addToGrid(panel, lblRole, 0, 2, gbc);
        addToGrid(panel, lblStatus, 1, 2, gbc);

        gbc.gridwidth = 2; // Address එකට සම්පූර්ණ ඉඩ ගමු
        addToGrid(panel, lblAddress, 0, 3, gbc);
        addToGrid(panel, lblExtra, 0, 4, gbc);

        return panel;
    }

    private JPanel createEditPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtFirstName = new JTextField(15);
        txtLastName = new JTextField(15);
        txtEmail = new JTextField(15);
        txtPhone = new JTextField(15);
        txtAddress = new JTextField(15);
        txtExtra = new JTextField(15);
        lblExtraEdit = new JLabel("Extra Detail:"); // මේක dynamically මාරු වෙනවා
        cmbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "PENDING"});

        // Rows එකතු කිරීම
        addEditRow(panel, "First Name:", txtFirstName, 0, gbc);
        addEditRow(panel, "Last Name:", txtLastName, 1, gbc);
        addEditRow(panel, "Email:", txtEmail, 2, gbc);
        addEditRow(panel, "Phone:", txtPhone, 3, gbc);
        addEditRow(panel, "Address:", txtAddress, 4, gbc);
        addEditRow(panel, "Status:", cmbStatus, 5, gbc);

        // අමතර දත්ත (Reg No/Staff Code) Row එක
        gbc.gridy = 6; gbc.gridx = 0; gbc.weightx = 0.2;
        panel.add(lblExtraEdit, gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        panel.add(txtExtra, gbc);

        return panel;
    }

    private JPanel createBottomActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);

        JButton btnEdit = new JButton("Edit Profile");
        JButton btnSave = new JButton("Save Changes");
        JButton btnCancel = new JButton("Cancel");

        btnSave.setBackground(TEAL_COLOR);
        btnSave.setForeground(Color.WHITE);
        btnSave.setVisible(false);
        btnCancel.setVisible(false);

        btnEdit.addActionListener(e -> {
            cardLayout.show(container, "EDIT");
            btnEdit.setVisible(false);
            btnSave.setVisible(true);
            btnCancel.setVisible(true);
        });

        btnCancel.addActionListener(e -> {
            cardLayout.show(container, "VIEW");
            btnSave.setVisible(false);
            btnCancel.setVisible(false);
            btnEdit.setVisible(true);
        });

        btnSave.addActionListener(e -> {
            // මෙතනදී UI එකෙන් දත්ත අරන් currentUser එක update කරලා Service එකට යවන්න
            saveUpdatedData();
            cardLayout.show(container, "VIEW");
            btnSave.setVisible(false);
            btnCancel.setVisible(false);
            btnEdit.setVisible(true);
        });

        panel.add(btnEdit);
        panel.add(btnSave);
        panel.add(btnCancel);
        return panel;
    }

    private void saveUpdatedData() {
        // UI fields වලින් දත්ත අරගෙන model එක update කිරීම
        currentUser.setFirstName(txtFirstName.getText());
        currentUser.setLastName(txtLastName.getText());
        currentUser.setEmail(txtEmail.getText());
        currentUser.setPhone(txtPhone.getText());
        currentUser.setAddress(txtAddress.getText());
        currentUser.setStatus(cmbStatus.getSelectedItem().toString());

        // TODO: මෙතනදී UserService.updateUser(currentUser) call කරන්න
        updateDetails(currentUser); // View එක refresh කරන්න
        JOptionPane.showMessageDialog(this, "Profile updated successfully!");
    }

    private void addToGrid(JPanel p, Component c, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y; gbc.weightx = 1.0;
        p.add(c, gbc);
    }

    private void addEditRow(JPanel p, String label, Component c, int y, GridBagConstraints gbc) {
        gbc.gridy = y; gbc.gridx = 0; gbc.weightx = 0.2;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        p.add(c, gbc);
    }

    private JLabel createStyledLabel(String text, FontAwesomeSolid icon) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setIcon(FontIcon.of(icon, 16, TEAL_COLOR));
        label.setIconTextGap(12);
        return label;
    }

    public void updateDetails(User user) {
        System.out.println(user.toString());
        this.currentUser = user;

        // 1. View Mode Update
        lblFirstName.setText("First Name: " + user.getFirstName());
        lblLastName.setText("Last Name: " + user.getLastName());
        lblEmail.setText("Email: " + user.getEmail());
        lblPhone.setText("Phone: " + user.getPhone());
        lblRole.setText("Role: " + user.getRole());
        lblStatus.setText("Status: " + user.getStatus());
        lblAddress.setText("Address: " + user.getAddress());

        // 2. Edit Mode Pre-fill
        txtFirstName.setText(user.getFirstName());
        txtLastName.setText(user.getLastName());
        txtEmail.setText(user.getEmail());
        txtPhone.setText(user.getPhone());
        txtAddress.setText(user.getAddress());
        cmbStatus.setSelectedItem(user.getStatus());

        // 3. Sub-class data (Student/Staff)
        if (user instanceof Student) {
            String regNo = ((Student) user).getRegistrationNo();
            lblExtra.setText("Registration No: " + regNo);
            lblExtraEdit.setText("Registration No:");
            txtExtra.setText(regNo);
            lblExtra.setVisible(true);
        } else if (user instanceof Staff) {
            String staffCode = ((Staff) user).getStaffCode();
            lblExtra.setText("Staff Code: " + staffCode);
            lblExtraEdit.setText("Staff Code:");
            txtExtra.setText(staffCode);
            lblExtra.setVisible(true);
        } else {
            lblExtra.setVisible(false);
        }

        cardLayout.show(container, "VIEW");
        setVisible(true);
    }
}