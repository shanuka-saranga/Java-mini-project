package com.fot.system.view.dashboard.lecturer.notice;

import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.service.NoticeService;
import com.fot.system.view.dashboard.admin.shared.NoticeFeedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class NoticePanel extends JPanel {
    private final User currentUser;
    private final NoticeService noticeService;
    private final NoticeFeedPanel noticeFeedPanel;

    public NoticePanel(User user) {
        this.currentUser = user;
        this.noticeService = new NoticeService();

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        add(createHeader(), BorderLayout.NORTH);

        noticeFeedPanel = new NoticeFeedPanel("Recent Notices");

        add(noticeFeedPanel, BorderLayout.CENTER);

        // දත්ත Load කරන්න
        loadNotices();
    }

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

    private void loadNotices() {
        SwingWorker<List<Notice>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Notice> doInBackground() {
                return noticeService.getRecentVisibleNoticesForRole(currentUser.getRole(), 15);
            }

            @Override
            protected void done() {
                try {
                    List<Notice> notices = get();
                    noticeFeedPanel.setNotices(notices);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                            NoticePanel.this,
                            "Failed to load notices.",
                            "System Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }
}