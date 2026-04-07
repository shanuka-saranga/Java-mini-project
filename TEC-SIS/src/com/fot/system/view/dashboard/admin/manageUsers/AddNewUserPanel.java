package com.fot.system.view.dashboard.admin.manageUsers;

import com.fot.system.config.AppConfig;
import com.fot.system.config.AppTheme;
import com.fot.system.model.AddUserRequest;
import com.fot.system.model.Department;
import com.fot.system.view.components.CustomButton;
import com.fot.system.view.components.ProfilePhotoFrame;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;

public class AddNewUserPanel extends JPanel {
    private static final String STUDENT_CARD = "STUDENT";
    private static final String STAFF_CARD = "STAFF";

    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JTextField txtProfilePicture;
    private JTextField txtDob;
    private JComboBox<Department> cmbDepartment;
    private ProfilePhotoFrame profilePhotoFrame;

    private JComboBox<String> cmbRole;
    private JComboBox<String> cmbStatus;

    private final CardLayout roleCardLayout = new CardLayout();
    private final JPanel roleSpecificPanel = new JPanel(roleCardLayout);

    private JTextField txtRegistrationNo;
    private JTextField txtRegistrationYear;
    private JComboBox<String> cmbStudentType;

    private JTextField txtStaffCode;
    private JTextField txtDesignation;

    private Runnable onCloseAction;
    private Runnable onSaveAction;

