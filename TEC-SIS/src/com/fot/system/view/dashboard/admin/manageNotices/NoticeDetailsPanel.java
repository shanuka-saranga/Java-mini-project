package com.fot.system.view.dashboard.admin.manageNotices;

import com.fot.system.config.AppTheme;
import com.fot.system.controller.EditNoticeController;
import com.fot.system.model.Notice;
import com.fot.system.view.components.CustomButton;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;

public class NoticeDetailsPanel extends JPanel {
    private static final String VIEW_CARD = "VIEW";
    private static final String EDIT_CARD = "EDIT";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel container = new JPanel(cardLayout);
    private final EditNoticeDetailsPanel editNoticeDetailsPanel = new EditNoticeDetailsPanel();
    private final EditNoticeController editNoticeController = new EditNoticeController();

    private JLabel lblTitle;
    private JLabel lblAudience;
    private JLabel lblPriority;
    private JLabel lblStatus;
    private JLabel lblPublishedDate;
    private JLabel lblExpiryDate;
    private JTextArea txtContent;
    private JLabel lblCreatedBy;

    private final Color tealColor = new Color(0, 121, 107);
    private Notice currentNotice;
    private Runnable onCloseAction;
    private Runnable onNoticeUpdatedAction;
    private Runnable onNoticeDeletedAction;

    public NoticeDetailsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(tealColor), " Notice Details "),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        setVisible(false);

        container.add(createViewPanel(), VIEW_CARD);
        container.add(editNoticeDetailsPanel, EDIT_CARD);

        add(container, BorderLayout.CENTER);
        add(createBottomActions(), BorderLayout.SOUTH);
    }

    private JPanel createViewPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setBackground(Color.WHITE);

        JPanel top = new JPanel(new GridBagLayout());
        top.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        lblTitle = createStyledLabel("Title: -", FontAwesomeSolid.BULLHORN);
        lblAudience = createStyledLabel("Audience: -", FontAwesomeSolid.USERS);
        lblPriority = createStyledLabel("Priority: -", FontAwesomeSolid.EXCLAMATION_CIRCLE);
        lblStatus = createStyledLabel("Status: -", FontAwesomeSolid.INFO_CIRCLE);
        lblPublishedDate = createStyledLabel("Published: -", FontAwesomeSolid.CALENDAR_ALT);
        lblExpiryDate = createStyledLabel("Expiry: -", FontAwesomeSolid.CLOCK);
        lblCreatedBy = createStyledLabel("Created By: -", FontAwesomeSolid.USER);

        addToGrid(top, lblTitle, 0, 0, gbc);
        addToGrid(top, lblAudience, 1, 0, gbc);
        addToGrid(top, lblPriority, 0, 1, gbc);
        addToGrid(top, lblStatus, 1, 1, gbc);
        addToGrid(top, lblPublishedDate, 0, 2, gbc);
        addToGrid(top, lblExpiryDate, 1, 2, gbc);
        gbc.gridwidth = 2;
        addToGrid(top, lblCreatedBy, 0, 3, gbc);

        txtContent = new JTextArea(6, 30);
        txtContent.setEditable(false);
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        txtContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtContent.setBackground(new Color(248, 251, 251));
        txtContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        wrapper.add(top, BorderLayout.NORTH);
        wrapper.add(new JScrollPane(txtContent), BorderLayout.CENTER);
        return wrapper;
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
                "Edit Notice",
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
            setVisible(false);
            if (onCloseAction != null) {
                onCloseAction.run();
            }
        });

        btnEdit.addActionListener(e -> {
            if (currentNotice == null) {
                return;
            }
            editNoticeDetailsPanel.setNoticeData(currentNotice);
            cardLayout.show(container, EDIT_CARD);
            btnEdit.setVisible(false);
            btnClose.setVisible(false);
            btnSave.setVisible(true);
            btnCancel.setVisible(true);
        });

        btnDelete.addActionListener(e -> deleteCurrentNotice());

        btnCancel.addActionListener(e -> {
            if (currentNotice != null) {
                editNoticeDetailsPanel.setNoticeData(currentNotice);
            }
            cardLayout.show(container, VIEW_CARD);
            btnSave.setVisible(false);
            btnCancel.setVisible(false);
            btnEdit.setVisible(true);
            btnClose.setVisible(true);
        });

        btnSave.addActionListener(e -> {
            if (saveUpdatedData()) {
                cardLayout.show(container, VIEW_CARD);
                btnSave.setVisible(false);
                btnCancel.setVisible(false);
                btnEdit.setVisible(true);
                btnClose.setVisible(true);
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
        if (currentNotice == null) {
            return false;
        }

        try {
            Notice updatedNotice = editNoticeController.updateNotice(editNoticeDetailsPanel.buildRequest());
            updateDetails(updatedNotice);
            if (onNoticeUpdatedAction != null) {
                onNoticeUpdatedAction.run();
            }
            JOptionPane.showMessageDialog(this, "Notice updated successfully!");
            return true;
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Edit Notice Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void deleteCurrentNotice() {
        if (currentNotice == null) {
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this notice?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            editNoticeController.deleteNotice(currentNotice.getId());
            JOptionPane.showMessageDialog(this, "Notice deleted successfully!");
            setVisible(false);

            if (onNoticeDeletedAction != null) {
                onNoticeDeletedAction.run();
            } else if (onCloseAction != null) {
                onCloseAction.run();
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete Notice Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addToGrid(JPanel panel, Component component, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 1.0;
        panel.add(component, gbc);
    }

    private JLabel createStyledLabel(String text, FontAwesomeSolid icon) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setIcon(FontIcon.of(icon, 16, tealColor));
        label.setIconTextGap(12);
        return label;
    }

    public void updateDetails(Notice notice) {
        this.currentNotice = notice;

        lblTitle.setText("Title: " + notice.getTitle());
        lblAudience.setText("Audience: " + notice.getAudience());
        lblPriority.setText("Priority: " + notice.getPriority());
        lblStatus.setText("Status: " + notice.getStatus());
        lblPublishedDate.setText("Published: " + notice.getPublishedDate());
        lblExpiryDate.setText("Expiry: " + (notice.getExpiryDate() == null ? "-" : notice.getExpiryDate()));
        lblCreatedBy.setText("Created By: " + notice.getCreatedByName());
        txtContent.setText(notice.getContent());
        editNoticeDetailsPanel.setNoticeData(notice);

        cardLayout.show(container, VIEW_CARD);
        setVisible(true);
    }

    public void setOnCloseAction(Runnable onCloseAction) {
        this.onCloseAction = onCloseAction;
    }

    public void setOnNoticeUpdatedAction(Runnable onNoticeUpdatedAction) {
        this.onNoticeUpdatedAction = onNoticeUpdatedAction;
    }

    public void setOnNoticeDeletedAction(Runnable onNoticeDeletedAction) {
        this.onNoticeDeletedAction = onNoticeDeletedAction;
    }
}
