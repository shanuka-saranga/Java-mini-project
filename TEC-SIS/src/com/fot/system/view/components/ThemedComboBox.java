package com.fot.system.view.components;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import java.awt.*;

public class ThemedComboBox<T> extends JComboBox<T> {
    private static final int DEFAULT_HEIGHT = 34;

    public ThemedComboBox() {
        super();
        applyTheme();
    }

    public ThemedComboBox(T[] items) {
        super(items);
        applyTheme();
    }

    private void applyTheme() {
        setFont(AppTheme.FORM_INPUT_FONT);
        setForeground(AppTheme.TEXT_DARK);
        setBackground(AppTheme.BG_LIGHT);
        setBorder(AppTheme.lineBorder(AppTheme.BORDER_MUTED));
        setPreferredSize(new Dimension(0, DEFAULT_HEIGHT));
    }
}
