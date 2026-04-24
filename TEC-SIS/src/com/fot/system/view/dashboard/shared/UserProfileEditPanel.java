package com.fot.system.view.dashboard.shared;

import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.view.components.CustomButton;
import com.fot.system.view.components.ProfilePhotoFrame;
import com.fot.system.view.components.ProfileSectionCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class UserProfileEditPanel extends JPanel {
    private static final int PROFILE_CARD_WIDTH = 350;

    private final ProfilePhotoFrame photoFrame;
    private final JTextField txtProfilePicture;
    private final JLabel lblEditHint;
    private final JLabel lblNameValue;
    private final JLabel lblEmailValue;
    private final JLabel lblRoleValue;
    private final JLabel lblDepartmentValue;
    private final JLabel lblRoleInfoValue;
    private final JTextField txtPhone;
    private final JTextArea txtAddress;
    private final JTextField txtDob;
    private final JPasswordField txtPassword;
    private final JPanel dobFieldPanel;
    private final JPanel passwordFieldPanel;
    private final ProfileSectionCard profileCard;

    private String currentProfilePicturePath = "";

    public UserProfileEditPanel(Runnable onCancel, Runnable onSave) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 0, 0, 18);

        profileCard = new ProfileSectionCard("Profile Card", "Update your profile photo and allowed personal details.");
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JPanel photoWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        photoWrap.setOpaque(false);
        photoFrame = new ProfilePhotoFrame("No Image");
        photoWrap.add(photoFrame);

        txtProfilePicture = new JTextField();
        txtProfilePicture.setEditable(false);
        styleTextField(txtProfilePicture);

        lblEditHint = new JLabel();
        lblEditHint.setFont(AppTheme.fontPlain(13));
        lblEditHint.setForeground(AppTheme.TEXT_SUBTLE);
        lblEditHint.setAlignmentX(Component.CENTER_ALIGNMENT);

        left.add(Box.createVerticalStrut(8));
        left.add(photoWrap);
        left.add(Box.createVerticalStrut(16));
        left.add(createLabeledEditField("Profile Picture", txtProfilePicture));
        left.add(Box.createVerticalStrut(10));
        left.add(createPhotoButtonRow());
        left.add(Box.createVerticalStrut(16));
        left.add(lblEditHint);
        left.add(Box.createVerticalStrut(4));

        profileCard.setContent(left);
        profileCard.setPreferredSize(new Dimension(PROFILE_CARD_WIDTH, 540));
        profileCard.setMinimumSize(new Dimension(PROFILE_CARD_WIDTH, 400));
        profileCard.setMaximumSize(new Dimension(PROFILE_CARD_WIDTH, Integer.MAX_VALUE));

        JPanel profileWrap = new JPanel(new BorderLayout());
        profileWrap.setOpaque(false);
        profileWrap.setPreferredSize(new Dimension(PROFILE_CARD_WIDTH, 0));
        profileWrap.add(profileCard, BorderLayout.NORTH);
        content.add(profileWrap, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        lblNameValue = createValueLabel();
        lblEmailValue = createValueLabel();
        lblRoleValue = createValueLabel();
        lblDepartmentValue = createValueLabel();
        lblRoleInfoValue = createValueLabel();

        txtPhone = new JTextField();
        styleTextField(txtPhone);

        txtAddress = new JTextArea(4, 20);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        styleTextArea(txtAddress);

        txtDob = new JTextField();
        styleTextField(txtDob);

        txtPassword = new JPasswordField();
        styleTextField(txtPassword);

        ProfileSectionCard accountCard = new ProfileSectionCard(
                "Account Details",
                "These core account details are managed by the system and shown as read-only."
        );
        accountCard.setContent(createFieldStack(new JComponent[]{
                createReadOnlyField("Full Name", lblNameValue),
                createReadOnlyField("Email Address", lblEmailValue),
                createReadOnlyField("Role", lblRoleValue),
                createReadOnlyField("Department", lblDepartmentValue),
                createReadOnlyField("Role Specific Record", lblRoleInfoValue)
        }));

        ProfileSectionCard updateCard = new ProfileSectionCard(
                "Allowed Updates",
                "Only the fields below can be changed from your profile panel."
        );
        dobFieldPanel = createEditableField("Date of Birth (yyyy-mm-dd)", txtDob);
        passwordFieldPanel = createEditableField("Password", txtPassword);
        updateCard.setContent(createFieldStack(new JComponent[]{
                createEditableField("Phone Number", txtPhone),
                createEditableField("Address", createTextAreaScrollPane()),
                dobFieldPanel,
                passwordFieldPanel
        }));

        right.add(accountCard);
        right.add(Box.createVerticalStrut(16));
        right.add(updateCard);
        right.add(Box.createVerticalStrut(18));
        right.add(createEditActions(onCancel, onSave));
        right.add(Box.createVerticalGlue());

        content.add(right, gbc);
        add(content, BorderLayout.CENTER);
    }

    public void bind(User user, String departmentName, String roleInfo, String accessHint, String dob, boolean canEditDob, boolean canEditPassword) {
        currentProfilePicturePath = user.getProfilePicturePath() == null ? "" : user.getProfilePicturePath();
        txtProfilePicture.setText(currentProfilePicturePath);
        photoFrame.setImagePath(currentProfilePicturePath);
        txtPhone.setText(valueOrEmpty(user.getPhone()));
        txtAddress.setText(valueOrEmpty(user.getAddress()));
        txtDob.setText(valueOrEmpty(dob));
        txtDob.setEditable(canEditDob);
        txtDob.setEnabled(canEditDob);
        dobFieldPanel.setVisible(canEditDob);
        txtPassword.setText(user.getPasswordHash());
        txtPassword.setEditable(canEditPassword);
        txtPassword.setEnabled(canEditPassword);
        passwordFieldPanel.setVisible(canEditPassword);

        lblNameValue.setText(user.getFullName());
        lblEmailValue.setText(valueOrDash(user.getEmail()));
        lblRoleValue.setText(valueOrDash(user.getRole()));
        lblDepartmentValue.setText(valueOrDash(departmentName));
        lblRoleInfoValue.setText(valueOrDash(roleInfo));
        lblEditHint.setText(accessHint);
        revalidate();
        repaint();
    }

    public void chooseProfilePicture(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Profile Picture");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));

        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            currentProfilePicturePath = selectedFile.getAbsolutePath();
            txtProfilePicture.setText(currentProfilePicturePath);
            photoFrame.setImagePath(currentProfilePicturePath);
        }
    }

    public void clearProfilePicture() {
        currentProfilePicturePath = "";
        txtProfilePicture.setText("");
        photoFrame.clearImage();
    }

    public String getProfilePicturePath() {
        return currentProfilePicturePath;
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

    public String getPassword() {
        return new String(txtPassword.getPassword()).trim();
    }

    private JPanel createPhotoButtonRow() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setOpaque(false);

        CustomButton browseButton = new CustomButton(
                "Browse",
                AppTheme.BTN_EDIT_BG,
                AppTheme.BTN_EDIT_FG,
                AppTheme.BTN_EDIT_HOVER,
                new Dimension(110, 38)
        );
        browseButton.addActionListener(e -> chooseProfilePicture(this));

        CustomButton clearButton = new CustomButton(
                "Clear",
                AppTheme.BTN_CANCEL_BG,
                AppTheme.BTN_CANCEL_FG,
                AppTheme.BTN_CANCEL_HOVER,
                new Dimension(95, 38)
        );
        clearButton.addActionListener(e -> clearProfilePicture());

        panel.add(browseButton);
        panel.add(clearButton);
        return panel;
    }

    private JPanel createEditActions(Runnable onCancel, Runnable onSave) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);

        CustomButton cancelButton = new CustomButton(
                "Cancel",
                AppTheme.BTN_CANCEL_BG,
                AppTheme.BTN_CANCEL_FG,
                AppTheme.BTN_CANCEL_HOVER,
                new Dimension(110, 40)
        );
        cancelButton.addActionListener(e -> onCancel.run());

        CustomButton saveButton = new CustomButton(
                "Save Changes",
                AppTheme.BTN_SAVE_BG,
                AppTheme.BTN_SAVE_FG,
                AppTheme.BTN_SAVE_HOVER,
                new Dimension(150, 40)
        );
        saveButton.addActionListener(e -> onSave.run());

        panel.add(cancelButton);
        panel.add(saveButton);
        return panel;
    }

    private JPanel createFieldStack(JComponent[] fields) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (int i = 0; i < fields.length; i++) {
            panel.add(fields[i]);
            if (i < fields.length - 1) {
                panel.add(Box.createVerticalStrut(12));
            }
        }
        return panel;
    }

    private JPanel createReadOnlyField(String labelText, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(AppTheme.fontPlain(13));
        label.setForeground(AppTheme.TEXT_DARK);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(AppTheme.SURFACE_MUTED);
        wrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(11, 12, 11, 12)
        ));
        wrap.add(valueLabel, BorderLayout.CENTER);

        panel.add(label, BorderLayout.NORTH);
        panel.add(wrap, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEditableField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(AppTheme.fontPlain(13));
        label.setForeground(AppTheme.TEXT_DARK);

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLabeledEditField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel label = new JLabel(labelText);
        label.setFont(AppTheme.fontPlain(13));
        label.setForeground(AppTheme.TEXT_DARK);

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane createTextAreaScrollPane() {
        JScrollPane scrollPane = new JScrollPane(txtAddress);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_MUTED),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        scrollPane.setPreferredSize(new Dimension(0, 90));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("-");
        label.setFont(AppTheme.fontBold(14));
        label.setForeground(AppTheme.TEXT_DARK);
        return label;
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(AppTheme.fontPlain(14));
        textField.setPreferredSize(new Dimension(0, 40));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_MUTED),
                new EmptyBorder(9, 12, 9, 12)
        ));
    }

    private void styleTextArea(JTextArea textArea) {
        textArea.setFont(AppTheme.fontPlain(14));
        textArea.setBorder(new EmptyBorder(9, 10, 9, 10));
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
