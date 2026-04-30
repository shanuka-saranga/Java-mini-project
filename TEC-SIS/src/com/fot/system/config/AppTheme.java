package com.fot.system.config;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class AppTheme {
    private AppTheme() {
    }

    // Primary Colors
    public static final Color PRIMARY = new Color(22, 97, 171);
    public static final Color BASE_COLOR = PRIMARY;
    public static final Color PRIMARY_HOVER = new Color(17, 84, 150);
    public static final Color PRIMARY_ACTIVE = new Color(13, 67, 122);

    // Background Colors
    public static final Color BG_LIGHT = new Color(255, 255, 255);
    public static final Color SIDEBAR_BG = PRIMARY;
    public static final Color SURFACE_MUTED = new Color(246, 248, 251);
    public static final Color SURFACE_SOFT = new Color(242, 245, 249);

    // Text Colors
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color TEXT_DARK = new Color(34, 40, 49);
    public static final Color TEXT_MUTED = new Color(141, 150, 161);
    public static final Color TEXT_SUBTLE = new Color(93, 102, 114);

    // Borders / Frames
    public static final Color BORDER_LIGHT = new Color(218, 225, 234);
    public static final Color BORDER_MUTED = new Color(201, 210, 221);
    public static final Color BORDER_SOFT = new Color(229, 234, 241);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color CARD_MUTED_BG = new Color(248, 250, 252);
    public static final Color CARD_BORDER = new Color(217, 225, 235);
    public static final Color AVATAR_BORDER = PRIMARY;
    public static final Color AVATAR_BG = new Color(246, 249, 253);
    public static final Color AVATAR_TEXT = new Color(98, 107, 120);
    public static final Color PRIORITY_HIGH = new Color(194, 41, 51);
    public static final Color PRIORITY_MEDIUM = new Color(216, 136, 32);
    public static final Color PRIORITY_LOW = PRIMARY;
    public static final Color ICON_ACCENT = PRIMARY;
    public static final Color CLOSE_ACTION = new Color(194, 41, 51);
    public static final Color CLOSE_ACTION_HOVER = new Color(175, 33, 42);
    public static final Color FILE_ICON_BG = new Color(233, 242, 252);
    public static final Color FILE_ICON_FG = PRIMARY;
    public static final Color ACTION_ICON_BG = new Color(242, 246, 251);
    public static final Color ACTION_ICON_HOVER = new Color(232, 240, 250);
    public static final Color ACTION_ICON_FG = PRIMARY;
    public static final Color ACTION_DELETE_ICON_BG = new Color(253, 238, 239);
    public static final Color ACTION_DELETE_ICON_HOVER = new Color(249, 222, 225);
    public static final Color ACTION_DELETE_ICON_FG = new Color(194, 41, 51);

    // Fonts
    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font TITLE_FONT = fontBold(22);
    public static final Font MENU_FONT = fontPlain(15);
    public static final Font LABEL_FONT = fontPlain(12);
    public static final Font LOGIN_TITLE_FONT = fontBold(26);
    public static final Font LOGIN_BUTTON_FONT = fontBold(16);
    public static final Font FORM_LABEL_FONT = fontPlain(14);
    public static final Font FORM_INPUT_FONT = fontPlain(15);
    public static final int CORNER_RADIUS = 0;

    public static Font fontPlain(int size) {
        return new Font(FONT_FAMILY, Font.PLAIN, size);
    }

    public static Font fontBold(int size) {
        return new Font(FONT_FAMILY, Font.BOLD, size);
    }

    // sidebar
    public static final int SIDEBAR_ICON_SIZE = 18;
    public static final int SIDEBAR_ICON_SLOT_WIDTH = 28;
    public static final int PROFILE_AVATAR_SIZE = 40;

    // table
    public static final Color TABLE_HEADER_BG = PRIMARY;
    public static final Color TABLE_HEADER_FG = Color.WHITE;
    public static final Color TABLE_ROW_BG = Color.WHITE;
    public static final Color TABLE_ROW_ALT_BG = new Color(248, 250, 252);
    public static final Color TABLE_SELECTION_BG = new Color(230, 240, 252);
    public static final Color TABLE_SELECTION_FG = new Color(20, 69, 133);

    // 1. Primary Button (Save / Confirm)
    public static final Color BTN_SAVE_BG = PRIMARY;
    public static final Color BTN_SAVE_FG = Color.WHITE;
    public static final Color BTN_SAVE_HOVER = PRIMARY_HOVER;

    // 2. Secondary Button (Edit / Update)
    public static final Color BTN_EDIT_BG = new Color(230, 240, 252);
    public static final Color BTN_EDIT_FG = PRIMARY;
    public static final Color BTN_EDIT_HOVER = new Color(213, 230, 249);

    // 3. Danger/Neutral Button (Cancel / Reset)
    public static final Color BTN_CANCEL_BG = new Color(113, 122, 135);
    public static final Color BTN_CANCEL_FG = Color.WHITE;
    public static final Color BTN_CANCEL_HOVER = new Color(95, 104, 116);

    // 4. Delete Button
    public static final Color BTN_DELETE_BG = new Color(194, 41, 51);
    public static final Color BTN_DELETE_FG = Color.WHITE;
    public static final Color BTN_DELETE_HOVER = new Color(175, 33, 42);

    public static Border lineBorder(Color color) {
        return BorderFactory.createLineBorder(color, 1, false);
    }

    public static Border inputBorder() {
        return BorderFactory.createCompoundBorder(
                lineBorder(BORDER_MUTED),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );
    }

    public static void applyGlobalTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        UIManager.put("Panel.background", BG_LIGHT);
        UIManager.put("Label.foreground", TEXT_DARK);
        UIManager.put("Button.background", new Color(238, 241, 245));
        UIManager.put("Button.foreground", TEXT_DARK);
        UIManager.put("Button.font", fontPlain(13));
        UIManager.put("Button.border", lineBorder(BORDER_MUTED));
        UIManager.put("OptionPane.background", BG_LIGHT);
        UIManager.put("OptionPane.messageForeground", TEXT_DARK);
        UIManager.put("OptionPane.buttonFont", fontPlain(13));
        UIManager.put("TextField.background", BG_LIGHT);
        UIManager.put("TextField.foreground", TEXT_DARK);
        UIManager.put("TextField.caretForeground", PRIMARY);
        UIManager.put("TextField.border", inputBorder());
        UIManager.put("PasswordField.border", inputBorder());
        UIManager.put("ComboBox.background", BG_LIGHT);
        UIManager.put("ComboBox.foreground", TEXT_DARK);
        UIManager.put("ComboBox.border", lineBorder(BORDER_MUTED));
        UIManager.put("ScrollPane.border", lineBorder(BORDER_LIGHT));
        UIManager.put("Table.gridColor", BORDER_SOFT);
        UIManager.put("Table.selectionBackground", TABLE_SELECTION_BG);
        UIManager.put("Table.selectionForeground", TABLE_SELECTION_FG);
    }

    public static void applyTableHeaderTheme(JTable table) {
        if (table == null || table.getTableHeader() == null) {
            return;
        }

        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TABLE_HEADER_FG);
        header.setFont(fontBold(13));
        header.setOpaque(true);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        renderer.setBackground(TABLE_HEADER_BG);
        renderer.setForeground(TABLE_HEADER_FG);
        renderer.setFont(fontBold(13));
        renderer.setBorder(BorderFactory.createLineBorder(PRIMARY_ACTIVE, 1, false));
        header.setDefaultRenderer(renderer);
    }
}
