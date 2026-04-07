package com.fot.system.view.dashboard.admin.shared;

import com.fot.system.config.AppTheme;
import com.fot.system.model.Notice;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class NoticeFeedPanel extends JPanel {

    private final JPanel listPanel;
    private final JLabel titleLabel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public NoticeFeedPanel(String title) {
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 235, 235), 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setNotices(List<Notice> notices) {
        listPanel.removeAll();

        if (notices == null || notices.isEmpty()) {
            JLabel emptyLabel = new JLabel("No notices available for this role.");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(120, 120, 120));
            emptyLabel.setBorder(new EmptyBorder(12, 8, 12, 8));
            listPanel.add(emptyLabel);
        } else {
            for (Notice notice : notices) {
                listPanel.add(createNoticeCard(notice));
                listPanel.add(Box.createVerticalStrut(12));
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createNoticeCard(Notice notice) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(new Color(248, 251, 251));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 233, 233), 1, true),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);

        JLabel title = new JLabel(notice.getTitle());
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel priorityBadge = new JLabel(notice.getPriority());
        priorityBadge.setOpaque(true);
        priorityBadge.setHorizontalAlignment(SwingConstants.CENTER);
        priorityBadge.setBorder(new EmptyBorder(4, 10, 4, 10));
        priorityBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        priorityBadge.setBackground(priorityColor(notice.getPriority()));
        priorityBadge.setForeground(Color.WHITE);

        JTextArea content = new JTextArea(notice.getContent());
        content.setEditable(false);
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        content.setOpaque(false);
        content.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        content.setForeground(new Color(70, 70, 70));

        JLabel meta = new JLabel(buildMeta(notice));
        meta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        meta.setForeground(AppTheme.TEXT_MUTED);

        header.add(title, BorderLayout.CENTER);
        header.add(priorityBadge, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        card.add(meta, BorderLayout.SOUTH);
        return card;
    }

    private String buildMeta(Notice notice) {
        String published = notice.getPublishedDate() == null ? "-" : dateFormat.format(notice.getPublishedDate());
        String expiry = notice.getExpiryDate() == null ? "No expiry" : dateFormat.format(notice.getExpiryDate());
        String createdBy = notice.getCreatedByName() == null ? "-" : notice.getCreatedByName();
        return notice.getAudience() + "  |  Published: " + published + "  |  Expires: " + expiry + "  |  By: " + createdBy;
    }

    private Color priorityColor(String priority) {
        if ("HIGH".equalsIgnoreCase(priority)) {
            return AppTheme.BTN_DELETE_BG;
        }
        if ("MEDIUM".equalsIgnoreCase(priority)) {
            return new Color(255, 152, 0);
        }
        return AppTheme.PRIMARY;
    }
}
