package com.fot.system.view.dashboard.sidebar;

import com.fot.system.config.AppConfig;
import com.fot.system.config.AppTheme;
import com.fot.system.view.dashboard.MainDashboard;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import javax.swing.*;
import java.awt.*;

public abstract class BaseSidebar extends JPanel {
    protected final MainDashboard parentFrame;

    public BaseSidebar(MainDashboard frame, String roleName) {
        this.parentFrame = frame;
        setupPanel();
        addHeader(roleName);
        addCommonButtons();
        addRoleSpecificButtons();
        addFooter();
    }

    private void setupPanel() {
        setPreferredSize(new Dimension(240, 0));
        setBackground(AppTheme.SIDEBAR_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    private void addHeader(String roleName) {
        add(Box.createVerticalStrut(30));
        JLabel title = new JLabel("TEC-SIS");
        title.setForeground(AppTheme.TEXT_LIGHT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(title);

        add(Box.createVerticalStrut(8));
        JLabel roleLabel = new JLabel(roleName + " Dashboard");
        roleLabel.setForeground(new Color(180, 180, 180));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(roleLabel);
        add(Box.createVerticalStrut(40));
    }

    private void addCommonButtons() {
        add(createMenuButton("Home", FontAwesomeSolid.HOME, AppConfig.MENU_HOME));
        add(Box.createVerticalStrut(10));
        add(createMenuButton("Profile", FontAwesomeSolid.USER, AppConfig.MENU_PROFILE));
        add(Box.createVerticalStrut(10));
    }

    protected abstract void addRoleSpecificButtons();

    private void addFooter() {
        add(Box.createVerticalGlue());
        JButton logoutBtn = createMenuButton("Logout", FontAwesomeSolid.SIGN_OUT_ALT, AppConfig.MENU_LOGOUT);
        add(logoutBtn);
        add(Box.createVerticalStrut(25));
    }

    protected JButton createMenuButton(String text, FontAwesomeSolid iconCode, String cardName) {

        JButton button = new JButton(text);

        FontIcon icon = FontIcon.of(iconCode, 18, AppTheme.TEXT_LIGHT);
        button.setIcon(icon);

        button.setMaximumSize(new Dimension(210, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.setFont(AppTheme.MENU_FONT);
        button.setForeground(AppTheme.TEXT_LIGHT);
        button.setBackground(AppTheme.PRIMARY);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setIconTextGap(15);

        button.addActionListener(e -> {
            if (!cardName.equals("Logout")) {
                parentFrame.switchPanel(cardName);
            }
        });

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(AppTheme.PRIMARY_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(AppTheme.PRIMARY);
            }
        });

        return button;
    }
}