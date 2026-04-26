package com.fot.system.view.dashboard.lecturer.notice;

import com.fot.system.config.AppTheme;
import com.fot.system.model.entity.*;
import com.fot.system.service.NoticeService;
import com.fot.system.view.dashboard.admin.components.NoticeFeedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * display lecturer-visible notices in a dedicated notice board panel
 * @author janith
 */
public class NoticePanel extends JPanel {
    private static final int DEFAULT_NOTICE_LIMIT = 15;
    private static final String PANEL_TITLE = "Recent Notices";

    private final User currentUser;
    private final NoticeService noticeService;
    private final NoticeFeedPanel noticeFeedPanel;

    /**
     * initialize lecturer notice board panel
     * @param user logged-in lecturer user
     * @author janith
     */
    public NoticePanel(User user) {
        this.currentUser = user;
        this.noticeService = new NoticeService();

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        add(createHeader(), BorderLayout.NORTH);

        noticeFeedPanel = new NoticeFeedPanel(PANEL_TITLE);

        add(noticeFeedPanel, BorderLayout.CENTER);

        loadNotices();
    }

    /**
     * create the notice board page header
     * @author janith
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);

        JLabel titleLabel = new JLabel("Notice Board");
        titleLabel.setFont(AppTheme.fontBold(28));
        titleLabel.setForeground(AppTheme.TEXT_DARK);

        JLabel subtitleLabel = new JLabel("View all the latest institutional notices and announcements for your role.");
        subtitleLabel.setFont(AppTheme.fontPlain(14));
        subtitleLabel.setForeground(AppTheme.TEXT_SUBTLE);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);
        return header;
    }

    /**
     * load notices visible to the current lecturer role
     * @author janith
     */
    private void loadNotices() {
        SwingWorker<List<Notice>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Notice> doInBackground() {
                return noticeService.getRecentVisibleNoticesForRole(resolveCurrentRole(), DEFAULT_NOTICE_LIMIT);
            }

            @Override
            protected void done() {
                try {
                    List<Notice> notices = get();
                    noticeFeedPanel.setNotices(notices == null ? List.of() : notices);
                } catch (Exception e) {
                    handleLoadError();
                }
            }
        };
        worker.execute();
    }

    /**
     * resolve the current user role used to filter visible notices
     * @author janith
     */
    private String resolveCurrentRole() {
        return currentUser == null || currentUser.getRole() == null ? "" : currentUser.getRole();
    }

    /**
     * show a consistent error message when notice loading fails
     * @author janith
     */
    private void handleLoadError() {
        noticeFeedPanel.setNotices(List.of());
        JOptionPane.showMessageDialog(
                NoticePanel.this,
                "Failed to load notices.",
                "System Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
