package com.fot.system.view.dashboard.admin.manageUsers;

import com.fot.system.config.AppConfig;
import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.EditUserRequest;
import com.fot.system.model.entity.Department;
import com.fot.system.model.entity.Staff;
import com.fot.system.model.entity.Student;
import com.fot.system.model.entity.User;
import com.fot.system.view.components.CustomButton;
import com.fot.system.view.components.ProfilePhotoFrame;
import com.fot.system.view.components.ThemedComboBox;
import com.fot.system.view.components.ThemedDatePicker;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class EditUserDetailsPanel extends JPanel {
    private static final String STUDENT_CARD = "STUDENT";
    private static final String STAFF_CARD = "STAFF";
    private static final Dimension INPUT_SIZE = new Dimension(0, 38);

    private int currentUserId = -1;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JTextField txtProfilePicture;
    private String currentProfilePicturePath;
    private ProfilePhotoFrame profilePhotoFrame;
    private ThemedDatePicker txtDob;
    private JComboBox<Department> cmbDepartment;
    private JComboBox<String> cmbRole;
    private JComboBox<String> cmbStatus;
    private final CardLayout roleCardLayout = new CardLayout();
    private final JPanel roleSpecificPanel = new JPanel(roleCardLayout);
    private JTextField txtRegistrationNo;
    private JTextField txtRegistrationYear;
    private JComboBox<String> cmbStudentType;
    private JTextField txtStaffCode;
    private JTextField txtDesignation;

    public EditUserDetailsPanel() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.SURFACE_SOFT);

        JScrollPane scrollPane = new JScrollPane(createFormPanel());
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppTheme.SURFACE_SOFT);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(AppTheme.SURFACE_SOFT);
        formPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

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
        profilePhotoFrame = new ProfilePhotoFrame("No image selected");
        txtDob = new ThemedDatePicker();
        cmbDepartment = new ThemedComboBox<>();

        cmbRole = new ThemedComboBox<>(new String[]{
                AppConfig.ROLE_ADMIN,
                AppConfig.ROLE_DEAN,
                AppConfig.ROLE_STUDENT,
                AppConfig.ROLE_LECTURER,
                AppConfig.ROLE_TO
        });
        cmbRole.setEnabled(false);
        cmbRole.addActionListener(e -> updateRoleSpecificFields());

        cmbStatus = new ThemedComboBox<>(new String[]{
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
        roleSpecificPanel.setBackground(AppTheme.SURFACE_SOFT);

        JPanel studentPanel = new JPanel(new GridBagLayout());
        studentPanel.setBackground(AppTheme.SURFACE_SOFT);
        GridBagConstraints studentGbc = new GridBagConstraints();
        studentGbc.insets = new Insets(5, 10, 5, 10);
        studentGbc.fill = GridBagConstraints.HORIZONTAL;

        txtRegistrationNo = new JTextField(15);
        txtRegistrationYear = new JTextField(15);
        cmbStudentType = new ThemedComboBox<>(new String[]{"PROPER", "REPEAT", "BATCH_MISSED"});

        addFormRow(studentPanel, "Registration No:", txtRegistrationNo, 0, studentGbc);
        addFormRow(studentPanel, "Registration Year:", txtRegistrationYear, 1, studentGbc);
        addFormRow(studentPanel, "Student Type:", cmbStudentType, 2, studentGbc);

        JPanel staffPanel = new JPanel(new GridBagLayout());
        staffPanel.setBackground(AppTheme.SURFACE_SOFT);
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
            currentProfilePicturePath = selectedFile.getAbsolutePath();
            txtProfilePicture.setText(currentProfilePicturePath);
            updateImagePreview(currentProfilePicturePath);
        }
    }

    private void clearProfilePicture() {
        currentProfilePicturePath = "";
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

    public void setDepartments(List<Department> departments) {
        DefaultComboBoxModel<Department> model = new DefaultComboBoxModel<>();
        for (Department department : departments) {
            model.addElement(department);
        }
        cmbDepartment.setModel(model);
    }

    public void setUserData(User user) {
        currentUserId = user.getId();
        cmbRole.setSelectedItem(user.getRole());
        txtFirstName.setText(user.getFirstName());
        txtLastName.setText(user.getLastName());
        txtEmail.setText(user.getEmail());
        txtPassword.setText(user.getPasswordHash());
        txtPhone.setText(user.getPhone());
        txtAddress.setText(user.getAddress());
        currentProfilePicturePath = user.getProfilePicturePath();
        txtProfilePicture.setText(currentProfilePicturePath == null ? "" : currentProfilePicturePath);
        updateImagePreview(currentProfilePicturePath);
        txtDob.setText(user.getDob() == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(user.getDob()));
        cmbStatus.setSelectedItem(user.getStatus());
        selectDepartment(user.getDepartmentId());

        if (user instanceof Student) {
            Student student = (Student) user;
            txtRegistrationNo.setText(student.getRegistrationNo());
            txtRegistrationYear.setText(String.valueOf(student.getRegistrationYear()));
            cmbStudentType.setSelectedItem(student.getStudentType());
            txtStaffCode.setText("");
            txtDesignation.setText("");
        } else if (user instanceof Staff) {
            Staff staff = (Staff) user;
            txtStaffCode.setText(staff.getStaffCode());
            txtDesignation.setText(staff.getDesignation());
            txtRegistrationNo.setText("");
            txtRegistrationYear.setText("");
            cmbStudentType.setSelectedItem("PROPER");
        }

        updateRoleSpecificFields();
    }

    public EditUserRequest buildRequest() {
        return new EditUserRequest(
                currentUserId,
                getRole(),
                getFirstName(),
                getLastName(),
                getEmail(),
                getPassword(),
                getPhone(),
                getAddress(),
                currentProfilePicturePath,
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

    private void updateRoleSpecificFields() {
        if (isStudentRole()) {
            roleCardLayout.show(roleSpecificPanel, STUDENT_CARD);
        } else {
            roleCardLayout.show(roleSpecificPanel, STAFF_CARD);
        }
    }

    private void addFormRow(JPanel panel, String label, Component component, int row, GridBagConstraints gbc) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.24;
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(AppTheme.fontPlain(13));
        fieldLabel.setForeground(AppTheme.TEXT_DARK);
        panel.add(fieldLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.76;
        styleFormComponent(component);
        panel.add(component, gbc);
    }

    private Department getSelectedDepartment() {
        Object selectedItem = cmbDepartment.getSelectedItem();
        if (selectedItem instanceof Department) {
            return (Department) selectedItem;
        }
        return null;
    }

    private void selectDepartment(int departmentId) {
        ComboBoxModel<Department> model = cmbDepartment.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Department department = model.getElementAt(i);
            if (department.getDepartmentId() == departmentId) {
                cmbDepartment.setSelectedItem(department);
                return;
            }
        }
    }

    private void styleFormComponent(Component component) {
        if (component instanceof JTextField) {
            JTextField field = (JTextField) component;
            field.setFont(AppTheme.fontPlain(14));
            field.setPreferredSize(INPUT_SIZE);
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppTheme.BORDER_MUTED, 1, false),
                    BorderFactory.createEmptyBorder(7, 10, 7, 10)
            ));
        } else if (component instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) component;
            combo.setFont(AppTheme.fontPlain(14));
            combo.setPreferredSize(INPUT_SIZE);
            combo.setMinimumSize(INPUT_SIZE);
        } else if (component instanceof JPanel) {
            component.setPreferredSize(new Dimension(0, 40));
        }
    }
}
