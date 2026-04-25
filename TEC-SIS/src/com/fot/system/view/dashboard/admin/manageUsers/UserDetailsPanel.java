package com.fot.system.view.dashboard.admin.manageUsers;

import com.fot.system.config.AppTheme;
import com.fot.system.controller.EditUserController;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.view.components.CustomButton;
import com.fot.system.view.components.ProfilePhotoFrame;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class UserDetailsPanel extends JPanel {
    private static final String VIEW_CARD = "VIEW";
    private static final String EDIT_CARD = "EDIT";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel container = new JPanel(cardLayout);
    private final EditUserDetailsPanel editUserDetailsPanel = new EditUserDetailsPanel();
    private final EditUserController editUserController = new EditUserController();

    private JLabel lblFirstName, lblLastName, lblEmail, lblRole, lblStatus, lblPhone, lblAddress, lblExtra;
    private ProfilePhotoFrame profilePhotoFrame;

    private User currentUser;
    private Runnable onCloseAction;
    private Runnable onUserUpdatedAction;
    private Runnable onUserDeletedAction;
    private Consumer<Boolean> onEditModeChangedAction;

    public UserDetailsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        javax.swing.border.TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT),
                " User Profile Details "
        );
        titledBorder.setTitleColor(AppTheme.TEXT_DARK);
        setBorder(BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        setVisible(false);

        container.add(createViewPanel(), VIEW_CARD);
        container.add(editUserDetailsPanel, EDIT_CARD);

        add(container, BorderLayout.CENTER);
        add(createBottomActions(), BorderLayout.SOUTH);
    }


    private JPanel createViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(Color.WHITE);

        profilePhotoFrame = new ProfilePhotoFrame("No Image");
        JPanel photoWrap = new JPanel(new GridBagLayout());
        photoWrap.setOpaque(false);
        photoWrap.add(profilePhotoFrame);

        JPanel detailsGrid = new JPanel(new GridBagLayout());
        detailsGrid.setBackground(Color.WHITE);
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

        addToGrid(detailsGrid, lblFirstName, 0, 0, gbc);
        addToGrid(detailsGrid, lblLastName, 1, 0, gbc);
        addToGrid(detailsGrid, lblEmail, 0, 1, gbc);
        addToGrid(detailsGrid, lblPhone, 1, 1, gbc);
        addToGrid(detailsGrid, lblRole, 0, 2, gbc);
        addToGrid(detailsGrid, lblStatus, 1, 2, gbc);

        gbc.gridwidth = 2;
        addToGrid(detailsGrid, lblAddress, 0, 3, gbc);
        addToGrid(detailsGrid, lblExtra, 0, 4, gbc);

        panel.add(photoWrap, BorderLayout.WEST);
        panel.add(detailsGrid, BorderLayout.CENTER);
        return panel;
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

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        CustomButton btnEdit = new CustomButton(
                "Edit Profile",
                AppTheme.BTN_EDIT_BG,
                AppTheme.BTN_EDIT_FG,
                AppTheme.BTN_EDIT_HOVER,
                new Dimension(150, 40)
        );

        CustomButton btnDelete = new CustomButton(
                "Delete",
                AppTheme.BTN_DELETE_BG,
                AppTheme.BTN_DELETE_FG,
                AppTheme.BTN_DELETE_HOVER,
                new Dimension(120, 40)
        );

        CustomButton btnSave = new CustomButton(
                "Save Changes",
                AppTheme.BTN_SAVE_BG,
                AppTheme.BTN_SAVE_FG,
                AppTheme.BTN_SAVE_HOVER,
                new Dimension(150, 40)
        );

        CustomButton btnCancel = new CustomButton(
                "Cancel",
                AppTheme.BTN_CANCEL_BG,
                AppTheme.BTN_CANCEL_FG,
                AppTheme.BTN_CANCEL_HOVER,
                new Dimension(120, 40)
        );

        btnSave.setVisible(false);
        btnCancel.setVisible(false);


        btnClose.addActionListener(e -> {
            clearDetails();
            notifyEditModeChanged(false);
            if (onCloseAction != null) {
                onCloseAction.run();
            }
        });

        btnEdit.addActionListener(e -> {
            if (currentUser == null) {
                return;
            }

            editUserDetailsPanel.setUserData(currentUser);
            cardLayout.show(container, EDIT_CARD);
            btnEdit.setVisible(false);
            btnClose.setVisible(false);
            btnSave.setVisible(true);
            btnCancel.setVisible(true);
            notifyEditModeChanged(true);
        });

        btnDelete.addActionListener(e -> deleteCurrentUser());

        btnCancel.addActionListener(e -> {
            if (currentUser != null) {
                editUserDetailsPanel.setUserData(currentUser);
            }
            cardLayout.show(container, VIEW_CARD);
            btnSave.setVisible(false);
            btnCancel.setVisible(false);
            btnEdit.setVisible(true);
            btnClose.setVisible(true);
            notifyEditModeChanged(false);
        });

        btnSave.addActionListener(e -> {
            if (saveUpdatedData()) {
                cardLayout.show(container, VIEW_CARD);
                btnSave.setVisible(false);
                btnCancel.setVisible(false);
                btnEdit.setVisible(true);
                btnClose.setVisible(true);
                notifyEditModeChanged(false);
            }
        });

        leftPanel.add(btnClose);

        rightPanel.add(btnDelete);
        rightPanel.add(btnEdit);
        rightPanel.add(btnSave);
        rightPanel.add(btnCancel);

        mainActionPanel.add(leftPanel, BorderLayout.WEST);
        mainActionPanel.add(rightPanel, BorderLayout.EAST);

        return mainActionPanel;
    }

    private boolean saveUpdatedData() {
        if (currentUser == null) {
            return false;
        }

        try {
            User updatedUser = editUserController.updateUser(editUserDetailsPanel.buildRequest());
            updateDetails(updatedUser);
            if (onUserUpdatedAction != null) {
                onUserUpdatedAction.run();
            }
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            return true;
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Edit User Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void deleteCurrentUser() {
        if (currentUser == null) {
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete " + currentUser.getFullName() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            editUserController.deleteUser(currentUser.getId());
            JOptionPane.showMessageDialog(this, "User deleted successfully!");
            setVisible(false);

            if (onUserDeletedAction != null) {
                onUserDeletedAction.run();
            } else if (onCloseAction != null) {
                onCloseAction.run();
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete User Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addToGrid(JPanel p, Component c, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y; gbc.weightx = 1.0;
        p.add(c, gbc);
    }

    private JLabel createStyledLabel(String text, FontAwesomeSolid icon) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontPlain(14));
        label.setIcon(FontIcon.of(icon, 16, AppTheme.ICON_ACCENT));
        label.setIconTextGap(12);
        return label;
    }

    public void updateDetails(User user) {
        if (user == null) {
            clearDetails();
            return;
        }
        this.currentUser = user;

        lblFirstName.setText("First Name: " + user.getFirstName());
        lblLastName.setText("Last Name: " + user.getLastName());
        lblEmail.setText("Email: " + user.getEmail());
        lblPhone.setText("Phone: " + user.getPhone());
        lblRole.setText("Role: " + user.getRole());
        lblStatus.setText("Status: " + user.getStatus());
        lblAddress.setText("Address: " + user.getAddress());
        updateProfileImage(user.getProfilePicturePath());
        editUserDetailsPanel.setUserData(user);

        if (user instanceof Student) {
            String regNo = ((Student) user).getRegistrationNo();
            lblExtra.setText("Registration No: " + regNo);
            lblExtra.setVisible(true);
        } else if (user instanceof Staff) {
            String staffCode = ((Staff) user).getStaffCode();
            lblExtra.setText("Staff Code: " + staffCode);
            lblExtra.setVisible(true);
        } else {
            lblExtra.setVisible(false);
        }

        cardLayout.show(container, VIEW_CARD);
        setVisible(true);
    }

    public void clearDetails() {
        currentUser = null;
        if (profilePhotoFrame != null) {
            profilePhotoFrame.clearImage();
        }
        setVisible(false);
    }

    public void setOnCloseAction(Runnable onCloseAction) {
        this.onCloseAction = onCloseAction;
    }

    public void setOnUserUpdatedAction(Runnable onUserUpdatedAction) {
        this.onUserUpdatedAction = onUserUpdatedAction;
    }

    public void setOnUserDeletedAction(Runnable onUserDeletedAction) {
        this.onUserDeletedAction = onUserDeletedAction;
    }

    public void setOnEditModeChangedAction(Consumer<Boolean> onEditModeChangedAction) {
        this.onEditModeChangedAction = onEditModeChangedAction;
    }

    public void setDepartments(List<Department> departments) {
        editUserDetailsPanel.setDepartments(departments);
    }

    private void updateProfileImage(String imagePath) {
        profilePhotoFrame.setImagePath(imagePath);
    }

    private void notifyEditModeChanged(boolean editing) {
        if (onEditModeChangedAction != null) {
            onEditModeChangedAction.accept(editing);
        }
    }
}
