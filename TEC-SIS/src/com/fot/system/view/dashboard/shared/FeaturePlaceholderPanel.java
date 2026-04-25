package com.fot.system.view.dashboard.shared;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FeaturePlaceholderPanel extends JPanel {

    public FeaturePlaceholderPanel(String title, String description) {
        setLayout(new BorderLayout());
        setBackground(AppTheme.SURFACE_SOFT);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, false),
                new EmptyBorder(24, 24, 24, 24)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.fontBold(28));
        titleLabel.setForeground(AppTheme.TEXT_DARK);

        JTextArea descriptionArea = new JTextArea(description);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setFont(AppTheme.fontPlain(15));
        descriptionArea.setForeground(AppTheme.TEXT_SUBTLE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descriptionArea, BorderLayout.CENTER);

        add(card, BorderLayout.NORTH);
    }
}
