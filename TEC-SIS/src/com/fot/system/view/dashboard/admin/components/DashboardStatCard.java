package com.fot.system.view.dashboard.admin.components;

import com.fot.system.config.AppTheme;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;

public class DashboardStatCard extends JPanel {

    private final JLabel valueLabel;

    public DashboardStatCard(String title, String initialValue, FontAwesomeSolid iconCode) {
        setLayout(new BorderLayout(15, 0));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(250, 100));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 235, 235), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel iconLabel = new JLabel(FontIcon.of(iconCode, 35, AppTheme.PRIMARY));
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.fontPlain(14));
        titleLabel.setForeground(new Color(120, 120, 120));

        valueLabel = new JLabel(initialValue);
        valueLabel.setFont(AppTheme.fontBold(24));
        valueLabel.setForeground(new Color(40, 40, 40));

        textPanel.add(titleLabel);
        textPanel.add(valueLabel);

        add(iconLabel, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
