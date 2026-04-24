package com.fot.system.view.dashboard.to;

import com.fot.system.config.AppTheme;
import com.fot.system.model.Notice;
import com.fot.system.model.User;
import com.fot.system.service.AttendanceService;
import com.fot.system.service.NoticeService;
import com.fot.system.view.dashboard.admin.shared.DashboardStatCard;
import com.fot.system.view.dashboard.admin.shared.NoticeFeedPanel;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TOHomePanel extends JPanel {

    private final User user;
    private final AttendanceService attendanceService = new AttendanceService();
    private final NoticeService noticeService = new NoticeService();

    private final DashboardStatCard medicalCard =
            new DashboardStatCard("Pending Medicals", "...", FontAwesomeSolid.FILE_MEDICAL);

    private final DashboardStatCard noticeCard =
            new DashboardStatCard("Visible Notices", "...", FontAwesomeSolid.BULLHORN);

    private final NoticeFeedPanel noticeFeedPanel = new NoticeFeedPanel("TO Notices");

    public TOHomePanel(User user) {
        this.user = user;

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        add(createHeader(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);

        loadData();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Welcome back, " + user.getFullName() + "!");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel sub = new JLabel(
                "Technical officer overview with pending medical submissions and notices."
        );
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(AppTheme.TEXT_SUBTLE);

        panel.add(title, BorderLayout.NORTH);
        panel.add(sub, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBody() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        JPanel stats = new JPanel(new GridLayout(1, 2, 16, 0));
        stats.setOpaque(false);

        stats.add(medicalCard);
        stats.add(noticeCard);

        panel.add(stats, BorderLayout.NORTH);
        panel.add(noticeFeedPanel, BorderLayout.CENTER);

        return panel;
    }

    private void loadData() {
        new SwingWorker<DashboardData, Void>() {

            @Override
            protected DashboardData doInBackground() {
                int med = attendanceService.getPendingMedicalSubmissionCount();
                int notices = noticeService.getVisibleNoticeCountForRole(user.getRole());
                List<Notice> list =
                        noticeService.getRecentVisibleNoticesForRole(user.getRole(), 8);

                return new DashboardData(med, notices, list);
            }

            @Override
            protected void done() {
                try {
                    DashboardData d = get();
                    medicalCard.setValue(String.valueOf(d.medicals));
                    noticeCard.setValue(String.valueOf(d.notices));
                    noticeFeedPanel.setNotices(d.noticeList);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TOHomePanel.this,
                            "Failed to load dashboard data.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private record DashboardData(int medicals, int notices, List<Notice> noticeList) {}
}