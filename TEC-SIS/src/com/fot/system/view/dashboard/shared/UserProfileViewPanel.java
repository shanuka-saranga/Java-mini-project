package com.fot.system.view.dashboard.shared;

import com.fot.system.config.AppTheme;
import com.fot.system.model.dto.*;
import com.fot.system.model.entity.*;
import com.fot.system.view.components.ProfilePhotoFrame;
import com.fot.system.view.components.ProfileSectionCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class UserProfileViewPanel extends JPanel implements Scrollable {
    private static final int STACK_BREAKPOINT = 920;
    private static final int PROFILE_CARD_WIDTH = 350;

    private final ProfilePhotoFrame photoFrame;
    private final JLabel lblName;
    private final JLabel lblEmail;
    private final JLabel lblRoleBadge;
    private final JLabel lblDepartment;
    private final JLabel lblStatus;
    private final JLabel lblAccessHint;

    private final JLabel lblAccountEmail;
    private final JLabel lblAccountRole;
    private final JLabel lblAccountDepartment;
    private final JLabel lblAccountStatus;
    private final JLabel lblPersonalDob;
    private final JLabel lblPersonalPhone;
    private final JLabel lblPersonalAddress;
    private final JLabel lblRolePrimary;
    private final JLabel lblRoleSecondary;
    private final ProfileSectionCard profileCard;
    private final ProfileSectionCard sideRoleCard;
    private final JPanel rightStack;
    private int currentMode = -1;

    public UserProfileViewPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());

        profileCard = new ProfileSectionCard("Profile Card", "Your basic profile summary.");
        JPanel profileContent = new JPanel();
        profileContent.setOpaque(false);
        profileContent.setLayout(new BoxLayout(profileContent, BoxLayout.Y_AXIS));

        JPanel photoWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        photoWrap.setOpaque(false);
        photoFrame = new ProfilePhotoFrame("No Image");
        photoWrap.add(photoFrame);

        lblName = createTitleLabel();
        lblEmail = createMetaLabel();
        lblRoleBadge = createBadgeLabel();
        lblDepartment = createMetaLabel();
        lblStatus = createMetaLabel();
        lblAccessHint = createMetaLabel();

        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        badgeWrap.setOpaque(false);
        badgeWrap.add(lblRoleBadge);

        profileContent.add(Box.createVerticalStrut(8));
        profileContent.add(photoWrap);
        profileContent.add(Box.createVerticalStrut(16));
        profileContent.add(lblName);
        profileContent.add(Box.createVerticalStrut(6));
        profileContent.add(lblEmail);
        profileContent.add(Box.createVerticalStrut(10));
        profileContent.add(badgeWrap);
        profileContent.add(Box.createVerticalStrut(16));
        profileContent.add(lblDepartment);
        profileContent.add(Box.createVerticalStrut(8));
        profileContent.add(lblStatus);
        profileContent.add(Box.createVerticalStrut(8));
        profileContent.add(lblAccessHint);
        profileContent.add(Box.createVerticalStrut(4));
        profileCard.setContent(profileContent);
        profileCard.setPreferredSize(new Dimension(PROFILE_CARD_WIDTH, 360));
        profileCard.setMinimumSize(new Dimension(PROFILE_CARD_WIDTH, 330));
        profileCard.setMaximumSize(new Dimension(PROFILE_CARD_WIDTH, Integer.MAX_VALUE));

        lblRolePrimary = createValueLabel();
        lblRoleSecondary = createValueLabel();
        sideRoleCard = new ProfileSectionCard(
                "Role Specific Information",
                "These values come from your academic or staff record and are shown here for reference."
        );
        sideRoleCard.setContent(createInfoStack(
                new String[]{"Primary Detail", "Secondary Detail"},
                new JLabel[]{lblRolePrimary, lblRoleSecondary}
        ));
        sideRoleCard.setPreferredSize(new Dimension(PROFILE_CARD_WIDTH, 220));
        sideRoleCard.setMinimumSize(new Dimension(PROFILE_CARD_WIDTH, 200));
        sideRoleCard.setMaximumSize(new Dimension(PROFILE_CARD_WIDTH, Integer.MAX_VALUE));

        rightStack = new JPanel(new BorderLayout(0, 18));
        rightStack.setOpaque(false);

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));

        lblAccountEmail = createValueLabel();
        lblAccountRole = createValueLabel();
        lblAccountDepartment = createValueLabel();
        lblAccountStatus = createValueLabel();
        ProfileSectionCard accountCard = new ProfileSectionCard(
                "Account Information",
                "These are the institutional details linked to your user account."
        );
        accountCard.setContent(createInfoStack(
                new String[]{"Email Address", "Role", "Department", "Status"},
                new JLabel[]{lblAccountEmail, lblAccountRole, lblAccountDepartment, lblAccountStatus}
        ));

        lblPersonalDob = createValueLabel();
        lblPersonalPhone = createValueLabel();
        lblPersonalAddress = createValueLabel();
        ProfileSectionCard personalCard = new ProfileSectionCard(
                "Personal Information",
                "This section shows the profile details available to you."
        );
        personalCard.setContent(createInfoStack(
                new String[]{"Date of Birth", "Phone Number", "Address"},
                new JLabel[]{lblPersonalDob, lblPersonalPhone, lblPersonalAddress}
        ));

        stack.add(accountCard);
        stack.add(Box.createVerticalStrut(16));
        stack.add(personalCard);
        stack.add(Box.createVerticalGlue());

        rightStack.add(stack, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                refreshLayoutMode();
            }
        });

        refreshLayoutMode();
    }

    public void bind(User user, String departmentName, String accessHint, String primaryRoleInfo, String secondaryRoleInfo, String dob, String phone, String address) {
        photoFrame.setImagePath(user.getProfilePicturePath());
        lblName.setText(user.getFullName());
        lblEmail.setText(user.getEmail());
        lblRoleBadge.setText(valueOrDash(user.getRole()));
        lblDepartment.setText("Department: " + valueOrDash(departmentName));
        lblStatus.setText("Account Status: " + valueOrDash(user.getStatus()));
        lblAccessHint.setText(accessHint);

        lblAccountEmail.setText(valueOrDash(user.getEmail()));
        lblAccountRole.setText(valueOrDash(user.getRole()));
        lblAccountDepartment.setText(valueOrDash(departmentName));
        lblAccountStatus.setText(valueOrDash(user.getStatus()));
        lblPersonalDob.setText(valueOrDash(dob));
        lblPersonalPhone.setText(valueOrDash(phone));
        lblPersonalAddress.setText(formatMultiline(address));
        lblRolePrimary.setText(valueOrDash(primaryRoleInfo));
        lblRoleSecondary.setText(valueOrDash(secondaryRoleInfo));
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(this::refreshLayoutMode);
    }

    private void refreshLayoutMode() {
        int width = getAvailableWidth();
        int mode = width > 0 && width < STACK_BREAKPOINT ? 1 : 2;
        if (mode == currentMode) {
            return;
        }

        currentMode = mode;
        removeAll();

        if (mode == 1) {
            JPanel stackLayout = new JPanel();
            stackLayout.setOpaque(false);
            stackLayout.setLayout(new BoxLayout(stackLayout, BoxLayout.Y_AXIS));
            profileCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            sideRoleCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            rightStack.setAlignmentX(Component.LEFT_ALIGNMENT);
            stackLayout.add(profileCard);
            stackLayout.add(Box.createVerticalStrut(16));
            stackLayout.add(sideRoleCard);
            stackLayout.add(Box.createVerticalStrut(16));
            stackLayout.add(rightStack);
            add(stackLayout, BorderLayout.CENTER);
        } else {
            JPanel splitLayout = new JPanel(new GridBagLayout());
            splitLayout.setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.NORTHWEST;

            gbc.gridx = 0;
            gbc.weightx = 0;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.VERTICAL;
            gbc.insets = new Insets(0, 0, 0, 18);
            splitLayout.add(createCompactProfileWrap(), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(0, 0, 0, 0);
            splitLayout.add(rightStack, gbc);

            add(splitLayout, BorderLayout.CENTER);
        }

        revalidate();
        repaint();
    }

    private int getAvailableWidth() {
        Container parent = getParent();
        if (parent instanceof JViewport) {
            return ((JViewport) parent).getExtentSize().width;
        }
        if (parent != null && parent.getParent() instanceof JViewport) {
            return ((JViewport) parent.getParent()).getExtentSize().width;
        }
        return Math.max(getWidth(), super.getPreferredSize().width);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 24;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return orientation == SwingConstants.VERTICAL ? visibleRect.height - 24 : visibleRect.width - 24;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    private JLabel createTitleLabel() {
        JLabel label = new JLabel("-");
        label.setFont(AppTheme.fontBold(24));
        label.setForeground(AppTheme.TEXT_DARK);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JLabel createMetaLabel() {
        JLabel label = new JLabel("-");
        label.setFont(AppTheme.fontPlain(13));
        label.setForeground(AppTheme.TEXT_SUBTLE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JLabel createBadgeLabel() {
        JLabel label = new JLabel("-");
        label.setOpaque(true);
        label.setBackground(AppTheme.BTN_EDIT_BG);
        label.setForeground(AppTheme.BTN_EDIT_FG);
        label.setBorder(new EmptyBorder(7, 14, 7, 14));
        label.setFont(AppTheme.fontBold(12));
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("-");
        label.setFont(AppTheme.fontBold(14));
        label.setForeground(AppTheme.TEXT_DARK);
        return label;
    }

    private JPanel createCompactProfileWrap() {
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setPreferredSize(new Dimension(PROFILE_CARD_WIDTH, 0));
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        profileCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        sideRoleCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(profileCard);
        wrap.add(Box.createVerticalStrut(12));
        wrap.add(sideRoleCard);
        wrap.add(Box.createVerticalGlue());
        return wrap;
    }

    private JPanel createInfoStack(String[] titles, JLabel[] valueLabels) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (int i = 0; i < titles.length; i++) {
            panel.add(createInfoRow(titles[i], valueLabels[i]));
            if (i < titles.length - 1) {
                panel.add(Box.createVerticalStrut(12));
            }
        }

        return panel;
    }

    private JPanel createInfoRow(String title, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout(0, 6));
        row.setBackground(AppTheme.SURFACE_MUTED);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.fontPlain(12));
        titleLabel.setForeground(AppTheme.TEXT_SUBTLE);

        row.add(titleLabel, BorderLayout.NORTH);
        row.add(valueLabel, BorderLayout.CENTER);
        return row;
    }

    private String formatMultiline(String value) {
        String text = valueOrDash(value);
        return "<html><body style='width:220px'>" + text + "</body></html>";
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
