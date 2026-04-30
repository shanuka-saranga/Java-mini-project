package com.fot.system.view.dashboard.admin.manageNotices;

import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.view.shared_components.CustomButton;
import com.fot.system.view.shared_components.ThemedComboBox;
import com.fot.system.view.shared_components.ThemedDatePicker;
import com.fot.system.view.shared_components.ThemedTextField;

import javax.swing.*;
import java.awt.*;

/**
 * render add-notice form used in admin notice management
 * @author janith
 */
public class AddNewNoticePanel extends JPanel {

    private final int currentUserId;
    private ThemedTextField txtTitle;
    private JTextArea txtContent;
    private ThemedComboBox<String> cmbAudience;
    private ThemedComboBox<String> cmbPriority;
    private ThemedComboBox<String> cmbStatus;
    private ThemedDatePicker txtPublishedDate;
    private ThemedDatePicker txtExpiryDate;
    private Runnable onCloseAction;
    private Runnable onSaveAction;

    /**
     * initialize add notice panel and default form values
     * @param currentUserId logged-in user id
     * @author janith
     */
    public AddNewNoticePanel(int currentUserId) {
        this.currentUserId = currentUserId;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(AppTheme.PRIMARY),
                        " Add New Notice "
                ),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JScrollPane scrollPane = new JScrollPane(createFormPanel());
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
        add(createBottomActions(), BorderLayout.SOUTH);
        resetForm();
    }

    /**
     * create notice input form content
     * @author janith
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtTitle = new ThemedTextField(15);
        txtContent = new JTextArea(6, 15);
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        cmbAudience = new ThemedComboBox<>(new String[]{"ALL", "STUDENT", "LECTURER", "TO"});
        cmbPriority = new ThemedComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
        cmbStatus = new ThemedComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        txtPublishedDate = new ThemedDatePicker();
        txtExpiryDate = new ThemedDatePicker();

        addFormRow(formPanel, "Title:", txtTitle, 0, gbc);
        addFormRow(formPanel, "Content:", new JScrollPane(txtContent), 1, gbc);
        addFormRow(formPanel, "Audience:", cmbAudience, 2, gbc);
        addFormRow(formPanel, "Priority:", cmbPriority, 3, gbc);
        addFormRow(formPanel, "Status:", cmbStatus, 4, gbc);
        addFormRow(formPanel, "Published Date:", txtPublishedDate, 5, gbc);
        addFormRow(formPanel, "Expiry Date:", txtExpiryDate, 6, gbc);

        return formPanel;
    }

    /**
     * create footer actions for close and save
     * @author janith
     */
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

    /**
     * reset form fields to defaults
     * @author janith
     */
    public void resetForm() {
        txtTitle.setText("");
        txtContent.setText("");
        cmbAudience.setSelectedItem("ALL");
        cmbPriority.setSelectedItem("MEDIUM");
        cmbStatus.setSelectedItem("ACTIVE");
        txtPublishedDate.setText(java.time.LocalDate.now().toString());
        txtExpiryDate.setText("");
    }

    /**
     * build add notice request from form values
     * @author janith
     */
    public AddNoticeRequest buildRequest() {
        return new AddNoticeRequest(
                txtTitle.getText().trim(),
                txtContent.getText().trim(),
                selected(cmbAudience),
                selected(cmbPriority),
                selected(cmbStatus),
                txtPublishedDate.getText().trim(),
                txtExpiryDate.getText().trim(),
                currentUserId
        );
    }

    /**
     * register close callback
     * @param onCloseAction callback
     * @author janith
     */
    public void setOnCloseAction(Runnable onCloseAction) {
        this.onCloseAction = onCloseAction;
    }

    /**
     * register save callback
     * @param onSaveAction callback
     * @author janith
     */
    public void setOnSaveAction(Runnable onSaveAction) {
        this.onSaveAction = onSaveAction;
    }

    /**
     * add labeled component row to form
     * @author janith
     */
    private void addFormRow(JPanel panel, String label, Component component, int row, GridBagConstraints gbc) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        gbc.weighty = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        gbc.weighty = row == 1 ? 1.0 : 0;
        panel.add(component, gbc);
    }

    /**
     * resolve selected combo box value safely
     * @author janith
     */
    private String selected(JComboBox<String> comboBox) {
        return comboBox.getSelectedItem() == null ? "" : comboBox.getSelectedItem().toString();
    }
}
