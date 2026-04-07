package com.fot.system.view.dashboard.admin.manageUsers;

import com.fot.system.config.AppTheme;
import com.fot.system.model.User;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;

public class AdminHomePanel extends JPanel {
    public AdminHomePanel(User user) {
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel welcomeLbl = new JLabel("Welcome back, " + user.getName() + "!");
        welcomeLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(welcomeLbl, BorderLayout.NORTH);
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.add(createStatCard("Total Users", "150", FontAwesomeSolid.USERS));
        statsPanel.add(createStatCard("Courses", "12", FontAwesomeSolid.BOOK_OPEN));
        statsPanel.add(createStatCard("Pending Notices", "5", FontAwesomeSolid.ENVELOPE_OPEN_TEXT));
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(statsPanel);

        add(wrapper, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String value, FontAwesomeSolid iconCode) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(250, 100));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 235, 235), 1, true), // ලස්සන Round border එකක්
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        FontIcon icon = FontIcon.of(iconCode, 35, AppTheme.PRIMARY);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(new Color(120, 120, 120));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(new Color(40, 40, 40));

        textPanel.add(lblTitle);
        textPanel.add(lblValue);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }
}