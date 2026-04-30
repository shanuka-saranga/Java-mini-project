package com.fot.system.view.dashboard.admin.manageNotices;

import com.fot.system.config.AppTheme;
import com.fot.system.controller.EditNoticeController;
import com.fot.system.model.entity.*;
import com.fot.system.view.shared_components.CustomButton;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * show selected notice details with edit/delete actions
 * @author janith
 */
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

    private Notice currentNotice;
    private Runnable onCloseAction;
    private Runnable onNoticeUpdatedAction;
    private Runnable onNoticeDeletedAction;
    private Consumer<Boolean> onEditModeChangedAction;

    /**
     * initialize notice details panel with view/edit cards
     * @author janith
     */
    public NoticeDetailsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(AppTheme.PRIMARY), " Notice Details "),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        setVisible(false);

        container.add(createViewPanel(), VIEW_CARD);
        container.add(editNoticeDetailsPanel, EDIT_CARD);

        add(container, BorderLayout.CENTER);
        add(createBottomActions(), BorderLayout.SOUTH);
    }

    /**
     * create read-only notice details card
     * @author janith
     */
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
        txtContent.setFont(AppTheme.fontPlain(14));
        txtContent.setBackground(AppTheme.SURFACE_MUTED);
        txtContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        wrapper.add(top, BorderLayout.NORTH);
        wrapper.add(new JScrollPane(txtContent), BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * create footer actions for close/edit/delete/save
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
            notifyEditModeChanged(false);
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
            notifyEditModeChanged(true);
        });

        btnDelete.addActionListener(e -> deleteCurrentNotice());

        btnCancel.addActionListener(e -> {
            if (currentNotice != null) {
                editNoticeDetailsPanel.setNoticeData(currentNotice);
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

        leftPanel.add(btnClose);
        rightPanel.add(btnDelete);
        rightPanel.add(btnEdit);
        rightPanel.add(btnSave);
        rightPanel.add(btnCancel);
        mainActionPanel.add(leftPanel, BorderLayout.WEST);
        mainActionPanel.add(rightPanel, BorderLayout.EAST);
        return mainActionPanel;
    }

    /**
     * validate and persist notice update
     * @author janith
     */
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
            JOptionPane.showMessageDialog(this, "Failed to update notice.", "Edit Notice Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * delete current notice after confirmation
     * @author janith
     */
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
            JOptionPane.showMessageDialog(this, "Failed to delete notice.", "Delete Notice Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * add detail component to grid layout
     * @author janith
     */
    private void addToGrid(JPanel panel, Component component, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 1.0;
        panel.add(component, gbc);
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
     * bind notice data to view card
     * @param notice selected notice
     * @author janith
     */
    public void updateDetails(Notice notice) {
        if (notice == null) {
            clearDetails();
            return;
        }

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
        currentNotice = null;
        txtContent.setText("");
        setVisible(false);
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
     * register callback after notice update
     * @param onNoticeUpdatedAction callback
     * @author janith
     */
    public void setOnNoticeUpdatedAction(Runnable onNoticeUpdatedAction) {
        this.onNoticeUpdatedAction = onNoticeUpdatedAction;
    }

    /**
     * register callback after notice delete
     * @param onNoticeDeletedAction callback
     * @author janith
     */
    public void setOnNoticeDeletedAction(Runnable onNoticeDeletedAction) {
        this.onNoticeDeletedAction = onNoticeDeletedAction;
    }

    /**
     * register callback for edit mode changes
     * @param onEditModeChangedAction callback
     * @author janith
     */
    public void setOnEditModeChangedAction(Consumer<Boolean> onEditModeChangedAction) {
        this.onEditModeChangedAction = onEditModeChangedAction;
    }

    /**
     * notify parent panel when edit mode state changes
     * @param editing current edit mode state
     * @author janith
     */
    private void notifyEditModeChanged(boolean editing) {
        if (onEditModeChangedAction != null) {
            onEditModeChangedAction.accept(editing);
        }
    }
}
