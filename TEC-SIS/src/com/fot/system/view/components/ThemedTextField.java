package com.fot.system.view.components;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import java.awt.*;

public class ThemedTextField extends JTextField {
    private static final int DEFAULT_HEIGHT = 34;

    public ThemedTextField() {
        this(0);
    }

    public ThemedTextField(int columns) {
        super(columns);
        applyTheme();
    }

    private void applyTheme() {
        setFont(AppTheme.FORM_INPUT_FONT);
        setForeground(AppTheme.TEXT_DARK);
        setBackground(AppTheme.BG_LIGHT);
        setCaretColor(AppTheme.PRIMARY);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_MUTED),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        setPreferredSize(new Dimension(0, DEFAULT_HEIGHT));
    }
}
