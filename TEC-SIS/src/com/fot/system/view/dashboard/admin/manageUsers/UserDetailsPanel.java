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

/**
 * show selected user details and handle edit/delete actions
 * @author janith
 */
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

    /**
     * initialize user details panel cards and footer actions
     * @author janith
     */
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


    /**
     * create view mode details panel
     * @author janith
     */
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



    /**
     * create action button area for close/edit/delete/save flow
     * @author janith
     */
    private JPanel createBottomActions() {
        JPanel mainActionPanel = new JPanel(new BorderLayout());
        mainActionPanel.setOpaque(false);
        CustomButton btnClose = new CustomButton(
                "Close",
                AppTheme.BTN_CANCEL_BG,
                AppTheme.BTN_CANCEL_FG,
                AppTheme.BTN_CANCEL_HOVER,
                new Dimension(120, 40)
        );

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftActions.setOpaque(false);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightActions.setOpaque(false);

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
            container.revalidate();
            container.repaint();
            btnSave.setVisible(false);
            btnCancel.setVisible(false);
            btnEdit.setVisible(true);
            btnClose.setVisible(true);
            notifyEditModeChanged(false);
        });

        btnSave.addActionListener(e -> {
            if (saveUpdatedData()) {
                cardLayout.show(container, VIEW_CARD);
                container.revalidate();
                container.repaint();
                btnSave.setVisible(false);
                btnCancel.setVisible(false);
                btnEdit.setVisible(true);
                btnClose.setVisible(true);
                notifyEditModeChanged(false);
            }
        });

        leftActions.add(btnClose);

        rightActions.add(btnDelete);
        rightActions.add(btnEdit);
        rightActions.add(btnSave);
        rightActions.add(btnCancel);

        mainActionPanel.add(leftActions, BorderLayout.WEST);
        mainActionPanel.add(rightActions, BorderLayout.EAST);

        return mainActionPanel;
    }

    /**
     * validate and persist edited user values
     * @author janith
     */
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

    /**
     * delete current user record after confirmation
     * @author janith
     */
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

    /**
     * add detail field component to grid
     * @author janith
     */
    private void addToGrid(JPanel p, Component c, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y; gbc.weightx = 1.0;
        p.add(c, gbc);
    }

    /**
     * create styled label with icon
     * @author janith
     */
    private JLabel createStyledLabel(String text, FontAwesomeSolid icon) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.fontPlain(14));
        label.setIcon(FontIcon.of(icon, 16, AppTheme.ICON_ACCENT));
        label.setIconTextGap(12);
        return label;
    }

    /**
     * bind selected user values into details view
     * @param user selected user
     * @author janith
     */
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
        container.revalidate();
        container.repaint();
        revalidate();
        repaint();
    }

    /**
     * clear current details state and hide panel
     * @author janith
     */
    public void clearDetails() {
        currentUser = null;
        if (profilePhotoFrame != null) {
            profilePhotoFrame.clearImage();
        }
        setVisible(false);
    }

    /**
     * register close callback
     * @param onCloseAction callback function
     * @author janith
     */
    public void setOnCloseAction(Runnable onCloseAction) {
        this.onCloseAction = onCloseAction;
    }

    /**
     * register callback after update success
     * @param onUserUpdatedAction callback function
     * @author janith
     */
    public void setOnUserUpdatedAction(Runnable onUserUpdatedAction) {
        this.onUserUpdatedAction = onUserUpdatedAction;
    }

    /**
     * register callback after delete success
     * @param onUserDeletedAction callback function
     * @author janith
     */
    public void setOnUserDeletedAction(Runnable onUserDeletedAction) {
        this.onUserDeletedAction = onUserDeletedAction;
    }

    /**
     * register callback for edit mode state changes
     * @param onEditModeChangedAction callback function
     * @author janith
     */
    public void setOnEditModeChangedAction(Consumer<Boolean> onEditModeChangedAction) {
        this.onEditModeChangedAction = onEditModeChangedAction;
    }

    /**
     * pass departments to embedded edit form
     * @param departments department list
     * @author janith
     */
    public void setDepartments(List<Department> departments) {
        editUserDetailsPanel.setDepartments(departments);
    }

    /**
     * update profile image preview
     * @param imagePath image path value
     * @author janith
     */
    private void updateProfileImage(String imagePath) {
        profilePhotoFrame.setImagePath(imagePath);
    }

    /**
     * notify parent panel when edit mode toggles
     * @param editing edit mode state
     * @author janith
     */
    private void notifyEditModeChanged(boolean editing) {
        if (onEditModeChangedAction != null) {
            onEditModeChangedAction.accept(editing);
        }
    }
}
