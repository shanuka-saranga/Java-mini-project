package com.fot.system.view.dashboard.admin.manageNotices;

import com.fot.system.model.EditNoticeRequest;
import com.fot.system.model.Notice;

import javax.swing.*;
import java.awt.*;

public class EditNoticeDetailsPanel extends JPanel {

    private JTextField txtTitle;
    private JTextArea txtContent;
    private JComboBox<String> cmbAudience;
    private JComboBox<String> cmbPriority;
    private JComboBox<String> cmbStatus;
    private JTextField txtPublishedDate;
    private JTextField txtExpiryDate;
    private int noticeId;
    private int createdBy;

    public EditNoticeDetailsPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtTitle = new JTextField(15);
        txtContent = new JTextArea(6, 15);
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        cmbAudience = new JComboBox<>(new String[]{"ALL", "STUDENT", "LECTURER", "TO"});
        cmbPriority = new JComboBox<>(new String[]{"LOW", "MEDIUM", "HIGH"});
        cmbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        txtPublishedDate = new JTextField(15);
        txtExpiryDate = new JTextField(15);

        addFormRow("Title:", txtTitle, 0, gbc);
        addFormRow("Content:", new JScrollPane(txtContent), 1, gbc);
        addFormRow("Audience:", cmbAudience, 2, gbc);
        addFormRow("Priority:", cmbPriority, 3, gbc);
        addFormRow("Status:", cmbStatus, 4, gbc);
        addFormRow("Published Date:", txtPublishedDate, 5, gbc);
        addFormRow("Expiry Date:", txtExpiryDate, 6, gbc);
    }

    public void setNoticeData(Notice notice) {
        this.noticeId = notice.getId();
        this.createdBy = notice.getCreatedBy();
        txtTitle.setText(notice.getTitle());
        txtContent.setText(notice.getContent());
        cmbAudience.setSelectedItem(notice.getAudience());
        cmbPriority.setSelectedItem(notice.getPriority());
        cmbStatus.setSelectedItem(notice.getStatus());
        txtPublishedDate.setText(notice.getPublishedDate() == null ? "" : new java.sql.Date(notice.getPublishedDate().getTime()).toString());
        txtExpiryDate.setText(notice.getExpiryDate() == null ? "" : new java.sql.Date(notice.getExpiryDate().getTime()).toString());
    }

    public EditNoticeRequest buildRequest() {
        return new EditNoticeRequest(
                noticeId,
                txtTitle.getText().trim(),
                txtContent.getText().trim(),
                selected(cmbAudience),
                selected(cmbPriority),
                selected(cmbStatus),
                txtPublishedDate.getText().trim(),
                txtExpiryDate.getText().trim(),
                createdBy
        );
    }

    private void addFormRow(String label, Component component, int row, GridBagConstraints gbc) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        gbc.weighty = 0;
        add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        gbc.weighty = row == 1 ? 1.0 : 0;
        add(component, gbc);
    }

    private String selected(JComboBox<String> comboBox) {
        return comboBox.getSelectedItem() == null ? "" : comboBox.getSelectedItem().toString();
    }
}
