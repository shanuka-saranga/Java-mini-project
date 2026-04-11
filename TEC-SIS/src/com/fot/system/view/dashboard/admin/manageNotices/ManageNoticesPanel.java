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

public class ManageNoticesPanel extends JPanel {
    private static final String DETAILS_CARD = "DETAILS";
    private static final String ADD_NOTICE_CARD = "ADD_NOTICE";
    private static final int EXPANDED_DIVIDER_SIZE = 5;
    private static final int COLLAPSED_DIVIDER_SIZE = 0;
    private static final int DETAILS_PANEL_HEIGHT = 350;

    private final NoticeService noticeService;
    private final NoticeTablePanel noticeTablePanel;
    private final NoticeDetailsPanel noticeDetailsPanel;
    private final AddNewNoticePanel addNewNoticePanel;
    private final AddNoticeController addNoticeController;
    private final CardLayout bottomCardLayout = new CardLayout();
    private final JPanel bottomContentPanel = new JPanel(bottomCardLayout);
    private JSplitPane splitPane;

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

        addNewNoticePanel = new AddNewNoticePanel(currentUser.getId());
        addNewNoticePanel.setOnCloseAction(this::collapseBottomPanel);

        noticeService = new NoticeService();
        addNoticeController = new AddNoticeController(addNewNoticePanel, this::afterNoticeAdded);

        bottomContentPanel.add(noticeDetailsPanel, DETAILS_CARD);
        bottomContentPanel.add(addNewNoticePanel, ADD_NOTICE_CARD);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(noticeTablePanel);
        splitPane.setBottomComponent(bottomContentPanel);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerSize(COLLAPSED_DIVIDER_SIZE);
        splitPane.setBackground(Color.WHITE);
        splitPane.setBorder(null);

        bottomContentPanel.setMinimumSize(new Dimension(0, 0));
        add(splitPane, BorderLayout.CENTER);

        noticeTablePanel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailsView();
            }
        });

        loadDataFromDatabase();
        SwingUtilities.invokeLater(this::collapseBottomPanel);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Notice Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));

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

    private void loadDataFromDatabase() {
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
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading notices!");
                }
            }
        };
        worker.execute();
    }

    private void showAddNoticePanel() {
        addNewNoticePanel.resetForm();
        bottomCardLayout.show(bottomContentPanel, ADD_NOTICE_CARD);
        addNewNoticePanel.setVisible(true);
        noticeDetailsPanel.setVisible(false);
        SwingUtilities.invokeLater(this::showBottomPanel);
    }

    private void updateDetailsView() {
        int row = noticeTablePanel.getTable().getSelectedRow();
        if (row == -1) {
            return;
        }

        int noticeId = Integer.parseInt(noticeTablePanel.getModel().getValueAt(row, 0).toString());

        SwingWorker<Notice, Void> worker = new SwingWorker<>() {
            @Override
            protected Notice doInBackground() {
                return noticeService.getNoticeById(noticeId);
            }

            @Override
            protected void done() {
                try {
                    Notice notice = get();
                    bottomCardLayout.show(bottomContentPanel, DETAILS_CARD);
                    noticeDetailsPanel.updateDetails(notice);
                    SwingUtilities.invokeLater(ManageNoticesPanel.this::showBottomPanel);
                    noticeDetailsPanel.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void showBottomPanel() {
        splitPane.setDividerSize(EXPANDED_DIVIDER_SIZE);
        bottomContentPanel.setPreferredSize(new Dimension(0, DETAILS_PANEL_HEIGHT));
        bottomContentPanel.revalidate();

        int availableHeight = splitPane.getHeight();
        if (availableHeight > DETAILS_PANEL_HEIGHT) {
            splitPane.setDividerLocation(availableHeight - DETAILS_PANEL_HEIGHT);
        } else {
            splitPane.setDividerLocation(0.6);
        }
        splitPane.revalidate();
        splitPane.repaint();
    }

    private void collapseBottomPanel() {
        bottomContentPanel.setPreferredSize(new Dimension(0, 0));
        splitPane.setDividerSize(COLLAPSED_DIVIDER_SIZE);
        splitPane.setDividerLocation(1.0);
        splitPane.revalidate();
        splitPane.repaint();
    }

    private void afterNoticeAdded() {
        loadDataFromDatabase();
        collapseBottomPanel();
    }

    private void afterNoticeDeleted() {
        loadDataFromDatabase();
        collapseBottomPanel();
    }
}
