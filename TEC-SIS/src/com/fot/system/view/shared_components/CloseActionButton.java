package com.fot.system.view.shared_components;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CloseActionButton extends JButton {

    public CloseActionButton() {
        super("X");
        setFont(AppTheme.fontBold(18));
        setForeground(AppTheme.CLOSE_ACTION);
        setBorder(null);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(28, 28));
        setHorizontalAlignment(SwingConstants.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setForeground(AppTheme.CLOSE_ACTION_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setForeground(AppTheme.CLOSE_ACTION);
            }
        });
    }
}
