package com.fot.system.view.dashboard.student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TimetableNoticePanel extends JPanel {

    private final Color cardColor = Color.WHITE;
    private final Color textDark = new Color(30, 30, 30);
    private final Color primaryDark = new Color(8, 110, 110);

    private JPanel noticesContainer;

    public TimetableNoticePanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(cardColor);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Important Notes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(textDark);

        noticesContainer = new JPanel();
        noticesContainer.setLayout(new BoxLayout(noticesContainer, BoxLayout.Y_AXIS));
        noticesContainer.setBackground(cardColor);

        JScrollPane scrollPane = new JScrollPane(noticesContainer);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setNotices(String day, List<String> notices) {
        noticesContainer.removeAll();

        if (notices == null || notices.isEmpty()) {
            noticesContainer.add(createNoticeItem("No special notices for " + day + "."));
        } else {
            for (String notice : notices) {
                noticesContainer.add(createNoticeItem(notice));
                noticesContainer.add(Box.createVerticalStrut(10));
            }
        }

        noticesContainer.revalidate();
        noticesContainer.repaint();
    }

    private JPanel createNoticeItem(String text) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(new Color(245, 250, 250));
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 230)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel iconLabel = new JLabel("•");
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        iconLabel.setForeground(primaryDark);

        JLabel textLabel = new JLabel("<html><div style='width:250px;'>" + text + "</div></html>");
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(textDark);

        item.add(iconLabel, BorderLayout.WEST);
        item.add(textLabel, BorderLayout.CENTER);

        return item;
    }
}