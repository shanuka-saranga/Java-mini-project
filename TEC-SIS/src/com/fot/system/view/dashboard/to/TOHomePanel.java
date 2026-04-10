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

    private final User currentUser;
    private final AttendanceService attendanceService;
    private final NoticeService noticeService;

    private final DashboardStatCard pendingMedicalsCard;
    private final DashboardStatCard visibleNoticesCard;
    private final NoticeFeedPanel noticeFeedPanel;

    public TOHomePanel(User user) {
        this.currentUser = user;
        this.attendanceService = new AttendanceService();
        this.noticeService = new NoticeService();

        setBackground(AppTheme.SURFACE_SOFT);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        pendingMedicalsCard = new DashboardStatCard("Pending Medicals", "...", FontAwesomeSolid.FILE_MEDICAL);
        visibleNoticesCard = new DashboardStatCard("Visible Notices", "...", FontAwesomeSolid.BULLHORN);
        noticeFeedPanel = new NoticeFeedPanel("TO Notices");

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        loadDashboardData();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel subtitleLabel = new JLabel("Here is your technical officer overview with pending medical submissions and recent notices.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(AppTheme.TEXT_SUBTLE);

        header.add(welcomeLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setOpaque(false);

        JPanel statsGrid = new JPanel(new GridLayout(1, 2, 16, 0));
        statsGrid.setOpaque(false);
        statsGrid.add(pendingMedicalsCard);
        statsGrid.add(visibleNoticesCard);

        content.add(statsGrid, BorderLayout.NORTH);
        content.add(noticeFeedPanel, BorderLayout.CENTER);
        return content;
    }

    private void loadDashboardData() {
        SwingWorker<DashboardData, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardData doInBackground() {
                int pendingMedicals = attendanceService.getPendingMedicalSubmissionCount();
                int visibleNoticeCount = noticeService.getVisibleNoticeCountForRole(currentUser.getRole());
                List<Notice> visibleNotices = noticeService.getRecentVisibleNoticesForRole(currentUser.getRole(), 8);
                return new DashboardData(pendingMedicals, visibleNoticeCount, visibleNotices);
            }

            @Override
            protected void done() {
                try {
                    DashboardData data = get();
                    pendingMedicalsCard.setValue(String.valueOf(data.pendingMedicals));
                    visibleNoticesCard.setValue(String.valueOf(data.visibleNotices));
                    noticeFeedPanel.setNotices(data.notices);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            TOHomePanel.this,
                            "Failed to load technical officer dashboard data.",
                            "Dashboard Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private static class DashboardData {
        private final int pendingMedicals;
        private final int visibleNotices;
        private final List<Notice> notices;

        private DashboardData(int pendingMedicals, int visibleNotices, List<Notice> notices) {
            this.pendingMedicals = pendingMedicals;
            this.visibleNotices = visibleNotices;
            this.notices = notices;
        }
    }
}
