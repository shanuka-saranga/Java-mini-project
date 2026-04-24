package com.fot.system.view.components;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SectionCard extends JPanel {

    private final JPanel contentPanel;

    public SectionCard(String title, String description) {
        setLayout(new BorderLayout(0, 10));
        setBackground(AppTheme.CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.fontBold(17));
        titleLabel.setForeground(AppTheme.TEXT_DARK);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(AppTheme.fontPlain(13));
        descriptionLabel.setForeground(AppTheme.TEXT_SUBTLE);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(descriptionLabel, BorderLayout.SOUTH);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        add(header, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void setContent(Component component) {
        contentPanel.removeAll();
        contentPanel.add(component, BorderLayout.CENTER);
    }
}
