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

    /**
     * initialize base sidebar with shared layout and controls
     * @param frame MainDashboard parent frame
     * @param roleName role display name
     * @author methum
     */
    public BaseSidebar(MainDashboard frame, String roleName) {
        this.parentFrame = frame;
        setupPanel();
        addHeader(roleName);
        addCommonButtons();
        addRoleSpecificButtons();
        addFooter();
    }

    /**
     * setup sidebar panel styles and layout
     * @author methum
     */
    private void setupPanel() {
        setPreferredSize(new Dimension(240, 0));
        setBackground(AppTheme.SIDEBAR_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    /**
     * add sidebar header with title and role label
     * @param roleName role display name
     * @author methum
     */
    private void addHeader(String roleName) {
        add(Box.createVerticalStrut(30));
        JLabel title = new JLabel("TEC-SIS");
        title.setForeground(AppTheme.TEXT_LIGHT);
        title.setFont(AppTheme.fontBold(24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(title);

        add(Box.createVerticalStrut(8));
        JLabel roleLabel = new JLabel(roleName + " Dashboard");
        roleLabel.setForeground(new Color(180, 180, 180));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(roleLabel);
        add(Box.createVerticalStrut(40));
    }

    /**
     * add common menu buttons for all roles
     * @author methum
     */
    private void addCommonButtons() {
        add(createMenuButton("Home", FontAwesomeSolid.HOME, AppConfig.MENU_HOME));
        add(Box.createVerticalStrut(10));
        add(createMenuButton("Profile", FontAwesomeSolid.USER, AppConfig.MENU_PROFILE));
        add(Box.createVerticalStrut(10));
    }

    /**
     * add role specific menu buttons from child sidebar classes
     * @author methum
     */
    protected abstract void addRoleSpecificButtons();

    /**
     * add sidebar footer with logout button
     * @author methum
     */
    private void addFooter() {
        add(Box.createVerticalGlue());
        JButton logoutBtn = createMenuButton("Logout", FontAwesomeSolid.SIGN_OUT_ALT, AppConfig.MENU_LOGOUT);
        add(logoutBtn);
        add(Box.createVerticalStrut(25));
    }

    /**
     * create styled sidebar menu button with icon and action
     * @param text button display text
     * @param iconCode font awesome icon code
     * @param cardName target card name
     * @author methum
     */
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
            if (AppConfig.MENU_LOGOUT.equals(cardName)) {
                parentFrame.logout();
            } else {
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
