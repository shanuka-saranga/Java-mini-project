package com.fot.system.view.dashboard.sidebar;

import com.fot.system.config.AppConfig;
import com.fot.system.config.AppTheme;
import com.fot.system.model.entity.User;
import com.fot.system.view.dashboard.MainDashboard;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseSidebar extends JPanel {

    protected final MainDashboard parentFrame;
    private final Map<String, JButton> menuButtons = new HashMap<>();
    private String activeCardName = AppConfig.MENU_HOME;

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
        add(createUserProfileCard());
        add(Box.createVerticalStrut(12));
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

        button.setIcon(createAlignedIcon(iconCode));

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

        menuButtons.put(cardName, button);
        applyMenuButtonStyle(button, AppConfig.MENU_HOME.equals(cardName), false);

        button.addActionListener(e -> {
            if (AppConfig.MENU_LOGOUT.equals(cardName)) {
                parentFrame.logout();
            } else {
                setActiveMenu(cardName);
                parentFrame.switchPanel(cardName);
            }
        });

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                applyMenuButtonStyle(button, cardName.equals(activeCardName), true);
            }

            public void mouseExited(MouseEvent evt) {
                applyMenuButtonStyle(button, cardName.equals(activeCardName), false);
            }
        });

        return button;
    }

    /**
     * set active sidebar menu item highlight
     * @param cardName active content card name
     * @author methum
     */
    public void setActiveMenu(String cardName) {
        if (cardName == null || cardName.isBlank()) {
            return;
        }

        JButton target = menuButtons.get(cardName);
        if (target == null) {
            return;
        }

        activeCardName = cardName;
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            boolean isActive = cardName.equals(entry.getKey());
            applyMenuButtonStyle(entry.getValue(), isActive, false);
        }
    }

    /**
     * apply visual style for default, hover and active menu states
     * @param button sidebar menu button
     * @param isActive whether this is selected menu
     * @param isHover whether hover state is active
     * @author methum
     */
    private void applyMenuButtonStyle(JButton button, boolean isActive, boolean isHover) {
        if (isActive) {
            button.setBackground(AppTheme.PRIMARY_ACTIVE);
            return;
        }
        button.setBackground(isHover ? AppTheme.PRIMARY_HOVER : AppTheme.PRIMARY);
    }

    /**
     * create fixed-width icon so all menu text starts at same x position
     * @param iconCode font awesome icon code
     * @author methum
     */
    private Icon createAlignedIcon(FontAwesomeSolid iconCode) {
        FontIcon baseIcon = FontIcon.of(iconCode, AppTheme.SIDEBAR_ICON_SIZE, AppTheme.TEXT_LIGHT);
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                int offsetX = x + (AppTheme.SIDEBAR_ICON_SLOT_WIDTH - baseIcon.getIconWidth()) / 2;
                baseIcon.paintIcon(c, g, offsetX, y);
            }

            @Override
            public int getIconWidth() {
                return AppTheme.SIDEBAR_ICON_SLOT_WIDTH;
            }

            @Override
            public int getIconHeight() {
                return baseIcon.getIconHeight();
            }
        };
    }

    /**
     * create compact profile card shown above logout button
     * @author methum
     */
    private JPanel createUserProfileCard() {
        User user = parentFrame.getCurrentUser();
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setMaximumSize(new Dimension(210, 68));
        card.setPreferredSize(new Dimension(210, 68));
        card.setBackground(AppTheme.PRIMARY_HOVER);
        card.setBorder(BorderFactory.createCompoundBorder(
                AppTheme.lineBorder(AppTheme.PRIMARY_ACTIVE),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComponent avatar = createCircleAvatar(user);
        avatar.setPreferredSize(new Dimension(AppTheme.PROFILE_AVATAR_SIZE, AppTheme.PROFILE_AVATAR_SIZE));
        avatar.setMinimumSize(new Dimension(AppTheme.PROFILE_AVATAR_SIZE,AppTheme.PROFILE_AVATAR_SIZE));
        avatar.setMaximumSize(new Dimension(AppTheme.PROFILE_AVATAR_SIZE, AppTheme.PROFILE_AVATAR_SIZE));

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(getDisplayName(user));
        nameLabel.setForeground(AppTheme.TEXT_LIGHT);
        nameLabel.setFont(AppTheme.fontBold(12));

        JLabel roleLabel = new JLabel(user == null || user.getRole() == null ? "-" : user.getRole());
        roleLabel.setForeground(AppTheme.TEXT_MUTED);
        roleLabel.setFont(AppTheme.fontPlain(11));

        details.add(Box.createVerticalGlue());
        details.add(nameLabel);
        details.add(Box.createVerticalStrut(2));
        details.add(roleLabel);
        details.add(Box.createVerticalGlue());

        card.add(avatar, BorderLayout.WEST);
        card.add(details, BorderLayout.CENTER);
        return card;
    }

    /**
     * create circular avatar component with profile image fallback
     * @param user current user
     * @author methum
     */
    private JComponent createCircleAvatar(User user) {
        String text = getInitial(user);
        Image image = resolveAvatarImage(user);
        return new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                int size = Math.min(getWidth(), getHeight()) - 2;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                Ellipse2D circle = new Ellipse2D.Double(x, y, size, size);

                g2.setColor(AppTheme.AVATAR_BG);
                g2.fill(circle);

                Shape previousClip = g2.getClip();
                g2.setClip(circle);
                if (image != null) {
                    drawCoverImage(g2, image, new Rectangle(x, y, size, size));
                }
                g2.setClip(previousClip);

                g2.setColor(AppTheme.TEXT_DARK);
                g2.draw(circle);

                if (image == null) {
                    g2.setFont(AppTheme.fontBold(20));
                    FontMetrics metrics = g2.getFontMetrics();
                    int textX = x + (size - metrics.stringWidth(text)) / 2;
                    int textY = y + ((size - metrics.getHeight()) / 2) + metrics.getAscent();
                    g2.drawString(text, textX, textY);
                }
                g2.dispose();
            }
        };
    }

    /**
     * resolve profile image if available on disk
     * @param user current user
     * @author methum
     */
    private Image resolveAvatarImage(User user) {
        if (user == null || user.getProfilePicturePath() == null || user.getProfilePicturePath().trim().isEmpty()) {
            return null;
        }
        String imagePath = user.getProfilePicturePath().trim();
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            return null;
        }
        return new ImageIcon(imagePath).getImage();
    }

    /**
     * draw image using cover mode inside avatar bounds
     * @param g2 graphics context
     * @param sourceImage image to draw
     * @param bounds draw bounds
     * @author methum
     */
    private void drawCoverImage(Graphics2D g2, Image sourceImage, Rectangle bounds) {
        int imageWidth = sourceImage.getWidth(null);
        int imageHeight = sourceImage.getHeight(null);
        if (imageWidth <= 0 || imageHeight <= 0) {
            return;
        }

        double scale = Math.max(
                (double) bounds.width / imageWidth,
                (double) bounds.height / imageHeight
        );
        int drawWidth = (int) Math.round(imageWidth * scale);
        int drawHeight = (int) Math.round(imageHeight * scale);
        int drawX = bounds.x + (bounds.width - drawWidth) / 2;
        int drawY = bounds.y + (bounds.height - drawHeight) / 2;
        g2.drawImage(sourceImage, drawX, drawY, drawWidth, drawHeight, null);
    }

    /**
     * get display name with basic fallback
     * @param user current user
     * @author methum
     */
    private String getDisplayName(User user) {
        if (user == null) {
            return "User";
        }
        String name = user.getFullName();
        if (name == null || name.trim().isEmpty()) {
            return user.getEmail() == null || user.getEmail().trim().isEmpty() ? "User" : user.getEmail();
        }
        return name;
    }

    /**
     * get avatar initial character
     * @param user current user
     * @author methum
     */
    private String getInitial(User user) {
        String name = getDisplayName(user);
        return name.isEmpty() ? "U" : String.valueOf(Character.toUpperCase(name.charAt(0)));
    }
}
