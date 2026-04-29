package com.fot.system.view.dashboard.admin.components;

import com.fot.system.config.AppTheme;
import com.fot.system.model.entity.*;
import com.fot.system.view.shared_components.FeedItemCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * render scrollable recent notice feed in admin dashboard
 * @author janith
 */
public class NoticeFeedPanel extends JPanel {
    private static final int NOTICE_CARD_HEIGHT = 138;

    private final JPanel listPanel;
    private final JLabel titleLabel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * initialize notice feed panel
     * @param title panel title
     * @author janith
     */
    public NoticeFeedPanel(String title) {
        setLayout(new BorderLayout(0, 15));
        setBackground(AppTheme.CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false),
                new EmptyBorder(18, 18, 18, 18)
        ));

        titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.fontBold(20));

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppTheme.CARD_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * update panel title text
     * @param title title value
     * @author janith
     */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    /**
     * bind notice list to feed panel
     * @param notices notice items
     * @author janith
     */
    public void setNotices(List<Notice> notices) {
        listPanel.removeAll();

        if (notices == null || notices.isEmpty()) {
            JLabel emptyLabel = new JLabel("No notices available for this role.");
            emptyLabel.setFont(AppTheme.fontPlain(14));
            emptyLabel.setForeground(AppTheme.TEXT_SUBTLE);
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

    /**
     * create styled feed card for a notice item
     * @param notice notice item
     * @author janith
     */
    private JPanel createNoticeCard(Notice notice) {
        return new FeedItemCard(
                notice.getTitle(),
                truncate(notice.getContent(), 140),
                buildMeta(notice),
                priorityColor(notice.getPriority()),
                NOTICE_CARD_HEIGHT
        );
    }

    /**
     * build compact meta text for notice card
     * @param notice notice item
     * @author janith
     */
    private String buildMeta(Notice notice) {
        String published = notice.getPublishedDate() == null ? "-" : dateFormat.format(notice.getPublishedDate());
        String expiry = notice.getExpiryDate() == null ? "No expiry" : dateFormat.format(notice.getExpiryDate());
        String createdBy = notice.getCreatedByName() == null ? "-" : notice.getCreatedByName();
        return notice.getAudience() + "  |  Published: " + published + "  |  Expires: " + expiry + "  |  By: " + createdBy;
    }

    /**
     * resolve priority indicator color
     * @param priority notice priority
     * @author janith
     */
    private Color priorityColor(String priority) {
        if ("HIGH".equalsIgnoreCase(priority)) {
            return AppTheme.PRIORITY_HIGH;
        }
        if ("MEDIUM".equalsIgnoreCase(priority)) {
            return AppTheme.PRIORITY_MEDIUM;
        }
        return AppTheme.PRIORITY_LOW;
    }

    /**
     * trim and limit text length for preview
     * @param text source text
     * @param maxLength max allowed length
     * @author janith
     */
    private String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        String normalized = text.trim().replaceAll("\\s+", " ");
        if (normalized.length() <= maxLength) {
            return normalized;
        }
        return normalized.substring(0, maxLength - 3) + "...";
    }
}
