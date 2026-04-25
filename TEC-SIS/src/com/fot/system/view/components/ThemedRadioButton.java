package com.fot.system.view.components;

import com.fot.system.config.AppTheme;

import javax.swing.*;
import java.awt.*;

public class ThemedRadioButton extends JRadioButton {
    public ThemedRadioButton(String text) {
        this(text, false);
    }

    public ThemedRadioButton(String text, boolean selected) {
        super(text, selected);
        applyTheme();
    }

    private void applyTheme() {
        setFont(AppTheme.FORM_LABEL_FONT);
        setForeground(AppTheme.TEXT_DARK);
        setOpaque(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