    public AddNewUserPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(AppTheme.PRIMARY),
                        " Add New User "
                ),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JScrollPane scrollPane = new JScrollPane(createFormPanel());
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
        add(createBottomActions(), BorderLayout.SOUTH);
        updateRoleSpecificFields();
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtFirstName = new JTextField(15);
        txtLastName = new JTextField(15);
        txtEmail = new JTextField(15);
        txtPassword = new JPasswordField(15);
        txtPhone = new JTextField(15);
        txtAddress = new JTextField(15);
        txtProfilePicture = new JTextField(15);
        txtProfilePicture.setEditable(false);
        txtDob = new JTextField(15);
        cmbDepartment = new JComboBox<>();
        profilePhotoFrame = new ProfilePhotoFrame("No image selected");

        cmbRole = new JComboBox<>(new String[]{
                AppConfig.ROLE_STUDENT,
                AppConfig.ROLE_LECTURER,
                AppConfig.ROLE_TO,
                AppConfig.ROLE_DEAN
        });
        cmbRole.addActionListener(e -> updateRoleSpecificFields());

        cmbStatus = new JComboBox<>(new String[]{
                AppConfig.STATUS_ACTIVE,
                AppConfig.STATUS_BLOCKED,
                "SUSPENDED"
        });

        addFormRow(formPanel, "User Role:", cmbRole, 0, gbc);
        addFormRow(formPanel, "First Name:", txtFirstName, 1, gbc);
        addFormRow(formPanel, "Last Name:", txtLastName, 2, gbc);
        addFormRow(formPanel, "Email:", txtEmail, 3, gbc);
        addFormRow(formPanel, "Password:", txtPassword, 4, gbc);
        addFormRow(formPanel, "Phone:", txtPhone, 5, gbc);
        addFormRow(formPanel, "Address:", txtAddress, 6, gbc);
        addFormRow(formPanel, "Profile Picture:", createProfilePictureSelector(), 7, gbc);
        addFormRow(formPanel, "Preview:", profilePhotoFrame, 8, gbc);
        addFormRow(formPanel, "DOB (yyyy-mm-dd):", txtDob, 9, gbc);
        addFormRow(formPanel, "Department:", cmbDepartment, 10, gbc);
        addFormRow(formPanel, "Status:", cmbStatus, 11, gbc);

        initializeRoleSpecificPanel();

        gbc.gridy = 12;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        formPanel.add(roleSpecificPanel, gbc);

        return formPanel;
    }

    private void initializeRoleSpecificPanel() {
        roleSpecificPanel.setBackground(Color.WHITE);

        JPanel studentPanel = new JPanel(new GridBagLayout());
        studentPanel.setBackground(Color.WHITE);

        GridBagConstraints studentGbc = new GridBagConstraints();
        studentGbc.insets = new Insets(5, 10, 5, 10);
        studentGbc.fill = GridBagConstraints.HORIZONTAL;

        txtRegistrationNo = new JTextField(15);
        txtRegistrationYear = new JTextField(15);
        cmbStudentType = new JComboBox<>(new String[]{"PROPER", "REPEAT"});

        addFormRow(studentPanel, "Registration No:", txtRegistrationNo, 0, studentGbc);
        addFormRow(studentPanel, "Registration Year:", txtRegistrationYear, 1, studentGbc);
        addFormRow(studentPanel, "Student Type:", cmbStudentType, 2, studentGbc);

        JPanel staffPanel = new JPanel(new GridBagLayout());
        staffPanel.setBackground(Color.WHITE);

        GridBagConstraints staffGbc = new GridBagConstraints();
        staffGbc.insets = new Insets(5, 10, 5, 10);
        staffGbc.fill = GridBagConstraints.HORIZONTAL;

        txtStaffCode = new JTextField(15);
        txtDesignation = new JTextField(15);

        addFormRow(staffPanel, "Staff Code:", txtStaffCode, 0, staffGbc);
        addFormRow(staffPanel, "Designation:", txtDesignation, 1, staffGbc);

        roleSpecificPanel.add(studentPanel, STUDENT_CARD);
        roleSpecificPanel.add(staffPanel, STAFF_CARD);
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

    private JPanel createProfilePictureSelector() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setOpaque(false);

        CustomButton browseButton = new CustomButton(
                "Browse",
                AppTheme.BTN_EDIT_BG,
                AppTheme.BTN_EDIT_FG,
                AppTheme.BTN_EDIT_HOVER,
                new Dimension(100, 34)
        );
        browseButton.addActionListener(e -> chooseProfilePicture());

        CustomButton clearButton = new CustomButton(
                "Clear",
                AppTheme.BTN_CANCEL_BG,
                AppTheme.BTN_CANCEL_FG,
                AppTheme.BTN_CANCEL_HOVER,
                new Dimension(90, 34)
        );
        clearButton.addActionListener(e -> clearProfilePicture());

        buttonPanel.add(browseButton);
        buttonPanel.add(clearButton);

        panel.add(txtProfilePicture, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);
        return panel;
    }

    private void chooseProfilePicture() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Profile Picture");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            txtProfilePicture.setText(selectedFile.getAbsolutePath());
            updateImagePreview(selectedFile.getAbsolutePath());
        }
    }

    private void clearProfilePicture() {
        txtProfilePicture.setText("");
        profilePhotoFrame.clearImage();
    }

    private void updateImagePreview(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            clearProfilePicture();
            return;
        }
        profilePhotoFrame.setImagePath(imagePath);
    }

    private void updateRoleSpecificFields() {
        if (isStudentRole()) {
            roleCardLayout.show(roleSpecificPanel, STUDENT_CARD);
        } else {
            roleCardLayout.show(roleSpecificPanel, STAFF_CARD);
        }

        roleSpecificPanel.revalidate();
        roleSpecificPanel.repaint();
    }

    public void resetForm() {
        cmbRole.setSelectedItem(AppConfig.ROLE_STUDENT);
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        clearProfilePicture();
        txtDob.setText("");
        if (cmbDepartment.getItemCount() > 0) {
            cmbDepartment.setSelectedIndex(0);
        }
        cmbStatus.setSelectedItem(AppConfig.STATUS_ACTIVE);

        txtRegistrationNo.setText("");
        txtRegistrationYear.setText("");
        cmbStudentType.setSelectedItem("PROPER");

        txtStaffCode.setText("");
        txtDesignation.setText("");

        updateRoleSpecificFields();
    }

    public AddUserRequest buildRequest() {
        return new AddUserRequest(
                getRole(),
                getFirstName(),
                getLastName(),
                getEmail(),
                getPassword(),
                getPhone(),
                getAddress(),
                getProfilePicturePath(),
                getDob(),
                getDepartmentId(),
                getStatus(),
                getRegistrationNo(),
                getRegistrationYear(),
                getStudentType(),
                getStaffCode(),
                getDesignation()
        );
    }

    public String getRole() {
        return cmbRole.getSelectedItem() == null ? "" : cmbRole.getSelectedItem().toString();
    }

    public String getFirstName() {
        return txtFirstName.getText().trim();
    }

    public String getLastName() {
        return txtLastName.getText().trim();
    }

    public String getEmail() {
        return txtEmail.getText().trim();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword()).trim();
    }

    public String getPhone() {
        return txtPhone.getText().trim();
    }

    public String getAddress() {
        return txtAddress.getText().trim();
    }

    public String getProfilePicturePath() {
        return txtProfilePicture.getText().trim();
    }

    public String getDob() {
        return txtDob.getText().trim();
    }

    public String getDepartmentId() {
        Department department = getSelectedDepartment();
        return department == null ? "" : String.valueOf(department.getDepartmentId());
    }

    public String getStatus() {
        return cmbStatus.getSelectedItem() == null ? "" : cmbStatus.getSelectedItem().toString();
    }

    public String getRegistrationNo() {
        return txtRegistrationNo.getText().trim();
    }

    public String getRegistrationYear() {
        return txtRegistrationYear.getText().trim();
    }

    public String getStudentType() {
        return cmbStudentType.getSelectedItem() == null ? "" : cmbStudentType.getSelectedItem().toString();
    }

    public String getStaffCode() {
        return txtStaffCode.getText().trim();
    }

    public String getDesignation() {
        return txtDesignation.getText().trim();
    }

    public boolean isStudentRole() {
        return AppConfig.ROLE_STUDENT.equals(getRole());
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

    public void setOnCloseAction(Runnable onCloseAction) {
        this.onCloseAction = onCloseAction;
    }

    public void setOnSaveAction(Runnable onSaveAction) {
        this.onSaveAction = onSaveAction;
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

    private Department getSelectedDepartment() {
        Object selectedItem = cmbDepartment.getSelectedItem();
        if (selectedItem instanceof Department) {
            return (Department) selectedItem;
        }
        return null;
    }
}
