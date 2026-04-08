package com.fot.system.view.components;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MaterialActionButton extends JButton {

    private final Color normalColor;
    private final Color hoverColor;
    private final int radius = 12;

    public MaterialActionButton(FontAwesomeSolid iconCode, Color iconColor, Color backgroundColor, Color hoverBackgroundColor, String toolTip) {
        this.normalColor = backgroundColor;
        this.hoverColor = hoverBackgroundColor;

        setIcon(FontIcon.of(iconCode, 14, iconColor));
        setToolTipText(toolTip);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(34, 34));
        setMinimumSize(new Dimension(34, 34));
        setMaximumSize(new Dimension(34, 34));
        setBackground(backgroundColor);
        setContentAreaFilled(false);
        setOpaque(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setBorder(null);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(normalColor);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}
