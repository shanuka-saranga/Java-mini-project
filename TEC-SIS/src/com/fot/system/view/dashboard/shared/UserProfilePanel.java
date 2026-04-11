package com.fot.system.view.dashboard.shared;

import com.fot.system.config.AppTheme;
import com.fot.system.controller.EditUserController;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.DepartmentService;
import com.fot.system.service.UserService;
import com.fot.system.view.components.CustomButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class UserProfilePanel extends JPanel {
    private static final String VIEW_CARD = "VIEW";
    private static final String EDIT_CARD = "EDIT";

    private final UserService userService = new UserService();
    private final DepartmentService departmentService = new DepartmentService();
    private final EditUserController editUserController = new EditUserController();
    private final User sessionUser;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final Map<Integer, Department> departmentMap = new HashMap<>();

    private User currentUser;
    private final UserProfileViewPanel viewPanel;
    private final UserProfileEditPanel editPanel;

    protected UserProfilePanel(User user) {
        this.sessionUser = user;

        setLayout(new BorderLayout());
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(createHeader(), BorderLayout.NORTH);

        viewPanel = new UserProfileViewPanel();
        editPanel = new UserProfileEditPanel(this::showViewCard, this::saveProfileChanges);

        contentPanel.setOpaque(false);
        contentPanel.add(createViewState(), VIEW_CARD);
        contentPanel.add(editPanel, EDIT_CARD);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppTheme.SURFACE_SOFT);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        loadDepartments();
        loadCurrentUserProfile();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 18, 0));

        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitle = new JLabel(getSubtitleText());
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(AppTheme.TEXT_SUBTLE);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createViewState() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);
        panel.add(viewPanel, BorderLayout.CENTER);
        panel.add(createViewActions(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createViewActions() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);

        CustomButton refreshButton = new CustomButton(
                "Refresh",
                AppTheme.BTN_EDIT_BG,
                AppTheme.BTN_EDIT_FG,
                AppTheme.BTN_EDIT_HOVER,
                new Dimension(110, 40)
        );
        refreshButton.addActionListener(e -> loadCurrentUserProfile());

        CustomButton editButton = new CustomButton(
                "Edit Profile",
                AppTheme.BTN_SAVE_BG,
                AppTheme.BTN_SAVE_FG,
                AppTheme.BTN_SAVE_HOVER,
                new Dimension(150, 40)
        );
        editButton.addActionListener(e -> showEditCard());

        panel.add(refreshButton);
        panel.add(editButton);
        return panel;
    }

    private void loadDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        departmentMap.clear();
        for (Department department : departments) {
            departmentMap.put(department.getDepartmentId(), department);
        }
    }

    private void loadCurrentUserProfile() {
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() {
                return userService.getUserById(sessionUser.getId());
            }

            @Override
            protected void done() {
                try {
                    currentUser = get();
                    if (currentUser == null) {
                        throw new RuntimeException("User profile could not be loaded.");
                    }
                    populatePanels();
                    cardLayout.show(contentPanel, VIEW_CARD);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            UserProfilePanel.this,
                            "Failed to load profile details.",
                            "Profile Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void populatePanels() {
        String departmentName = resolveDepartmentName(currentUser.getDepartmentId());
        RoleInfo roleInfo = buildRoleInfo(currentUser);
        String dob = formatDate(currentUser.getDob());

        viewPanel.bind(
                currentUser,
                departmentName,
                getViewAccessHint(),
                roleInfo.primaryValue,
                roleInfo.secondaryValue,
                dob,
                currentUser.getPhone(),
                currentUser.getAddress()
        );

        editPanel.bind(
                currentUser,
                departmentName,
                roleInfo.primaryValue + " | " + roleInfo.secondaryValue,
                getEditAccessHint(),
                dob,
                canEditDob(),
                canEditPassword()
        );
    }

    private void showEditCard() {
        populatePanels();
        cardLayout.show(contentPanel, EDIT_CARD);
    }

    private void showViewCard() {
        populatePanels();
        cardLayout.show(contentPanel, VIEW_CARD);
    }

    private void saveProfileChanges() {
        if (currentUser == null) {
            return;
        }

        try {
            EditUserRequest request = buildProfileUpdateRequest();
            currentUser = editUserController.updateUser(request);
            populatePanels();
            cardLayout.show(contentPanel, VIEW_CARD);
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Profile Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private EditUserRequest buildProfileUpdateRequest() {
        String dobValue = canEditDob() ? editPanel.getDob() : formatDate(currentUser.getDob());

        if (currentUser instanceof Student) {
            Student student = (Student) currentUser;
            return new EditUserRequest(
                    currentUser.getId(),
                    currentUser.getRole(),
                    currentUser.getFirstName(),
                    currentUser.getLastName(),
                    currentUser.getEmail(),
                    editPanel.getPassword(),
                    editPanel.getPhone(),
                    editPanel.getAddress(),
                    editPanel.getProfilePicturePath(),
                    dobValue,
                    String.valueOf(currentUser.getDepartmentId()),
                    currentUser.getStatus(),
                    student.getRegistrationNo(),
                    String.valueOf(student.getRegistrationYear()),
                    student.getStudentType(),
                    "",
                    ""
            );
        }

        Staff staff = (Staff) currentUser;
        return new EditUserRequest(
                currentUser.getId(),
                currentUser.getRole(),
                currentUser.getFirstName(),
                currentUser.getLastName(),
                currentUser.getEmail(),
                editPanel.getPassword(),
                editPanel.getPhone(),
                editPanel.getAddress(),
                editPanel.getProfilePicturePath(),
                dobValue,
                String.valueOf(currentUser.getDepartmentId()),
                currentUser.getStatus(),
                "",
                "",
                "",
                staff.getStaffCode(),
                staff.getDesignation()
        );
    }

    private RoleInfo buildRoleInfo(User user) {
        if (user instanceof Student) {
            Student student = (Student) user;
            return new RoleInfo(
                    "Registration No: " + valueOrDash(student.getRegistrationNo()),
                    "Student Type: " + valueOrDash(student.getStudentType()) + " | Registration Year: " + student.getRegistrationYear()
            );
        }

        if (user instanceof Staff) {
            Staff staff = (Staff) user;
            return new RoleInfo(
                    "Staff Code: " + valueOrDash(staff.getStaffCode()),
                    "Designation: " + valueOrDash(staff.getDesignation())
            );
        }

        return new RoleInfo("-", "-");
    }

    private String resolveDepartmentName(int departmentId) {
        Department department = departmentMap.get(departmentId);
        return department == null ? "-" : department.toString();
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private String formatDate(java.util.Date date) {
        return date == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    protected abstract boolean canEditDob();

    protected abstract boolean canEditPassword();

    protected abstract String getSubtitleText();

    protected abstract String getViewAccessHint();

    protected abstract String getEditAccessHint();

    protected User getCurrentUser() {
        return currentUser;
    }

    private static class RoleInfo {
        private final String primaryValue;
        private final String secondaryValue;

        private RoleInfo(String primaryValue, String secondaryValue) {
            this.primaryValue = primaryValue;
            this.secondaryValue = secondaryValue;
        }
    }
}
