package com.fot.system.view.dashboard.admin.manageNotices;

import com.fot.system.config.AppTheme;
import com.fot.system.controller.AddNoticeController;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.NoticeService;
import com.fot.system.view.components.CustomButton;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * manage notice dashboard section with table and detail forms
 * @author janith
 */
public class ManageNoticesPanel extends JPanel {
    private static final String DETAILS_CARD = "DETAILS";
    private static final String ADD_NOTICE_CARD = "ADD_NOTICE";
    private static final int EXPANDED_DIVIDER_SIZE = 5;
    private static final int COLLAPSED_DIVIDER_SIZE = 0;
    private static final double DETAILS_PANEL_RATIO = 0.60;

    private final NoticeService noticeService;
    private final NoticeTablePanel noticeTablePanel;
    private final NoticeDetailsPanel noticeDetailsPanel;
    private final AddNewNoticePanel addNewNoticePanel;
    private final AddNoticeController addNoticeController;
    private final CardLayout bottomCardLayout = new CardLayout();
    private final JPanel bottomContentPanel = new JPanel(bottomCardLayout);
    private JSplitPane splitPane;
    private boolean bottomExpanded;
    private boolean selectionLocked;
    private int lockedSelectionRow = -1;
    private boolean restoringSelection;
    private boolean initialLoadPending = true;

    /**
     * initialize manage notices panel
     * @param currentUser logged in user
     * @author janith
     */
    public ManageNoticesPanel(User currentUser) {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        add(createHeader(), BorderLayout.NORTH);

        noticeTablePanel = new NoticeTablePanel();
        noticeDetailsPanel = new NoticeDetailsPanel();
        noticeDetailsPanel.setOnCloseAction(this::collapseBottomPanel);
        noticeDetailsPanel.setOnNoticeUpdatedAction(this::loadDataFromDatabase);
        noticeDetailsPanel.setOnNoticeDeletedAction(this::afterNoticeDeleted);
        noticeDetailsPanel.setOnEditModeChangedAction(this::onEditModeChanged);

        addNewNoticePanel = new AddNewNoticePanel(currentUser.getId());
        addNewNoticePanel.setOnCloseAction(this::collapseBottomPanel);

        noticeService = new NoticeService();
        addNoticeController = new AddNoticeController(addNewNoticePanel, this::afterNoticeAdded);

        bottomContentPanel.add(noticeDetailsPanel, DETAILS_CARD);
        bottomContentPanel.add(addNewNoticePanel, ADD_NOTICE_CARD);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(noticeTablePanel);
        splitPane.setBottomComponent(bottomContentPanel);
        splitPane.setResizeWeight(DETAILS_PANEL_RATIO);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(COLLAPSED_DIVIDER_SIZE);
        splitPane.setBackground(Color.WHITE);
        splitPane.setBorder(null);

        noticeTablePanel.setMinimumSize(new Dimension(0, 140));
        bottomContentPanel.setMinimumSize(new Dimension(0, 0));
        add(splitPane, BorderLayout.CENTER);

        noticeTablePanel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || restoringSelection) {
                return;
            }
            if (initialLoadPending) {
                return;
            }
            if (selectionLocked) {
                maintainLockedSelection();
                return;
            }

            if (noticeTablePanel.getTable().getSelectedRow() != -1) {
                updateDetailsView();
            }
        });

        loadDataFromDatabase();
        SwingUtilities.invokeLater(this::collapseBottomPanel);
    }

    /**
     * create panel header with title and add button
     * @author janith
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Notice Management");
        title.setFont(AppTheme.fontBold(26));

        CustomButton addBtn = new CustomButton(
                "Add New Notice",
                AppTheme.BTN_SAVE_BG,
                AppTheme.BTN_SAVE_FG,
                AppTheme.BTN_SAVE_HOVER,
                new Dimension(180, 40)
        );
        addBtn.setIcon(FontIcon.of(FontAwesomeSolid.BULLHORN, 15, Color.WHITE));
        addBtn.addActionListener(e -> showAddNoticePanel());

        header.add(title, BorderLayout.WEST);
        header.add(addBtn, BorderLayout.EAST);
        return header;
    }

    /**
     * load notice table data from database
     * @author janith
     */
    private void loadDataFromDatabase() {
        final Integer selectedNoticeId = getSelectedNoticeId();
        SwingWorker<List<Notice>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Notice> doInBackground() {
                return noticeService.getAllNotices();
            }

            @Override
            protected void done() {
                try {
                    List<Notice> notices = get();
                    noticeTablePanel.getModel().setRowCount(0);

                    for (Notice notice : notices) {
                        Object[] rowData = {
                                notice.getId(),
                                notice.getTitle(),
                                notice.getAudience(),
                                notice.getPriority(),
                                notice.getStatus(),
                                notice.getPublishedDate(),
                                notice.getExpiryDate() == null ? "-" : notice.getExpiryDate()
                        };
                        noticeTablePanel.addRow(rowData);
                    }

                    if (initialLoadPending) {
                        JTable table = noticeTablePanel.getTable();
                        table.clearSelection();
                        selectionLocked = false;
                        lockedSelectionRow = -1;
                        noticeDetailsPanel.clearDetails();
                        collapseBottomPanel();
                        initialLoadPending = false;
                        return;
                    }

                    if (selectedNoticeId != null && restoreSelectionByNoticeId(selectedNoticeId)) {
                        return;
                    }

                    JTable table = noticeTablePanel.getTable();
                    if (table.getSelectedRow() == -1) {
                        noticeDetailsPanel.clearDetails();
                        collapseBottomPanel();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            ManageNoticesPanel.this,
                            "Error loading notices!",
                            "Notice Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    /**
     * show add new notice form
     * @author janith
     */
    private void showAddNoticePanel() {
        addNewNoticePanel.resetForm();
        bottomCardLayout.show(bottomContentPanel, ADD_NOTICE_CARD);
        addNewNoticePanel.setVisible(true);
        noticeDetailsPanel.setVisible(false);
        SwingUtilities.invokeLater(this::showBottomPanel);
    }

    /**
     * load selected row notice details view
     * @author janith
     */
    private void updateDetailsView() {
        JTable table = noticeTablePanel.getTable();
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            noticeDetailsPanel.clearDetails();
            collapseBottomPanel();
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        int noticeId = Integer.parseInt(noticeTablePanel.getModel().getValueAt(modelRow, 0).toString());

        SwingWorker<Notice, Void> worker = new SwingWorker<>() {
            @Override
            protected Notice doInBackground() {
                return noticeService.getNoticeById(noticeId);
            }

            @Override
            protected void done() {
                try {
                    Notice notice = get();
                    if (notice == null) {
                        noticeDetailsPanel.clearDetails();
                        collapseBottomPanel();
                        return;
                    }
                    bottomCardLayout.show(bottomContentPanel, DETAILS_CARD);
                    noticeDetailsPanel.updateDetails(notice);
                    SwingUtilities.invokeLater(ManageNoticesPanel.this::showBottomPanel);
                    noticeDetailsPanel.setVisible(true);
                } catch (Exception e) {
                    noticeDetailsPanel.clearDetails();
                    collapseBottomPanel();
                }
            }
        };
        worker.execute();
    }

    /**
     * expand bottom split region
     * @author janith
     */
    private void showBottomPanel() {
        if (bottomExpanded) {
            splitPane.setDividerSize(EXPANDED_DIVIDER_SIZE);
            splitPane.revalidate();
            splitPane.repaint();
            return;
        }

        bottomExpanded = true;
        splitPane.setDividerSize(EXPANDED_DIVIDER_SIZE);
        splitPane.setDividerLocation(DETAILS_PANEL_RATIO);
        splitPane.revalidate();
        splitPane.repaint();
    }

    /**
     * collapse bottom split region
     * @author janith
     */
    private void collapseBottomPanel() {
        bottomExpanded = false;
        splitPane.setDividerSize(COLLAPSED_DIVIDER_SIZE);
        splitPane.setDividerLocation(1.0);
        splitPane.revalidate();
        splitPane.repaint();
    }

    /**
     * refresh table after notice add
     * @author janith
     */
    private void afterNoticeAdded() {
        loadDataFromDatabase();
        collapseBottomPanel();
    }

    /**
     * refresh table after notice delete
     * @author janith
     */
    private void afterNoticeDeleted() {
        loadDataFromDatabase();
        collapseBottomPanel();
    }

    /**
     * lock current selection while editing
     * @param editing current edit mode state
     * @author janith
     */
    private void onEditModeChanged(boolean editing) {
        JTable table = noticeTablePanel.getTable();
        if (editing) {
            selectionLocked = true;
            lockedSelectionRow = table.getSelectedRow();
            return;
        }

        selectionLocked = false;
        lockedSelectionRow = table.getSelectedRow();
    }

    /**
     * keep selection on locked row while editing
     * @author janith
     */
    private void maintainLockedSelection() {
        JTable table = noticeTablePanel.getTable();
        int selectedRow = table.getSelectedRow();
        int rowCount = table.getRowCount();

        if (lockedSelectionRow == -1) {
            return;
        }
        if (lockedSelectionRow >= rowCount) {
            selectionLocked = false;
            lockedSelectionRow = -1;
            return;
        }
        if (selectedRow == lockedSelectionRow) {
            return;
        }

        restoringSelection = true;
        try {
            table.getSelectionModel().setSelectionInterval(lockedSelectionRow, lockedSelectionRow);
        } finally {
            restoringSelection = false;
        }
    }

    /**
     * resolve selected notice id from table selection
     * @author janith
     */
    private Integer getSelectedNoticeId() {
        JTable table = noticeTablePanel.getTable();
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        Object idValue = noticeTablePanel.getModel().getValueAt(modelRow, 0);
        if (idValue == null) {
            return null;
        }

        try {
            return Integer.parseInt(idValue.toString());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * restore selection after table refresh using notice id
     * @param noticeId notice id to reselect
     * @author janith
     */
    private boolean restoreSelectionByNoticeId(int noticeId) {
        JTable table = noticeTablePanel.getTable();

        for (int modelRow = 0; modelRow < noticeTablePanel.getModel().getRowCount(); modelRow++) {
            Object value = noticeTablePanel.getModel().getValueAt(modelRow, 0);
            if (value == null || !String.valueOf(noticeId).equals(value.toString())) {
                continue;
            }

            int viewRow = table.convertRowIndexToView(modelRow);
            if (viewRow < 0) {
                return false;
            }

            restoringSelection = true;
            try {
                table.getSelectionModel().setSelectionInterval(viewRow, viewRow);
            } finally {
                restoringSelection = false;
            }

            updateDetailsView();
            return true;
        }

        return false;
    }
}
